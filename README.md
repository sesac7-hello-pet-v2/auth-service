# Hello Pet v2 - Authentication Service

JWT 기반 인증/인가를 담당하는 마이크로서비스입니다.

## 📌 개요

Hello Pet v2의 Auth Service는 사용자 인증과 JWT 토큰 관리를 담당합니다. 헥사고날 아키텍처(포트와 어댑터 패턴)를 적용하여 비즈니스 로직과 인프라스트럭처를 분리하고, 유지보수성과 테스트 용이성을 향상시켰습니다.

## 🚀 기술 스택

### Core Framework
- **Java**: 17
- **Spring Boot**: 3.5.6
- **Spring Cloud**: 2025.0.0
- **Build Tool**: Gradle 8.5

### Dependencies
- **데이터베이스**: PostgreSQL + Spring Data JPA
- **인증**: JWT (jjwt 0.12.6)
- **통신**: OpenFeign (마이크로서비스 간 통신)
- **모니터링**: Spring Boot Actuator
- **메트릭**: Micrometer + Prometheus
- **트레이싱**: OpenTelemetry
- **유틸리티**: Lombok
- **검증**: Spring Validation

## 🏗️ 아키텍처

### 헥사고날 아키텍처 (Ports & Adapters)

```
┌──────────────────────────────────────────────────┐
│                   Adapters (외부)                  │
├────────────────┬──────────────┬──────────────────┤
│   Web Layer    │   Database   │  External APIs   │
│  (Controller)  │   (JPA)      │   (OpenFeign)    │
└────────┬───────┴──────┬───────┴─────────┬────────┘
         │              │                  │
    ┌────▼──────────────▼──────────────────▼────┐
    │              Ports (인터페이스)             │
    │  - In Ports (Use Cases)                   │
    │  - Out Ports (Repositories/Clients)       │
    └────────────────────┬───────────────────────┘
                         │
              ┌──────────▼──────────┐
              │   Domain (핵심)     │
              │  - Entities         │
              │  - Business Logic   │
              └────────────────────┘
```

## 📁 프로젝트 구조

```
auth-service/
├── src/main/java/hello/pet/authservice/
│   ├── adapter/                          # 외부 어댑터
│   │   ├── in/                          # 인바운드 어댑터
│   │   │   └── web/                     # REST 컨트롤러
│   │   │       ├── AuthController.java
│   │   │       ├── config/
│   │   │       │   └── CookieFactory.java
│   │   │       └── dto/
│   │   │           ├── LoginRequest.java
│   │   │           └── RefreshTokenResponse.java
│   │   ├── out/                         # 아웃바운드 어댑터
│   │   │   ├── auth/
│   │   │   │   └── AuthAdapter.java
│   │   │   ├── persistence/             # 데이터베이스
│   │   │   │   ├── RefreshTokenJpaRepository.java
│   │   │   │   └── RefreshTokenRepositoryImpl.java
│   │   │   ├── security/                # 보안
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   └── TokenHash.java
│   │   │   └── AuthClient.java          # Feign 클라이언트
│   │   └── config/
│   │       └── OpenFeignConfig.java
│   ├── application/                      # 애플리케이션 계층
│   │   ├── port/                        # 포트 정의
│   │   │   ├── in/                      # 인바운드 포트
│   │   │   │   ├── LoginUseCase.java
│   │   │   │   ├── LogoutUseCase.java
│   │   │   │   ├── RefreshTokenUseCase.java
│   │   │   │   └── command/
│   │   │   │       ├── LoginCommand.java
│   │   │   │       ├── LogoutCommand.java
│   │   │   │       └── RefreshCommand.java
│   │   │   └── out/                     # 아웃바운드 포트
│   │   │       ├── repository/
│   │   │       └── result/
│   │   │           ├── LoginResult.java
│   │   │           ├── LogoutResult.java
│   │   │           └── RefreshResult.java
│   │   ├── service/                      # 유스케이스 구현
│   │   └── exception/                    # 예외 처리
│   │       ├── GlobalExceptionHandler.java
│   │       ├── InvalidRefreshTokenException.java
│   │       └── LoginCredentialException.java
│   └── domain/                           # 도메인 계층
│       ├── entity/
│       ├── vo/
│       └── repository/
├── src/main/resources/
│   └── application.yaml                  # 설정 파일
├── Dockerfile                             # Docker 이미지
├── build.gradle                           # 빌드 설정
└── README.md
```

## 🔧 주요 기능

### 1. 사용자 인증
- **로그인**: 이메일/비밀번호 검증
- **토큰 발급**: Access Token + Refresh Token
- **쿠키 관리**: Secure, HttpOnly 쿠키 설정

### 2. 토큰 관리
- **JWT 생성**: 사용자 정보 클레임 포함
- **토큰 검증**: 서명 및 만료 시간 검증
- **토큰 갱신**: Refresh Token으로 Access Token 재발급
- **토큰 해시**: Refresh Token SHA-256 해싱 저장

### 3. 세션 관리
- **로그아웃**: Refresh Token 무효화
- **다중 디바이스**: 디바이스별 독립적인 세션
- **토큰 저장**: PostgreSQL 기반 영구 저장

### 4. 보안
- **비밀번호 암호화**: BCrypt 해싱
- **토큰 보안**: HS512 알고리즘
- **CORS 처리**: Gateway에서 중앙 관리

## 🔑 API 엔드포인트

### 인증 API

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/v1/auth/login` | 사용자 로그인 | ❌ |
| POST | `/v1/auth/logout` | 사용자 로그아웃 | ✅ |
| POST | `/v1/auth/refresh` | 토큰 갱신 | ❌ (Refresh Token) |
| GET | `/v1/auth/validate` | 토큰 유효성 검증 | ✅ |
| GET | `/v1/auth/health` | 헬스 체크 | ❌ |

### 요청/응답 예시

**로그인 요청:**
```json
POST /v1/auth/login
{
  "email": "user@example.com",
  "password": "password123"
}
```

**로그인 응답:**
```json
{
  "userId": 1,
  "email": "user@example.com",
  "nickname": "홍길동",
  "role": "USER",
  "accessToken": "eyJhbGciOiJIUzUxMi...",
  "refreshToken": "eyJhbGciOiJIUzUxMi..."
}
```

## 🚦 시작하기

### 사전 요구사항
- JDK 17 이상
- Gradle 8.5 이상
- PostgreSQL 14 이상

### 로컬 개발 환경

1. **PostgreSQL 설정**
```bash
# PostgreSQL 실행
docker run -d \
  --name postgres-auth \
  -p 5432:5432 \
  -e POSTGRES_DB=db-auth \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  postgres:14-alpine
```

2. **프로젝트 클론**
```bash
git clone <repository-url>
cd auth-service
```

3. **빌드**
```bash
./gradlew clean build
```

4. **실행**
```bash
./gradlew bootRun
```

### 환경 변수

```yaml
# 데이터베이스
SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/db-auth
SPRING_DATASOURCE_USERNAME: postgres
SPRING_DATASOURCE_PASSWORD: postgres

# JWT 설정
JWT_SECRET: your-256-bit-secret
JWT_ACCESS_EXPIRATION: 900000     # 15분 (밀리초)
JWT_REFRESH_EXPIRATION: 86400000  # 24시간 (밀리초)

# 서버 포트
SERVER_PORT: 8081
```

## 🐳 Docker

### 이미지 빌드
```bash
docker build -t hello-pet-auth .
```

### 컨테이너 실행
```bash
docker run -d \
  --name auth-service \
  -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/db-auth \
  -e JWT_SECRET=your-secret-key \
  hello-pet-auth
```

### Multi-stage Build
- **Stage 1**: Gradle 빌드 환경
  - gradle:8.5-jdk17-alpine
  - 소스 코드 컴파일 및 JAR 생성
- **Stage 2**: 실행 환경
  - eclipse-temurin:17-jre-alpine
  - 최소화된 런타임 이미지

## 🗄️ 데이터베이스 스키마

### refresh_tokens 테이블
```sql
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_token_hash (token_hash),
    INDEX idx_expires_at (expires_at)
);
```

## 📊 모니터링

### Actuator 엔드포인트
```bash
# 헬스 체크
GET http://localhost:8081/actuator/health

# Prometheus 메트릭
GET http://localhost:8081/actuator/prometheus

# 애플리케이션 정보
GET http://localhost:8081/actuator/info
```

### 주요 메트릭
- 로그인 성공/실패 카운트
- 토큰 갱신 카운트
- JWT 생성/검증 시간
- 데이터베이스 연결 풀 상태

## 🔒 보안 고려사항

### JWT 보안
- **시크릿 키**: 최소 256비트 이상
- **토큰 만료**: Access Token 15분, Refresh Token 24시간
- **알고리즘**: HS512 (HMAC with SHA-512)

### 쿠키 보안
- **Secure**: HTTPS 전송만 허용
- **HttpOnly**: JavaScript 접근 차단
- **SameSite**: CSRF 공격 방지

### 데이터 보안
- **비밀번호**: BCrypt 해싱 (라운드 10)
- **토큰 저장**: Refresh Token 해시값만 저장
- **민감 정보**: 로그에서 마스킹 처리

## 🐛 문제 해결

### 데이터베이스 연결 실패
```bash
# PostgreSQL 상태 확인
docker ps | grep postgres

# 연결 테스트
psql -h localhost -U postgres -d db-auth
```

### JWT 시크릿 키 오류
```bash
# 시크릿 키 길이 확인 (최소 32자)
echo -n $JWT_SECRET | wc -c
```

### 토큰 만료 오류
- Access Token 만료: Refresh Token으로 갱신
- Refresh Token 만료: 재로그인 필요

## 🧪 테스트

```bash
# 단위 테스트
./gradlew test

# 통합 테스트
./gradlew integrationTest

# 테스트 커버리지
./gradlew jacocoTestReport
```

## 📈 성능 최적화

- **연결 풀링**: HikariCP 기본 설정
- **캐싱**: 자주 조회되는 사용자 정보 캐싱 (예정)
- **비동기 처리**: 로그 기록 비동기 처리
- **인덱싱**: 토큰 해시, 사용자 ID 인덱스
