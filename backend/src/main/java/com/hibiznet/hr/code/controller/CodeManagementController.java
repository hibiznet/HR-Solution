package com.hibiznet.hr.code.controller;

import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.code.dto.CodeManagementDtos.*;
import com.hibiznet.hr.code.service.CodeManagementService;
import com.hibiznet.hr.common.dto.PageResponse;
import com.hibiznet.hr.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/codes")
public class CodeManagementController {

    private final CodeManagementService codeManagementService;

    @GetMapping("/groups")
    public ApiResponse<PageResponse<CodeGroupResponse>> getGroups(@AuthenticationPrincipal AuthenticatedUser actor,
                                                                  @RequestParam(required = false) String keyword,
                                                                  @RequestParam(required = false) Boolean isActive,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(codeManagementService.getGroups(actor, keyword, isActive, page, size));
    }

    @GetMapping("/groups/{id}")
    public ApiResponse<CodeGroupResponse> getGroup(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id) {
        return ApiResponse.ok(codeManagementService.getGroup(actor, id));
    }

    @PostMapping("/groups")
    public ApiResponse<CodeGroupResponse> createGroup(@AuthenticationPrincipal AuthenticatedUser actor, @Valid @RequestBody CodeGroupRequest request) {
        return ApiResponse.ok(codeManagementService.createGroup(actor, request));
    }

    @PutMapping("/groups/{id}")
    public ApiResponse<CodeGroupResponse> updateGroup(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id, @Valid @RequestBody CodeGroupRequest request) {
        return ApiResponse.ok(codeManagementService.updateGroup(actor, id, request));
    }

    @DeleteMapping("/groups/{id}")
    public ApiResponse<Void> deleteGroup(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id) {
        codeManagementService.deleteGroup(actor, id);
        return ApiResponse.ok(null);
    }

    @GetMapping("/details")
    public ApiResponse<PageResponse<CodeDetailResponse>> getDetails(@AuthenticationPrincipal AuthenticatedUser actor,
                                                                    @RequestParam(required = false) String keyword,
                                                                    @RequestParam(required = false) Long groupId,
                                                                    @RequestParam(required = false) Boolean isActive,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(codeManagementService.getDetails(actor, keyword, groupId, isActive, page, size));
    }

    @GetMapping("/details/{id}")
    public ApiResponse<CodeDetailResponse> getDetail(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id) {
        return ApiResponse.ok(codeManagementService.getDetail(actor, id));
    }

    @PostMapping("/details")
    public ApiResponse<CodeDetailResponse> createDetail(@AuthenticationPrincipal AuthenticatedUser actor, @Valid @RequestBody CodeDetailRequest request) {
        return ApiResponse.ok(codeManagementService.createDetail(actor, request));
    }

    @PutMapping("/details/{id}")
    public ApiResponse<CodeDetailResponse> updateDetail(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id, @Valid @RequestBody CodeDetailRequest request) {
        return ApiResponse.ok(codeManagementService.updateDetail(actor, id, request));
    }

    @DeleteMapping("/details/{id}")
    public ApiResponse<Void> deleteDetail(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id) {
        codeManagementService.deleteDetail(actor, id);
        return ApiResponse.ok(null);
    }
}
