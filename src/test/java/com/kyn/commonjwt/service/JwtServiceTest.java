package com.kyn.commonjwt.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.kyn.commonjwt.config.JwtConfig;
import com.kyn.commonjwt.dto.TokenDto;
import com.kyn.commonjwt.dto.TokenRequest;

import io.jsonwebtoken.Claims;

class JwtServiceTest {

    private JwtService jwtService;
    private static final String SECRET = "VGhpc0lzVGVzdFNlY3JldEtleUZvckpXVFRva2VuR2VuZXJhdGlvbkFuZFZhbGlkYXRpb24=";
    private static final String TEST_SUBJECT = "test@email.com";
    private static final List<String> TEST_ROLES = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

    @BeforeEach
    void setUp() {
        JwtConfig jwtConfig = new JwtConfig(SECRET);
        jwtService = new JwtService(jwtConfig);
    }

    @Nested
    @DisplayName("Token Generation Tests")
    class TokenGenerationTest {
        
        @Test
        @DisplayName("Generate TokenDto from TokenRequest")
        void generateTokenByRequest() {
            // given
            TokenRequest request = TokenRequest.builder()
                    .subject(TEST_SUBJECT)
                    .roles(TEST_ROLES)
                    .build();

            // when
            TokenDto tokenDto = jwtService.generateToken(request);

            // then
            assertThat(tokenDto.getAccessToken()).isNotNull();
            assertThat(tokenDto.getRefreshToken()).isNotNull();
            assertThat(tokenDto.getAccessTokenExpiresIn()).isGreaterThan(System.currentTimeMillis());
            assertThat(tokenDto.getRefreshTokenExpiresIn()).isGreaterThan(System.currentTimeMillis());

            // verify token information
            Claims claims = jwtService.getClaims(tokenDto.getAccessToken());
            assertThat(claims.getSubject()).isEqualTo(TEST_SUBJECT);
            assertThat(jwtService.getRoles(tokenDto.getAccessToken())).isEqualTo(TEST_ROLES);
        }

        @Test
        @DisplayName("Generate TokenDto from Subject and Claims")
        void generateTokenDtoBySubjectAndClaims() {
            // given
            Map<String, Object> claims = new HashMap<>();
            claims.put("auth", TEST_ROLES);

            // when
            TokenDto tokenDto = jwtService.generateTokenDto(TEST_SUBJECT, claims);

            // then
            assertThat(tokenDto.getAccessToken()).isNotNull();
            assertThat(tokenDto.getRefreshToken()).isNotNull();
            assertThat(tokenDto.getAccessTokenExpiresIn()).isGreaterThan(System.currentTimeMillis());
            assertThat(tokenDto.getRefreshTokenExpiresIn()).isGreaterThan(System.currentTimeMillis());
        }

        @Test
        @DisplayName("Generate TokenDto with Custom Expiration")
        void generateTokenDtoWithCustomExpiration() {
            // given
            Map<String, Object> claims = new HashMap<>();
            claims.put("auth", TEST_ROLES);
            long customAccessExpiration = 1000 * 60 * 15; // 15 minutes
            long customRefreshExpiration = 1000 * 60 * 60 * 24; // 1 day

            // when
            TokenDto tokenDto = jwtService.generateTokenDto(TEST_SUBJECT, claims, customAccessExpiration, customRefreshExpiration);

            // then
            long now = System.currentTimeMillis();
            assertThat(tokenDto.getAccessTokenExpiresIn()).isGreaterThan(now + customAccessExpiration - 1000);
            assertThat(tokenDto.getRefreshTokenExpiresIn()).isGreaterThan(now + customRefreshExpiration - 1000);
        }

        @Test
        @DisplayName("Generate Single Token from TokenRequest")
        void generateJustTokenByRequest() {
            // given
            TokenRequest request = TokenRequest.builder()
                    .subject(TEST_SUBJECT)
                    .roles(TEST_ROLES)
                    .build();

            // when
            String token = jwtService.generateJustToken(request);

            // then
            assertThat(token).isNotNull();
            assertThat(jwtService.validateToken(token)).isTrue();
            assertThat(jwtService.getSubject(token)).isEqualTo(TEST_SUBJECT);
            assertThat(jwtService.getRoles(token)).isEqualTo(TEST_ROLES);
        }

        @Test
        @DisplayName("Generate Single Token from Subject and Claims")
        void generateJustTokenBySubjectAndClaims() {
            // given
            Map<String, Object> claims = new HashMap<>();
            claims.put("auth", TEST_ROLES);

            // when
            String token = jwtService.generateJustToken(TEST_SUBJECT, claims);

            // then
            assertThat(token).isNotNull();
            assertThat(jwtService.validateToken(token)).isTrue();
            assertThat(jwtService.getSubject(token)).isEqualTo(TEST_SUBJECT);
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTest {

        @Test
        @DisplayName("Validate Valid Token")
        void validateValidToken() {
            // given
            TokenRequest request = TokenRequest.builder()
                    .subject(TEST_SUBJECT)
                    .roles(TEST_ROLES)
                    .build();
            String token = jwtService.generateJustToken(request);

            // when
            boolean isValid = jwtService.validateToken(token);

            // then
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("Validate Invalid Token")
        void validateInvalidToken() {
            // given
            String invalidToken = "invalid.token.format";

            // when
            boolean isValid = jwtService.validateToken(invalidToken);

            // then
            assertThat(isValid).isFalse();
        }
    }

    @Nested
    @DisplayName("Token Information Extraction Tests")
    class TokenInformationTest {

        private String token;

        @BeforeEach
        void setUp() {
            TokenRequest request = TokenRequest.builder()
                    .subject(TEST_SUBJECT)
                    .roles(TEST_ROLES)
                    .build();
            token = jwtService.generateJustToken(request);
        }

        @Test
        @DisplayName("Extract Subject from Token")
        void extractSubject() {
            // when
            String subject = jwtService.getSubject(token);

            // then
            assertThat(subject).isEqualTo(TEST_SUBJECT);
        }

        @Test
        @DisplayName("Extract Roles from Token")
        void extractRoles() {
            // when
            List<String> roles = jwtService.getRoles(token);

            // then
            assertThat(roles).isEqualTo(TEST_ROLES);
        }

        @Test
        @DisplayName("Extract Claims from Token")
        void extractClaims() {
            // when
            Claims claims = jwtService.getClaims(token);

            // then
            assertThat(claims.getSubject()).isEqualTo(TEST_SUBJECT);
            assertThat(claims.get("auth")).isEqualTo(TEST_ROLES);
        }
    }
} 