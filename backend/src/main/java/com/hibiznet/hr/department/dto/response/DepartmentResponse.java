package com.hibiznet.hr.department.dto.response;

public record DepartmentResponse(
    Long id,
    String deptCode,
    String deptName,
    String deptType,
    Integer sortOrder,
    Boolean isActive,
    Long parentId,
    String parentName
) {}
