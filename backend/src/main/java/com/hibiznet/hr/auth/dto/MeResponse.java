package com.hibiznet.hr.auth.dto;

import java.util.List;

public record MeResponse(
    Long userId,
    Long employeeId,
    Long departmentId,
    String username,
    String displayName,
    String email,
    List<String> roles
) {
}
