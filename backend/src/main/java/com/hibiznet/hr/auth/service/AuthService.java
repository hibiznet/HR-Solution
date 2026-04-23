package com.hibiznet.hr.auth.service;

import com.hibiznet.hr.auth.dto.AuthTokenResponse;
import com.hibiznet.hr.auth.dto.MeResponse;
import com.hibiznet.hr.auth.entity.UserAccount;
import com.hibiznet.hr.auth.entity.UserRole;
import com.hibiznet.hr.auth.repository.UserAccountRepository;
import com.hibiznet.hr.auth.repository.UserRoleRepository;
import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.auth.security.JwtTokenProvider;
import com.hibiznet.hr.auth.security.RefreshTokenProperties;
import com.hibiznet.hr.auth.token.RefreshTokenStore;
import com.hibiznet.hr.employee.entity.Employee;
import com.hibiznet.hr.employee.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final RefreshTokenProperties refreshTokenProperties;

    public AuthTokenResponse login(String username, String rawPassword) {
        UserAccount user = userAccountRepository.findByUsername(username)
            .orElseThrow(() -> new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다."));
        if (!Boolean.TRUE.equals(user.getIsActive()) || !passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        user.updateLastLoginAt(LocalDateTime.now());
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user.getId(), user.getUsername(), user.getEmail(), loadRoles(user));
        return issueTokens(authenticatedUser);
    }

    public AuthTokenResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("유효하지 않은 리프레시 토큰입니다.");
        }
        AuthenticatedUser parsed = jwtTokenProvider.parseAuthenticatedUser(refreshToken);
        String key = refreshKey(parsed.userId());
        String storedToken = refreshTokenStore.get(key).orElseThrow(() -> new BadCredentialsException("저장된 리프레시 토큰이 없습니다."));
        if (!storedToken.equals(refreshToken)) {
            throw new BadCredentialsException("리프레시 토큰이 일치하지 않습니다.");
        }
        UserAccount user = userAccountRepository.findById(parsed.userId())
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user.getId(), user.getUsername(), user.getEmail(), loadRoles(user));
        refreshTokenStore.delete(key);
        return issueTokens(authenticatedUser);
    }

    public void logout(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("유효하지 않은 리프레시 토큰입니다.");
        }
        AuthenticatedUser parsed = jwtTokenProvider.parseAuthenticatedUser(refreshToken);
        String key = refreshKey(parsed.userId());
        refreshTokenStore.get(key).filter(refreshToken::equals).ifPresent(v -> refreshTokenStore.delete(key));
    }

    @Transactional(readOnly = true)
    public MeResponse me(AuthenticatedUser user) {
        UserAccount found = userAccountRepository.findById(user.userId())
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Employee employee = employeeRepository.findByUserAccountId(found.getId()).orElse(null);
        return new MeResponse(
            found.getId(),
            employee != null ? employee.getId() : null,
            employee != null && employee.getDepartment() != null ? employee.getDepartment().getId() : null,
            found.getUsername(),
            employee != null ? employee.getName() : found.getUsername(),
            found.getEmail(),
            loadRoles(found)
        );
    }

    private AuthTokenResponse issueTokens(AuthenticatedUser authenticatedUser) {
        String accessToken = jwtTokenProvider.generateAccessToken(authenticatedUser);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authenticatedUser);
        Instant refreshExpiresAt = jwtTokenProvider.getExpiration(refreshToken);
        refreshTokenStore.save(refreshKey(authenticatedUser.userId()), refreshToken, Duration.between(Instant.now(), refreshExpiresAt));
        return new AuthTokenResponse("Bearer", accessToken, jwtTokenProvider.getExpiration(accessToken), refreshToken, refreshExpiresAt, authenticatedUser.username(), authenticatedUser.roles());
    }

    private List<String> loadRoles(UserAccount user) {
        return userRoleRepository.findByUserAccount(user).stream().map(UserRole::getRole).map(role -> role.getRoleCode()).toList();
    }

    private String refreshKey(Long userId) { return refreshTokenProperties.keyPrefix() + userId; }
}
