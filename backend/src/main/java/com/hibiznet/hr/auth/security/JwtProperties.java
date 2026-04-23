package com.hibiznet.hr.auth.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
    String secret,
    long accessTokenExpireSeconds,
    long refreshTokenExpireSeconds,
    String issuer
) {
}
