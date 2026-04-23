package com.hibiznet.hr.employee.dto.response;

import java.time.LocalDate;

public record EmployeeResponse(
    Long id,
    String employeeNo,
    String name,
    String email,
    String phone,
    LocalDate hireDate,
    LocalDate resignDate,
    String statusCode,
    String employmentTypeCode,
    Long departmentId,
    String departmentName,
    Long jobTitleId,
    String jobTitleName,
    Long managerEmployeeId,
    String managerName,
    LocalDate probationEndDate,
    String workTypeCode
) {}
