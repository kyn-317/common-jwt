package com.kyn.commonjwt.config;

import javax.crypto.SecretKey;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

/**
 * Configuration class for JWT (JSON Web Token) settings.
 * Provides functionality for JWT secret key management and token expiration times.
 */
@Getter
public class JwtConfig {
    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final String authoritiesKey = "auth";
    private static final long DEFAULT_ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 30L; // 30Min
    private static final long DEFAULT_REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7L; // 7Days

    /**
     * Creates a JwtConfig with custom expiration times.
     *
     * @param secret Base64 encoded secret key for JWT signing
     * @param accessTokenExpiration Access token expiration time in milliseconds
     * @param refreshTokenExpiration Refresh token expiration time in milliseconds
     */
    public JwtConfig(String secret, long accessTokenExpiration, long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * Creates a JwtConfig with default expiration times.
     * Default values are 30 minutes for access token and 7 days for refresh token.
     *
     * @param secret Base64 encoded secret key for JWT signing
     */
    public JwtConfig(String secret) {
        this(secret, DEFAULT_ACCESS_TOKEN_EXPIRATION_TIME, DEFAULT_REFRESH_TOKEN_EXPIRATION_TIME);
    }

    /**
     * Returns the key name used for storing authorities/roles in JWT claims.
     * 
     * @return The key name for authorities in JWT claims
     */
    public String getAuthoritiesKey() {
        return this.authoritiesKey;
    }
} 