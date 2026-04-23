package com.hibiznet.hr.leave.controller;

import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.common.dto.PageResponse;
import com.hibiznet.hr.common.response.ApiResponse;
import com.hibiznet.hr.leave.dto.request.LeaveRequestUpsertRequest;
import com.hibiznet.hr.leave.dto.response.LeaveRequestResponse;
import com.hibiznet.hr.leave.service.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leave/requests")
public class LeaveRequestController {
    private final LeaveRequestService leaveRequestService;

    @GetMapping
    public ApiResponse<PageResponse<LeaveRequestResponse>> getLeaveRequests(@AuthenticationPrincipal AuthenticatedUser actor,
                                                                            @RequestParam(required = false) String keyword,
                                                                            @RequestParam(required = false) Long employeeId,
                                                                            @RequestParam(required = false) Long departmentId,
                                                                            @RequestParam(required = false) String leaveTypeCode,
                                                                            @RequestParam(required = false) String statusCode,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(leaveRequestService.getLeaveRequests(actor, keyword, employeeId, departmentId, leaveTypeCode, statusCode, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<LeaveRequestResponse> getLeaveRequest(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id) {
        return ApiResponse.ok(leaveRequestService.getLeaveRequest(actor, id));
    }

    @PostMapping
    public ApiResponse<LeaveRequestResponse> createLeaveRequest(@AuthenticationPrincipal AuthenticatedUser actor, @Valid @RequestBody LeaveRequestUpsertRequest request) {
        return ApiResponse.ok(leaveRequestService.createLeaveRequest(actor, request));
    }

    @PutMapping("/{id}")
    public ApiResponse<LeaveRequestResponse> updateLeaveRequest(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id, @Valid @RequestBody LeaveRequestUpsertRequest request) {
        return ApiResponse.ok(leaveRequestService.updateLeaveRequest(actor, id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteLeaveRequest(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id) {
        leaveRequestService.deleteLeaveRequest(actor, id);
        return ApiResponse.ok(null);
    }
}
