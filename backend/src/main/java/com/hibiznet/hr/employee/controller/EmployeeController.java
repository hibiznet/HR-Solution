package com.hibiznet.hr.employee.controller;

import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.common.dto.PageResponse;
import com.hibiznet.hr.common.response.ApiResponse;
import com.hibiznet.hr.employee.dto.request.EmployeeUpsertRequest;
import com.hibiznet.hr.employee.dto.response.EmployeeResponse;
import com.hibiznet.hr.employee.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public ApiResponse<PageResponse<EmployeeResponse>> getEmployees(@AuthenticationPrincipal AuthenticatedUser actor,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Long departmentId,
        @RequestParam(required = false) String statusCode,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(employeeService.getEmployees(actor, keyword, departmentId, statusCode, page, size));
    }

    @GetMapping("/all")
    public ApiResponse<List<EmployeeResponse>> getAllEmployees(@AuthenticationPrincipal AuthenticatedUser actor) {
        return ApiResponse.ok(employeeService.getAllEmployees(actor));
    }

    @GetMapping("/{id}")
    public ApiResponse<EmployeeResponse> getEmployee(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id) {
        return ApiResponse.ok(employeeService.getEmployee(actor, id));
    }

    @PostMapping
    public ApiResponse<EmployeeResponse> createEmployee(@AuthenticationPrincipal AuthenticatedUser actor, @Valid @RequestBody EmployeeUpsertRequest request) {
        return ApiResponse.ok(employeeService.createEmployee(actor, request), "직원을 등록했습니다.");
    }

    @PutMapping("/{id}")
    public ApiResponse<EmployeeResponse> updateEmployee(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id, @Valid @RequestBody EmployeeUpsertRequest request) {
        return ApiResponse.ok(employeeService.updateEmployee(actor, id, request), "직원 정보를 수정했습니다.");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEmployee(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id) {
        employeeService.deleteEmployee(actor, id);
        return ApiResponse.ok(null, "직원을 삭제했습니다.");
    }
}
