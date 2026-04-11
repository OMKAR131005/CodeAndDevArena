package com.devconnect.bakend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private int expiration;

    @Value("${app.jwt.temp-expiration}")
    private int tempExpiration;

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long id, boolean isMfaActive) {
        if (isMfaActive) return generateTempToken(id);
        return Jwts.builder()
                .subject(id.toString())
                .issuedAt(new Date())
                .claim("isMfaActive", false)
                .claim("isPending2FA", false)
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey())
                .compact();
    }

    public String generateTempToken(Long id) {
        return Jwts.builder()
                .subject(id.toString())
                .issuedAt(new Date())
                .claim("isMfaActive", true)
                .claim("isPending2FA", true)
                .expiration(new Date(System.currentTimeMillis() + tempExpiration))
                .signWith(getSignKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token);
            return true;
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    public Long getId(String token) {
        Claims claims = Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();
        return Long.parseLong(claims.getSubject());
    }

    public boolean isPending2FA(String token) {
        Claims claims = Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();
        return claims.get("isPending2FA", Boolean.class);
    }
}