# CLAUDE.md

이 파일은 이 저장소에서 작업하는 Claude Code(claude.ai/code)를 위한 가이드입니다.

## 프로젝트 개요

`bukang`은 Spring Initializr로 생성한 직후 상태의 Spring Boot 백엔드 프로젝트입니다. 현재는 진입점(`com.bukang.BukangApplication`)과 컨텍스트 로딩 테스트만 있고, 도메인 코드는 아직 없습니다.

- Java 25 (Gradle toolchain), Spring Boot 4.1.1, Gradle 9.7.1 (Kotlin DSL)
- 루트 패키지: `com.bukang` (`group`은 `com`)
- git 저장소로 초기화되어 있지 않음

## 빌드 및 실행 명령어

Windows에서는 `gradlew.bat`, Git Bash에서는 `./gradlew`를 사용합니다.

```bash
./gradlew build                 # 컴파일 + 테스트 + 패키징
./gradlew bootRun               # 애플리케이션 실행 (devtools 적용)
./gradlew test                  # 전체 테스트
./gradlew test --tests "com.bukang.BukangApplicationTests"            # 단일 테스트 클래스
./gradlew test --tests "com.bukang.BukangApplicationTests.contextLoads" # 단일 테스트 메서드
./gradlew clean
```

## 의존성 구성 (build.gradle.kts)

Spring Boot 4의 모듈화된 스타터를 사용합니다. 테스트 지원도 스타터별 `*-test` 모듈로 나뉘어 있습니다.

| 영역 | 의존성 |
|---|---|
| Web | `spring-boot-starter-webmvc` (서블릿 기반 MVC) |
| 보안 | `spring-boot-starter-security` |
| RDB | `spring-boot-starter-data-jpa` + MySQL 드라이버, H2(인메모리) + `spring-boot-h2console` |
| 검색 | `spring-boot-starter-data-elasticsearch` |
| 메시징 | `spring-boot-starter-kafka` |
| 기타 | Lombok, devtools |

## 설정 관련 주의사항

- `src/main/resources/application.yaml`에는 `spring.application.name`만 있습니다. 데이터소스, Elasticsearch, Kafka 접속 정보는 아직 설정되지 않아 Spring Boot 기본값(H2 인메모리 DB, `localhost` 기본 포트의 Elasticsearch/Kafka)으로 동작합니다.
- MySQL을 쓰려면 `spring.datasource.*` 설정을 추가해야 합니다. 프로파일별 설정(예: `application-local.yaml`)으로 H2와 MySQL을 나누는 방식을 권장합니다.
- Spring Security가 클래스패스에 있으므로 별도 `SecurityFilterChain` 빈이 없으면 모든 엔드포인트에 기본 인증이 걸리고, H2 콘솔도 막힙니다. API나 H2 콘솔을 열 때는 보안 설정을 함께 추가해야 합니다.

## 코드 컨벤션

코드 컨벤션은 `.claude/rules/`에 있습니다 (자동으로 로드됨).

- `java-convention.md`: Java 코딩 컨벤션 (캠퍼스 핵데이) 및 Checkstyle 검사
- `git-commit-convention.md`: 커밋 메시지 규칙 (Conventional Commits 1.0.0)
