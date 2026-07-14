package org.fr.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final Key key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
            @Value("${app.jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public String generateAccessToken(String subject) {
        return generateToken(subject, accessTokenExpirationMs, "access");
    }

    public String generateRefreshToken(String subject) {
        return generateToken(subject, refreshTokenExpirationMs, "refresh");
    }

    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isValidAccessToken(String token, String subject) {
        Claims claims = parseClaims(token);
        return subject.equals(claims.getSubject()) && "access".equals(claims.get("typ", String.class));
    }

    public Instant getRefreshTokenExpiryInstant() {
        return Instant.now().plusMillis(refreshTokenExpirationMs);
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    private String generateToken(String subject, long expirationMs, String type) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(subject)
                .claim("typ", type)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
