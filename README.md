# bukang

Spring Boot 4.1 / Java 25 백엔드 프로젝트입니다.

## 개발 환경 설정

저장소를 clone한 뒤 각자 컴퓨터에서 한 번씩 해야 하는 설정입니다.
로그인 정보와 토큰은 저장소에 공유되지 않으므로, 모든 팀원이 직접 설정해야 합니다.

### 1. 필요한 도구

- JDK 25 (Gradle toolchain이 사용)
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

## 빌드 및 실행

```bash
./gradlew build     # 컴파일 + 테스트 + 패키징
./gradlew bootRun   # 애플리케이션 실행
./gradlew test      # 전체 테스트
```

Windows 명령 프롬프트에서는 `gradlew.bat`을 사용합니다.
