# 누가 썼게? 백엔드

React 클라이언트에 JSON REST API를 제공하는 Spring Boot 백엔드입니다.

## 기술

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Flyway

## PostgreSQL 실행 설정

IntelliJ 실행 구성의 환경변수에 다음 값을 등록합니다.

```text
DB_URL=jdbc:postgresql://YOUR_NEON_HOST/neondb?sslmode=require
DB_USERNAME=YOUR_NEON_USERNAME
DB_PASSWORD=YOUR_NEON_PASSWORD
CORS_ALLOWED_ORIGINS=http://localhost:5173
```

비밀번호를 `application.properties`나 Git에 직접 올리지 않습니다.

애플리케이션이 시작되면 Flyway가 `src/main/resources/db/migration`의 SQL을 실행합니다.

## PostgreSQL 없이 화면 테스트

Neon을 연결하기 전에는 메모리 DB를 사용하는 `demo` 프로필로 실행할 수 있습니다.

IntelliJ의 Active profiles에 `demo`를 입력하거나 다음 명령을 사용합니다.

```bash
./gradlew bootRun --args='--spring.profiles.active=demo'
```

Windows:

```powershell
.\\gradlew.bat bootRun --args='--spring.profiles.active=demo'
```

데모 데이터는 서버를 종료하면 사라집니다. 실제 서비스에서는 기본 프로필과 PostgreSQL을 사용합니다.

## 주요 API

- `POST /api/trees`: 트리 생성
- `GET /api/trees/{slug}`: 공개 트리 조회
- `GET /api/trees/{slug}/owner`: 주인용 트리 조회
- `POST /api/trees/{slug}/letters`: 편지 등록
- `POST /api/trees/{slug}/letters/{letterId}/guess`: 작성자 추리
- `DELETE /api/trees/{slug}/letters/{letterId}`: 편지 삭제
- `GET /api/health`: 서버 상태 확인

주인용 API에는 `X-Tree-Admin-Key` 헤더가 필요합니다.

## 테스트

```powershell
.\\gradlew.bat test
```
