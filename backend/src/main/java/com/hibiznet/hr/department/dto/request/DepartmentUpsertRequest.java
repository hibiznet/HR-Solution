package com.hibiznet.hr.department.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DepartmentUpsertRequest(
    Long parentId,
    @NotBlank @Size(max = 50) String deptCode,
    @NotBlank @Size(max = 100) String deptName,
    @Size(max = 50) String deptType,
    @NotNull Integer sortOrder,
    @NotNull Boolean isActive
) {}
