package com.kyn.commonjwt.config;

import javax.crypto.SecretKey;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

@Getter
public class JwtConfig {
    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final String authoritiesKey = "auth";
    private static final long DEFAULT_ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 30L; // 30Min
    private static final long DEFAULT_REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7L; // 7Days

    public JwtConfig(String secret, long accessTokenExpiration, long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public JwtConfig(String secret) {
        this(secret, DEFAULT_ACCESS_TOKEN_EXPIRATION_TIME, DEFAULT_REFRESH_TOKEN_EXPIRATION_TIME);
    }
} 