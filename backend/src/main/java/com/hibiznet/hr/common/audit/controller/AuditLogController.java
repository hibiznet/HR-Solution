package com.hibiznet.hr.common.audit.controller;

import com.hibiznet.hr.common.audit.dto.AuditLogResponse;
import com.hibiznet.hr.common.audit.service.AuditLogService;
import com.hibiznet.hr.common.dto.PageResponse;
import com.hibiznet.hr.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {
    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_HR_MANAGER','ROLE_SYS_ADMIN')")
    public ApiResponse<PageResponse<AuditLogResponse>> getLogs(
        @RequestParam(required = false) String actionType,
        @RequestParam(required = false) String targetTable,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.ok(auditLogService.getLogs(actionType, targetTable, keyword, page, size));
    }
}
