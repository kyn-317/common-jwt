# Personal JWT Library

A common library for easy implementation of JWT (JSON Web Token) authentication.

## Features
- Generate Access Token and Refresh Token
- Token validation
- Claims extraction

## Usage

### 1. Add Dependency

```xml

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
    
	<dependency>
	    <groupId>com.github.kyn-317</groupId>
	    <artifactId>common-jwt</artifactId>
	    <version>v1.0.4</version>
	</dependency>
```

### 2. JwtConfig Setup

```java
// Default settings (Access Token: 30 minutes, Refresh Token: 7 days)
JwtConfig jwtConfig = new JwtConfig("your-base64-encoded-secret-key");

// Custom expiration time settings
JwtConfig jwtConfig = new JwtConfig(
    "your-base64-encoded-secret-key",
    1800000L,  // Access Token expiration (ms)
    604800000L // Refresh Token expiration (ms)
);
```

### 3. Using JwtService

```java
// Create JwtService
JwtService jwtService = new JwtService(jwtConfig);
// Create Just String
JwtService jwtService = new JwtService("your-base64-encoded-secret-key");

// Generate token with TokenRequest
TokenRequest request = TokenRequest.builder()
    .subject("user@email.com")
    .roles(Arrays.asList("ROLE_USER"))
    .build();

TokenDto tokenDto = jwtService.generateToken(request);

// Generate token with claims
Map<String, Object> claims = new HashMap<>();
claims.put("username", "user1");
claims.put("roles", Arrays.asList("ROLE_USER"));

TokenDto tokenDto = jwtService.generateToken("user@email.com", claims);

// Token validation
boolean isValid = jwtService.validateToken(tokenDto.getAccessToken());

// Extract Claims
Claims claims = jwtService.getClaims(tokenDto.getAccessToken());

// Extract Subject
String subject = jwtService.getSubject(tokenDto.getAccessToken());

// Extract Roles
List<String> roles = jwtService.getRoles(tokenDto.getAccessToken());
```

## Important Notes
1. Secret key is Base64 encoded string
2. Token expiration time is in milliseconds (ms)
3. Do not include sensitive information in Claims

## License
This project is licensed under the MIT License - see the LICENSE file for details. 