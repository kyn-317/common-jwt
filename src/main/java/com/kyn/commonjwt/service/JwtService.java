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

@RequiredArgsConstructor
public class JwtService {
    
    private final JwtConfig jwtConfig;
    
    //generate Token by Request
    public TokenDto generateToken(TokenRequest request) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(jwtConfig.getAuthoritiesKey(), request.getRoles());
        return generateTokenDto(request.getSubject(), claims);
    }
    //generate Token by Subject and Claims
    public TokenDto generateTokenDto(String subject, Map<String, Object> claims) {
        return generateTokenDto(subject, claims, jwtConfig.getAccessTokenExpiration(), jwtConfig.getRefreshTokenExpiration());
    }

    //generate Token by Subject and Claims and expiration
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

    public String generateJustToken(TokenRequest request){
        Map<String, Object> claims = new HashMap<>();
        claims.put(jwtConfig.getAuthoritiesKey(), request.getRoles());
        return generateJustToken(request.getSubject(), claims);
    }
    //generate Token by Subject and Claims and expiration
    public String generateJustToken(String subject, Map<String, Object> claims) {
        return generateJustToken(subject, claims, jwtConfig.getAccessTokenExpiration());
    }

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

    //validate Token
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

    //get Claims
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //get Subject
    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    //get Roles     
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Claims claims = getClaims(token);
        return (List<String>) claims.get(jwtConfig.getAuthoritiesKey());
    }
} 