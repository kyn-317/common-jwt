package com.kyn.commonjwt.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kyn.commonjwt.config.JwtConfig;
import com.kyn.commonjwt.dto.TokenDto;
import com.kyn.commonjwt.dto.TokenRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;

/**
 * Service class for JWT (JSON Web Token) operations.
 * Provides functionality for token generation, validation, and information extraction.
 */
@RequiredArgsConstructor
public class JwtService {
    
    private final JwtConfig jwtConfig;
    
    /**
     * initialize just secret key
     * @param secret
     */
    public JwtService (String secret){
        this.jwtConfig = new JwtConfig(secret);
    }

    /**
     * Generates a TokenDto from a TokenRequest object.
     *
     * @param request TokenRequest containing subject and roles
     * @return TokenDto containing access token, refresh token, and expiration times
     */
    public TokenDto generateToken(TokenRequest request) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(jwtConfig.getAuthoritiesKey(), request.getRoles());
        return generateTokenDto(request.getSubject(), claims);
    }

    /**
     * Generates a TokenDto from a TokenRequest object with custom access and refresh token expiration times.
     *
     * @param request TokenRequest containing subject and roles
     * @param accessExpiration Access token expiration time in milliseconds
     * @param refreshExpiration Refresh token expiration time in milliseconds
     * @return TokenDto containing access token, refresh token, and expiration times
     */
    public TokenDto generateToken(TokenRequest request, long accessExpiration, long refreshExpiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(jwtConfig.getAuthoritiesKey(), request.getRoles());
        return generateTokenDto(request.getSubject(), claims, accessExpiration, refreshExpiration);
    }
    
    /**
     * Generates a TokenDto from subject and claims.
     *
     * @param subject The subject of the token (typically user identifier)
     * @param claims Map of claims to include in the token
     * @return TokenDto containing access token, refresh token, and expiration times
     */
    public TokenDto generateTokenDto(String subject, Map<String, Object> claims) {
        return generateTokenDto(subject, claims, jwtConfig.getAccessTokenExpiration(), jwtConfig.getRefreshTokenExpiration());
    }

    /**
     * Generates a TokenDto with custom expiration times.
     *
     * @param subject The subject of the token (typically user identifier)
     * @param claims Map of claims to include in the token
     * @param accessExpiration Access token expiration time in milliseconds
     * @param refreshExpiration Refresh token expiration time in milliseconds
     * @return TokenDto containing access token, refresh token, and expiration times
     */
    public TokenDto generateTokenDto(String subject, Map<String, Object> claims, long accessExpiration, long refreshExpiration) {
        long now = (new Date()).getTime();
        String token = generateJustToken(subject, claims, accessExpiration);
        String refreshToken = generateJustToken(subject, claims, refreshExpiration);
        return TokenDto.builder()
                .refreshToken(refreshToken)
                .accessToken(token)
                .accessTokenExpiresIn(now + accessExpiration)
                .refreshTokenExpiresIn(now + refreshExpiration)
                .build();
    }

    /**
     * Generates a single token string from a TokenRequest.
     *
     * @param request TokenRequest containing subject and roles
     * @return JWT token string
     */
    public String generateJustToken(TokenRequest request){
        Map<String, Object> claims = new HashMap<>();
        claims.put(jwtConfig.getAuthoritiesKey(), request.getRoles());
        return generateJustToken(request.getSubject(), claims);
    }


    /**
     * Generates a single token string from a TokenRequest with custom access token expiration time.
     *
     * @param request TokenRequest containing subject and roles
     * @param accessExpiration Access token expiration time in milliseconds
     * @return JWT token string
     */
    public String generateJustToken(TokenRequest request, long accessExpiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(jwtConfig.getAuthoritiesKey(), request.getRoles());
        return generateJustToken(request.getSubject(), claims, accessExpiration);
    }
    
    /**
     * Generates a single token string from subject and claims.
     * Uses default access token expiration time.
     *
     * @param subject The subject of the token (typically user identifier)
     * @param claims Map of claims to include in the token
     * @return JWT token string
     */
    public String generateJustToken(String subject, Map<String, Object> claims) {
        return generateJustToken(subject, claims, jwtConfig.getAccessTokenExpiration());
    }

    /**
     * Generates a single token string with custom expiration time.
     *
     * @param subject The subject of the token (typically user identifier)
     * @param claims Map of claims to include in the token
     * @param expiration Token expiration time in milliseconds
     * @return JWT token string
     */
    public String generateJustToken(String subject, Map<String, Object> claims, long expiration) {       
        long now = (new Date()).getTime();
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiration))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    /**
     * Validates a JWT token string.
     *
     * @param token JWT token string to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token JWT token string
     * @return Claims object containing all token claims
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the subject from a JWT token.
     *
     * @param token JWT token string
     * @return Subject string (typically user identifier)
     */
    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extracts the roles/authorities from a JWT token.
     *
     * @param token JWT token string
     * @return List of role strings
     */
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Claims claims = getClaims(token);
        return (List<String>) claims.get(jwtConfig.getAuthoritiesKey());
    }
} 