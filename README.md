# bukang

Spring Boot 4.1 / Java 25 백엔드 프로젝트입니다.

## 개발 환경 설정

저장소를 clone한 뒤 각자 컴퓨터에서 한 번씩 해야 하는 설정입니다.
로그인 정보와 토큰은 저장소에 공유되지 않으므로, 모든 팀원이 직접 설정해야 합니다.

### 1. 필요한 도구

- JDK 25 (Gradle toolchain이 사용)
- Docker Desktop (MySQL, Kafka, Elasticsearch, Redis 컨테이너 실행)
- Node.js (MCP 서버를 `npx`로 실행)
- Claude Code

### 2. git 사용자 정보

커밋 작성자로 쓰입니다. 자기 이름과 이메일로 설정합니다.

```bash
git config --global user.name "이름"
git config --global user.email "이메일@example.com"
```

### 3. 환경변수

`.mcp.json`에는 `${...}` 참조만 있고 실제 값은 없습니다. 필요한 값을 환경변수로 설정합니다.

| 변수 | 용도 | 필수 |
|---|---|---|
| `GITHUB_PERSONAL_ACCESS_TOKEN` | GitHub MCP (이슈, PR 조회) | GitHub MCP를 쓸 때 |
| `CONTEXT7_API_KEY` | Context7 MCP (라이브러리 문서 조회) | 선택 (없으면 제한된 호출량으로 동작) |
| `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_USER`, `MYSQL_PASS`, `MYSQL_DB` | MySQL MCP (읽기 전용 조회) | MySQL MCP를 쓸 때 (기본값 `127.0.0.1:3306`, `root`, `bukang`) |

Windows에서는 PowerShell에서 아래처럼 설정하고, 터미널과 IDE를 다시 시작합니다.

```powershell
setx GITHUB_PERSONAL_ACCESS_TOKEN "ghp_..."
```

### 4. MCP 서버 승인

프로젝트 루트에서 Claude Code를 처음 실행하면 `.mcp.json`에 있는 MCP 서버를 사용할지 묻습니다. 승인하면 됩니다.

| 서버 | 용도 |
|---|---|
| `context7` | 라이브러리 최신 문서 조회 |
| `github` | GitHub 이슈, PR 조회 |
| `playwright` | 브라우저 자동화 |
| `sequential-thinking` | 단계별 사고 보조 |
| `shrimp-task-manager` | 작업 계획, 관리 |
| `mysql` | MySQL 읽기 전용 조회 (로컬 MySQL이 실행 중이어야 함) |
| `linear` | Linear 이슈, 프로젝트 조회 |

### 5. Linear 로그인

Linear MCP는 OAuth로 인증합니다.

1. Claude Code에서 `/mcp`를 실행합니다.
2. `linear`를 선택하고 브라우저에서 Linear 계정으로 로그인합니다.
3. `programmers-05` 워크스페이스에 초대된 계정이어야 PRO 팀 이슈를 볼 수 있습니다.

### 6. 확인

`/mcp`에서 각 서버가 connected 상태인지 확인합니다. 실패한 서버가 있으면 해당 환경변수나 로컬 서비스(MySQL 등)를 확인합니다.

### 컨테이너 구성 (`compose.yml`)

| 서비스 | 용도 | 주소 |
|---|---|---|
| `bukang-db` | MySQL 8.4 | `localhost:3306` |
| `bukang-kafka` | Kafka 호환 브로커 (Redpanda) | `localhost:9092` |
| `bukang-kafka-console` | Kafka 관리 화면 (Redpanda Console) | http://localhost:8091 |
| `bukang-elasticsearch` | Elasticsearch 9.4.5 + nori 한글 분석기 | `localhost:9200` |
| `bukang-elasticvue` | Elasticsearch 관리 화면 (Elasticvue) | http://localhost:8090 |
| `bukang-redis` | Redis 8.2 | `localhost:6379` |
| `bukang` | Spring 앱 (prod 프로파일) | http://localhost:8080 |

`bukang`(앱) 서비스는 `app` 프로파일로 분리되어 있어서 `--profile app`을 붙일 때만 실행됩니다.

### 개발 실행 (앱 컨테이너 제외)

IDE나 `bootRun`으로 앱을 실행하면 Spring Boot가 `compose.yml`의 인프라 컨테이너를 자동으로 띄웁니다. 이미 실행 중이면 그대로 사용합니다. 앱이 직접 띄운 컨테이너는 앱을 종료할 때 함께 멈춥니다.

`docker/elasticsearch/Dockerfile`을 수정했다면 `docker compose up -d --build`로 이미지를 다시 만듭니다.

### 운영(prod) 앱 컨테이너 실행

앱을 운영용 이미지로 빌드해서 인프라와 함께 컨테이너로 실행합니다.

1. `.env.example`을 복사해 `.env`를 만들고 값을 채웁니다. 접속 정보는 `compose.yml`의 `bukang-db` 설정과 맞춥니다.

   ```bash
   cp .env.example .env
   ```

   | 변수 | 값 |
   |---|---|
   | `CRYPTO_HMAC_KEY` | `openssl rand -base64 32`로 생성 (아래 [민감정보 암호화](#민감정보-암호화) 참고) |
   | `CRYPTO_PASSWORD` | `openssl rand -base64 32`로 생성 |
   | `CRYPTO_SALT` | `openssl rand -hex 8`로 생성 |
   | `MYSQL_USER` / `MYSQL_PASS` / `MYSQL_DB` | `compose.yml`의 `bukang-db` 값과 동일 |
   | `JWT_SECRET` | 32자 이상의 임의 문자열 |

2. IDE나 `bootRun`으로 실행 중인 앱이 있으면 종료합니다. 같은 8080 포트를 사용합니다.

3. 앱 이미지를 빌드하고 전체 컨테이너를 실행합니다.

   ```bash
   docker compose --profile app up -d --build   # 앱 + 인프라 실행 (인프라가 healthy가 된 뒤 앱 시작)
   docker compose logs -f bukang                # 앱 로그 확인 ("Started BukangApplication"이면 성공)
   docker compose --profile app down            # 앱 포함 전체 중지 + 삭제
   ```

앱 컨테이너는 Docker 내부 서비스 이름(`bukang-db`, `bukang-kafka:29092` 등)으로 인프라에 접속하므로, `.env`의 주소에는 `localhost` 대신 서비스 이름을 사용합니다. 운영 프로파일에서는 Swagger가 비활성화됩니다.

## 민감정보 암호화

복호화가 필요한 개인정보(현재 휴대폰 번호)는 DB에 **AES-256-GCM 암호문**으로 저장합니다.
그 값으로 조회하거나 중복을 검사해야 하면 **HMAC-SHA256 해시(블라인드 인덱스)**를 별도 컬럼에 함께 저장합니다.
비밀번호는 복호화할 필요가 없으므로 이 방식이 아니라 `PasswordEncoder`(BCrypt)로 해시합니다.

| 컬럼 | 저장 값 | 용도 |
|---|---|---|
| `phone` | AES 암호문 (같은 값도 매번 다른 암호문) | 꺼내서 보여 주거나 발송할 때 (복호화) |
| `phone_hash` | HMAC 해시 (같은 값이면 항상 같은 해시, unique) | 조회, 중복 검사 (`WHERE phone_hash = ?`) |

### 구성 요소 (`global/config/crypto`)

| 클래스 | 역할 |
|---|---|
| `CryptoConfig` | `crypto.password`, `crypto.salt`로 `TextEncryptor`(AES-256-GCM) 빈을 한 번만 생성 |
| `Base64TextEncryptor` | 암호화 결과(바이트)를 Base64 문자열로 바꿔 DB 컬럼에 저장할 수 있게 함 |
| `EncryptedStringConverter` | JPA `AttributeConverter`. DB에 저장할 때 암호화하고, 조회할 때 복호화 |
| `BlindIndexGenerator` | `crypto.hmac-secret`으로 조회용 해시 생성. 필드별 메서드에서 정규화 후 해시 (`generatePhone`은 숫자만 남김) |

### 키 설정

| 설정 키 | dev / test | prod | 생성 명령 |
|---|---|---|---|
| `crypto.password` | `application-dev.yaml`, `application-test.yaml` | `.env`의 `CRYPTO_PASSWORD` | `openssl rand -base64 32` |
| `crypto.salt` | 〃 | `.env`의 `CRYPTO_SALT` | `openssl rand -hex 8` (16진수만 가능) |
| `crypto.hmac-secret` | 〃 | `.env`의 `CRYPTO_HMAC_KEY` | `openssl rand -base64 32` |

- dev/test 키는 개발용이라 yaml에 들어 있습니다. prod 키는 dev와 **다른 값**으로 만들고 `.env`에만 둡니다.
- 세 키는 서로 다른 값이어야 합니다. `JWT_SECRET`과도 같은 값을 쓰지 않습니다.
- `openssl`은 Git Bash에 기본으로 들어 있습니다.

### 새 필드에 적용하는 방법

1. 엔티티 필드에 `@Convert`를 붙입니다. 암호문이 원문보다 길어지므로 컬럼 길이를 넉넉히 잡습니다.

   ```java
   @Convert(converter = EncryptedStringConverter.class)
   @Column(length = 255)
   private String phone;
   ```

2. 그 값으로 조회하거나 중복을 검사해야 하면 해시 컬럼을 추가하고, `BlindIndexGenerator`에 필드 전용 메서드를 만듭니다.
   정규화 방식(예: 숫자만 남기기, 소문자로 바꾸기)과 필드 이름 접두사(`"phone:"`)를 필드마다 정합니다.

   ```java
   @Column(name = "phone_hash", unique = true, length = 64)
   private String phoneHash;
   ```

3. 서비스(`app` 계층)에서 해시를 계산해 엔티티에 원문과 함께 넘깁니다. 원문은 평문 그대로 넘기면 저장할 때 자동으로 암호화됩니다.

   ```java
   String phoneHash = blindIndexGenerator.generatePhone(phone);
   if (memberRepository.existsByPhoneHash(phoneHash)) {
   	throw new DuplicatePhoneException("이미 사용 중인 전화번호입니다.");
   }
   new Member(username, email, passwordEncoder.encode(password), nickname, phone, phoneHash);
   ```

4. 값을 바꾸는 메서드도 원문과 해시를 **함께** 받아 둘 다 바꿉니다. 해시는 자동으로 갱신되지 않습니다.

### 주의 사항

- **키를 바꾸면 기존 데이터를 읽을 수 없습니다.** 데이터가 쌓인 뒤에는 세 키 모두 바꾸지 않습니다. prod 키는 `.env` 외에 안전한 곳에도 백업합니다.
- **암호화한 필드로 직접 조회하지 않습니다.** `findByPhone(...)`은 결과가 나오지 않습니다(암호문이 매번 다름). 반드시 해시 컬럼으로 조회합니다.
- **엔티티 안에서는 항상 평문입니다.** `getPhone()`은 복호화된 원문을 반환하므로, API 응답 DTO에 그대로 넣거나 로그·예외 메시지에 남기지 않습니다. 화면에 보여 줄 때는 필요하면 마스킹합니다.
- **native query와 JDBC는 Converter를 거치지 않습니다.** 직접 SQL로 다루면 암호화/복호화가 일어나지 않습니다.
- **DB를 직접 조회하면 암호문이 보이는 게 정상입니다.**
- 주민등록번호는 법령 근거 없이 수집할 수 없습니다(개인정보 보호법 제24조의2). 본인 확인이 필요하면 본인인증의 CI 값을 같은 방식으로 저장합니다.

## API 문서 (Swagger)

### 접속

| 주소 | 내용 |
|---|---|
| http://localhost:8080/swagger-ui.html | Swagger UI (dev 프로파일에서만 열림, prod에서는 비활성화) |
| http://localhost:8080/v3/api-docs | OpenAPI 문서 원본(JSON) |

### 작성 규칙

컨트롤러와 요청 DTO의 Swagger 애노테이션은 [`.claude/rules/swagger-convention.md`](.claude/rules/swagger-convention.md)를 따릅니다.
참고 구현은 `boundedcontext/member/in/ApiV1AuthController.java`와 `AuthApiExamples.java`입니다.

- 컨트롤러에 `@Tag`, 엔드포인트마다 `@Operation`과 `@ApiResponses`를 붙입니다.
- 응답 코드는 그 API가 **실제로 반환하는 코드만** 적습니다. 생성 API의 201은 springdoc이 추론하지 못하므로 직접 명시합니다.
- 응답 예시는 `{Tag}ApiExamples` 상수 클래스에 모으고, `message`는 코드의 메시지 문자열을 그대로 씁니다.
  예외 메시지나 검증 메시지를 바꾸면 예시도 함께 바꿉니다.
- 요청 DTO의 모든 필드에 `@Schema(description, example)`를 붙입니다.

참고 구현에서 따르는 것은 애노테이션 구성 방식뿐입니다. 응답 코드와 에러는 각 컨트롤러의 실제 코드를 기준으로 작성합니다.

### Claude Code 스킬: `/swagger-annotate`

위 규칙대로 Swagger 애노테이션을 작성해 주는 프로젝트 스킬입니다 (`.claude/skills/swagger-annotate`).

```
/swagger-annotate ApiV1MemberController
```

슬래시 명령 대신 "ApiV1MemberController에 Swagger 달아줘", "API 문서화해줘"처럼 요청해도 됩니다.
대상을 적지 않으면 IDE에서 열려 있는 컨트롤러를 대상으로 합니다.

스킬은 다음 순서로 진행합니다.

1. **실제 응답 조사:** 성공 반환 코드, 호출 흐름에서 던지는 예외와 메시지, `GlobalExceptionHandler`의 매핑,
   요청 검증, `WebConfig`의 인증 필요 여부를 확인합니다.
2. **작성:** `@Tag`, `@Operation`, `@ApiResponses`, 응답 예시 상수 클래스, 요청 DTO의 `@Schema`를 작성합니다.
   비즈니스 로직, 매핑 경로, 메서드 시그니처는 바꾸지 않습니다.
3. **검사:** checkstyle 경고가 0건인지 확인하고, 앱을 띄워 `/v3/api-docs`의 예시가 실제 응답과 같은지 확인합니다.
   8080을 IDE가 쓰고 있으면 18080 포트로 따로 띄우고, 확인 후 종료합니다.
4. **보고:** 문서화한 응답 코드와 예시, 바뀐 파일, 조사 중 발견한 문제(핸들러가 없어 500으로 나가는 예외 등)를 알려 줍니다.

스킬은 커밋하지 않습니다. 결과를 확인한 뒤 직접 커밋하거나 커밋을 요청합니다.
