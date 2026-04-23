package com.hibiznet.hr.leave.controller;

import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.common.dto.PageResponse;
import com.hibiznet.hr.common.response.ApiResponse;
import com.hibiznet.hr.leave.dto.request.LeavePolicyUpsertRequest;
import com.hibiznet.hr.leave.dto.response.LeavePolicyResponse;
import com.hibiznet.hr.leave.service.LeavePolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leave/policies")
public class LeavePolicyController {
    private final LeavePolicyService leavePolicyService;

    @GetMapping
    public ApiResponse<PageResponse<LeavePolicyResponse>> getPolicies(@AuthenticationPrincipal AuthenticatedUser actor,
                                                                      @RequestParam(required = false) String keyword,
                                                                      @RequestParam(required = false) Boolean isActive,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(leavePolicyService.getPolicies(actor, keyword, isActive, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<LeavePolicyResponse> getPolicy(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id) {
        return ApiResponse.ok(leavePolicyService.getPolicy(actor, id));
    }

    @PostMapping
    public ApiResponse<LeavePolicyResponse> createPolicy(@AuthenticationPrincipal AuthenticatedUser actor, @Valid @RequestBody LeavePolicyUpsertRequest request) {
        return ApiResponse.ok(leavePolicyService.createPolicy(actor, request));
    }

    @PutMapping("/{id}")
    public ApiResponse<LeavePolicyResponse> updatePolicy(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id, @Valid @RequestBody LeavePolicyUpsertRequest request) {
        return ApiResponse.ok(leavePolicyService.updatePolicy(actor, id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePolicy(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id) {
        leavePolicyService.deletePolicy(actor, id);
        return ApiResponse.ok(null);
    }
}
