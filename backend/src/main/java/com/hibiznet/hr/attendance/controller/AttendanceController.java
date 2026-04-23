package com.hibiznet.hr.attendance.controller;

import com.hibiznet.hr.attendance.dto.request.AttendanceUpsertRequest;
import com.hibiznet.hr.attendance.dto.response.AttendanceResponse;
import com.hibiznet.hr.attendance.service.AttendanceService;
import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.common.dto.PageResponse;
import com.hibiznet.hr.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ApiResponse<PageResponse<AttendanceResponse>> getAttendanceRecords(
        @AuthenticationPrincipal AuthenticatedUser actor,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Long employeeId,
        @RequestParam(required = false) Long departmentId,
        @RequestParam(required = false) String attendanceStatusCode,
        @RequestParam(required = false) Boolean isClosed,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.ok(attendanceService.getAttendanceRecords(actor, keyword, employeeId, departmentId, attendanceStatusCode, isClosed, startDate, endDate, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<AttendanceResponse> getAttendanceRecord(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id) {
        return ApiResponse.ok(attendanceService.getAttendanceRecord(actor, id));
    }

    @PostMapping
    public ApiResponse<AttendanceResponse> createAttendanceRecord(@AuthenticationPrincipal AuthenticatedUser actor, @Valid @RequestBody AttendanceUpsertRequest request) {
        return ApiResponse.ok(attendanceService.createAttendanceRecord(actor, request), "근태 기록을 등록했습니다.");
    }

    @PutMapping("/{id}")
    public ApiResponse<AttendanceResponse> updateAttendanceRecord(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id, @Valid @RequestBody AttendanceUpsertRequest request) {
        return ApiResponse.ok(attendanceService.updateAttendanceRecord(actor, id, request), "근태 기록을 수정했습니다.");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAttendanceRecord(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable Long id) {
        attendanceService.deleteAttendanceRecord(actor, id);
        return ApiResponse.ok(null, "근태 기록을 삭제했습니다.");
    }
}
