package com.hibiznet.hr.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(AuthenticatedUser user) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(jwtProperties.accessTokenExpireSeconds());

        return Jwts.builder()
            .subject(user.username())
            .issuer(jwtProperties.issuer())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .claim("uid", user.userId())
            .claim("email", user.email())
            .claim("roles", user.roles())
            .claim("typ", "access")
            .signWith(signingKey)
            .compact();
    }

    public String generateRefreshToken(AuthenticatedUser user) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(jwtProperties.refreshTokenExpireSeconds());

        return Jwts.builder()
            .subject(user.username())
            .issuer(jwtProperties.issuer())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .claim("uid", user.userId())
            .claim("typ", "refresh")
            .signWith(signingKey)
            .compact();
    }

    public AuthenticatedUser parseAuthenticatedUser(String token) {
        Claims claims = parseClaims(token);
        Long userId = claims.get("uid", Number.class).longValue();
        String username = claims.getSubject();
        String email = claims.get("email", String.class);
        List<String> roles = claims.get("roles", List.class);
        return new AuthenticatedUser(userId, username, email, roles == null ? List.of() : roles);
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(parseClaims(token).get("typ", String.class));
    }

    public boolean validateToken(String token) {
        parseClaims(token);
        return true;
    }

    public Instant getExpiration(String token) {
        return parseClaims(token).getExpiration().toInstant();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(signingKey)
            .requireIssuer(jwtProperties.issuer())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
