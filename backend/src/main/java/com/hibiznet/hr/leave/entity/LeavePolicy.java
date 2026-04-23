package com.hibiznet.hr.leave.entity;

import com.hibiznet.hr.common.entity.BaseTimeEntity;
import com.hibiznet.hr.common.entity.YNConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "leave_policy")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LeavePolicy extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_name", nullable = false, length = 100)
    private String policyName;

    @Column(name = "accrual_basis_code", nullable = false, length = 50)
    private String accrualBasisCode;

    @Convert(converter = YNConverter.class)
    @Column(name = "monthly_leave_for_first_year", nullable = false, columnDefinition = "char(1)")
    private Boolean monthlyLeaveForFirstYear;

    @Column(name = "annual_leave_days", precision = 6, scale = 2)
    private BigDecimal annualLeaveDays;

    @Convert(converter = YNConverter.class)
    @Column(name = "is_active", nullable = false, columnDefinition = "char(1)")
    private Boolean isActive;

    @Builder
    public LeavePolicy(
            String policyName,
            String accrualBasisCode,
            Boolean monthlyLeaveForFirstYear,
            BigDecimal annualLeaveDays,
            Boolean isActive
    ) {
        this.policyName = policyName;
        this.accrualBasisCode = accrualBasisCode;
        this.monthlyLeaveForFirstYear = monthlyLeaveForFirstYear;
        this.annualLeaveDays = annualLeaveDays;
        this.isActive = isActive;
    }

    public void update(
            String policyName,
            String accrualBasisCode,
            Boolean monthlyLeaveForFirstYear,
            BigDecimal annualLeaveDays,
            Boolean isActive
    ) {
        this.policyName = policyName;
        this.accrualBasisCode = accrualBasisCode;
        this.monthlyLeaveForFirstYear = monthlyLeaveForFirstYear;
        this.annualLeaveDays = annualLeaveDays;
        this.isActive = isActive;
    }
}
