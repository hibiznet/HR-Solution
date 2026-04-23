package com.hibiznet.hr.leave.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record LeaveRequestUpsertRequest(
    @NotNull Long employeeId,
    @NotBlank String leaveTypeCode,
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate,
    LocalTime startTime,
    LocalTime endTime,
    @NotNull @DecimalMin("0.00") BigDecimal daysCount,
    String reason,
    @NotBlank String statusCode
) {}
