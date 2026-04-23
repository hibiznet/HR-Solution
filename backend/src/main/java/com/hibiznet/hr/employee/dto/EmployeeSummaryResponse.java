package com.hibiznet.hr.employee.dto;

public record EmployeeSummaryResponse(
    Long id,
    String employeeNo,
    String name,
    String email,
    String statusCode,
    String departmentName
) {
}
