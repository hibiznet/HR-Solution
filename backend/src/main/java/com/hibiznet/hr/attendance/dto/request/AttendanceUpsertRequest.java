package com.hibiznet.hr.attendance.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceUpsertRequest(
    @NotNull Long employeeId,
    @NotNull LocalDate workDate,
    LocalDateTime plannedStartTime,
    LocalDateTime plannedEndTime,
    LocalDateTime checkInTime,
    LocalDateTime checkOutTime,
    @NotNull @Min(0) Integer breakMinutes,
    @NotNull @Min(0) Integer overtimeMinutes,
    @NotBlank @Size(max = 50) String attendanceStatusCode,
    @NotNull Boolean isClosed
) {}
