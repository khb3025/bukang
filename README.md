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
