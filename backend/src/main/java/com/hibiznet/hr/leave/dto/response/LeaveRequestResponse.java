package com.hibiznet.hr.leave.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record LeaveRequestResponse(
    Long id,
    Long employeeId,
    String employeeName,
    Long departmentId,
    String departmentName,
    String leaveTypeCode,
    LocalDate startDate,
    LocalDate endDate,
    LocalTime startTime,
    LocalTime endTime,
    BigDecimal daysCount,
    String reason,
    String statusCode
) {}
