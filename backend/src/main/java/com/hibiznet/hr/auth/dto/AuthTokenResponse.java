package com.hibiznet.hr.auth.dto;

import java.time.Instant;
import java.util.List;

public record AuthTokenResponse(
    String tokenType,
    String accessToken,
    Instant accessTokenExpiresAt,
    String refreshToken,
    Instant refreshTokenExpiresAt,
    String username,
    List<String> roles
) {
}
