package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.UserAccount;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.expiration = expiration;
    }

    public String generateToken(UserAccount user) {

        Date now = new Date();

        Date expiry = new Date(
                now.getTime() + expiration
        );

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .claim("fresh", false)
                .claim("jti", UUID.randomUUID().toString())
                .claim("type", "access")
                .subject(user.getEmail())
                .issuedAt(now)
                .notBefore(now)
                .claim("csrf", UUID.randomUUID().toString())
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }
}