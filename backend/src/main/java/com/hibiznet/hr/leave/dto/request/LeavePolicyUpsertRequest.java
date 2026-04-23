package com.hibiznet.hr.leave.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record LeavePolicyUpsertRequest(
    @NotBlank String policyName,
    @NotBlank String accrualBasisCode,
    @NotNull Boolean monthlyLeaveForFirstYear,
    @DecimalMin("0.00") BigDecimal annualLeaveDays,
    @NotNull Boolean isActive
) {}
