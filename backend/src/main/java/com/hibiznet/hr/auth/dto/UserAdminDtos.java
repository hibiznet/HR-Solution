package com.hibiznet.hr.auth.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class UserAdminDtos {
    public record RoleResponse(Long id, String roleCode, String roleName, String description) {}
    public record UserSummaryResponse(Long id, String username, String email, Boolean isActive, List<String> roles) {}
    public record UserDetailResponse(Long id, String username, String email, Boolean isActive, List<String> roles) {}
    public record UserSearchResponse(Long id, String username, String email, Boolean isActive, List<String> roles) {}
    public record UserUpsertRequest(
        @NotBlank String username,
        @Email @NotBlank String email,
        String password,
        @NotNull Boolean isActive,
        @NotEmpty List<String> roleCodes
    ) {}
}
