# Common JWT Library

JWT(JSON Web Token) 인증을 쉽게 구현할 수 있는 공통 라이브러리입니다.

## 기능
- Access Token 및 Refresh Token 생성
- 토큰 검증
- Claims 추출
- 비밀번호 암호화 (BCrypt)

## 사용 방법

### 1. 의존성 추가

```xml
<dependency>
    <groupId>com.kyn</groupId>
    <artifactId>common-jwt</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. JwtConfig 설정

```java
// 기본 설정 (Access Token 30분, Refresh Token 7일)
JwtConfig jwtConfig = new JwtConfig("your-base64-encoded-secret-key");

// 커스텀 만료 시간 설정
JwtConfig jwtConfig = new JwtConfig(
    "your-base64-encoded-secret-key",
    1800000L,  // Access Token 만료 시간 (ms)
    604800000L // Refresh Token 만료 시간 (ms)
);
```

### 3. JwtService 사용

```java
// JwtService 생성
JwtService jwtService = new JwtService(jwtConfig);

// 토큰 생성
Map<String, Object> claims = new HashMap<>();
claims.put("role", "ROLE_USER");
claims.put("username", "user1");

TokenDto tokenDto = jwtService.generateToken("user@email.com", claims);

// 토큰 검증
boolean isValid = jwtService.validateToken(tokenDto.getAccessToken());

// Claims 추출
Claims claims = jwtService.getClaims(tokenDto.getAccessToken());

// Subject(이메일 등) 추출
String subject = jwtService.getSubject(tokenDto.getAccessToken());
```

## 주의사항
1. 비밀키는 반드시 Base64로 인코딩된 문자열을 사용해야 합니다.
2. 토큰 만료 시간은 밀리초(ms) 단위입니다.
3. Claims에는 민감한 정보를 포함하지 않도록 주의하세요.

## 테스트
JUnit 5를 사용한 테스트 코드가 포함되어 있습니다. 다음 명령어로 테스트를 실행할 수 있습니다:

```bash
mvn test
``` 