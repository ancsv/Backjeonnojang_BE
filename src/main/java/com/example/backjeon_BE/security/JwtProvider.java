package com.example.backjeon_BE.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    public String createToken(String email, String role, Long userId) {
        try {
            Date now = new Date();
            Date expiry = new Date(now.getTime() + 1000 * 60 * 60 * 24); // 24시간

            return Jwts.builder()
                    .setSubject(userId.toString())
                    .claim("email", email)
                    .claim("role", role)
                    .setIssuedAt(now)
                    .setExpiration(expiry)
                    .signWith(SignatureAlgorithm.HS256, secret.getBytes(StandardCharsets.UTF_8))
                    .compact();
        } catch (Exception e) {
            System.err.println("JWT 생성 실패: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("JWT 토큰 생성 실패", e);
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public String getEmailFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("email", String.class);
    }

    public String getRoleFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("role", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
    }
}