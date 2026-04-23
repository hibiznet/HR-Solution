package com.hibiznet.hr.attendance.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceResponse(
    Long id,
    Long employeeId,
    String employeeNo,
    String employeeName,
    Long departmentId,
    String departmentName,
    LocalDate workDate,
    LocalDateTime plannedStartTime,
    LocalDateTime plannedEndTime,
    LocalDateTime checkInTime,
    LocalDateTime checkOutTime,
    Integer breakMinutes,
    Integer totalWorkMinutes,
    Integer overtimeMinutes,
    String attendanceStatusCode,
    Boolean isClosed
) {}
