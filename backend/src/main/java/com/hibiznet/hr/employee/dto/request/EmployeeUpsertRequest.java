package com.hibiznet.hr.employee.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record EmployeeUpsertRequest(
    @NotBlank @Size(max = 50) String employeeNo,
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Email @Size(max = 150) String email,
    @Size(max = 30) String phone,
    @NotNull LocalDate hireDate,
    LocalDate resignDate,
    @NotBlank @Size(max = 50) String statusCode,
    @NotBlank @Size(max = 50) String employmentTypeCode,
    @NotNull Long departmentId,
    Long jobTitleId,
    Long managerEmployeeId,
    LocalDate probationEndDate,
    @Size(max = 50) String workTypeCode
) {}
