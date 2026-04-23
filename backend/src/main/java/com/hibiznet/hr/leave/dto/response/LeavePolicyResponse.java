package com.hibiznet.hr.leave.dto.response;

import java.math.BigDecimal;

public record LeavePolicyResponse(
    Long id,
    String policyName,
    String accrualBasisCode,
    Boolean monthlyLeaveForFirstYear,
    BigDecimal annualLeaveDays,
    Boolean isActive
) {}
