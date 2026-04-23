package com.hibiznet.hr.auth.controller;

import com.hibiznet.hr.auth.dto.AuthTokenResponse;
import com.hibiznet.hr.auth.dto.LoginRequest;
import com.hibiznet.hr.auth.dto.LogoutRequest;
import com.hibiznet.hr.auth.dto.MeResponse;
import com.hibiznet.hr.auth.dto.RefreshTokenRequest;
import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.auth.service.AuthService;
import com.hibiznet.hr.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request.username(), request.password()));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.refreshToken());
        return ApiResponse.ok("로그아웃되었습니다.");
    }

    @GetMapping("/me")
    public ApiResponse<MeResponse> me(@AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.ok(authService.me(user));
    }
}
