package com.hibiznet.hr.department.controller;

import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.common.dto.PageResponse;
import com.hibiznet.hr.common.response.ApiResponse;
import com.hibiznet.hr.department.dto.request.DepartmentUpsertRequest;
import com.hibiznet.hr.department.dto.response.DepartmentResponse;
import com.hibiznet.hr.department.service.DepartmentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    public ApiResponse<PageResponse<DepartmentResponse>> getDepartments(@AuthenticationPrincipal AuthenticatedUser actor,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Boolean isActive,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(departmentService.getDepartments(actor, keyword, isActive, page, size));
    }

    @GetMapping("/all")
    public ApiResponse<List<DepartmentResponse>> getAllDepartments(@AuthenticationPrincipal AuthenticatedUser actor) {
        return ApiResponse.ok(departmentService.getAllDepartments(actor));
    }

    @GetMapping("/{id}")
    public ApiResponse<DepartmentResponse> getDepartment(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id) {
        return ApiResponse.ok(departmentService.getDepartment(actor, id));
    }

    @PostMapping
    public ApiResponse<DepartmentResponse> createDepartment(@AuthenticationPrincipal AuthenticatedUser actor, @Valid @RequestBody DepartmentUpsertRequest request) {
        return ApiResponse.ok(departmentService.createDepartment(actor, request), "부서를 등록했습니다.");
    }

    @PutMapping("/{id}")
    public ApiResponse<DepartmentResponse> updateDepartment(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id, @Valid @RequestBody DepartmentUpsertRequest request) {
        return ApiResponse.ok(departmentService.updateDepartment(actor, id, request), "부서를 수정했습니다.");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDepartment(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id) {
        departmentService.deleteDepartment(actor, id);
        return ApiResponse.ok(null, "부서를 삭제했습니다.");
    }
}
