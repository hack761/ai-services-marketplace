package com.marketplace.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "mysecretkeymysecretkeymysecretkey12"; // must be 32+ chars for HS256

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)                          // ← new API (not setSubject)
                .issuedAt(new Date())                    // ← new API (not setIssuedAt)
                .expiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 60)
                )                                        // ← new API (not setExpiration)
                .signWith(getSigningKey())               // ← new API (no algorithm arg needed)
                .compact();
    }
}