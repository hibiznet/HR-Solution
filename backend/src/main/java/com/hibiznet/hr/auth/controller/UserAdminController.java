package com.hibiznet.hr.auth.controller;

import com.hibiznet.hr.auth.dto.UserAdminDtos.*;
import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.auth.service.UserAdminService;
import com.hibiznet.hr.common.dto.PageResponse;
import com.hibiznet.hr.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class UserAdminController {
    private final UserAdminService userAdminService;

    @GetMapping("/users")
    public ApiResponse<PageResponse<UserSearchResponse>> getUsers(@AuthenticationPrincipal AuthenticatedUser actor,
                                                                  @RequestParam(required = false) String keyword,
                                                                  @RequestParam(required = false) Boolean isActive,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(userAdminService.getUsers(actor, keyword, isActive, page, size));
    }

    @GetMapping("/users/{id}")
    public ApiResponse<UserDetailResponse> getUser(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id) {
        return ApiResponse.ok(userAdminService.getUser(actor, id));
    }

    @PostMapping("/users")
    public ApiResponse<UserDetailResponse> createUser(@AuthenticationPrincipal AuthenticatedUser actor, @Valid @RequestBody UserUpsertRequest request) {
        return ApiResponse.ok(userAdminService.createUser(actor, request));
    }

    @PutMapping("/users/{id}")
    public ApiResponse<UserDetailResponse> updateUser(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id, @Valid @RequestBody UserUpsertRequest request) {
        return ApiResponse.ok(userAdminService.updateUser(actor, id, request));
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id) {
        userAdminService.deleteUser(actor, id);
        return ApiResponse.ok(null);
    }

    @GetMapping("/roles")
    public ApiResponse<List<RoleResponse>> getRoles(@AuthenticationPrincipal AuthenticatedUser actor) {
        return ApiResponse.ok(userAdminService.getRoles(actor));
    }
}
