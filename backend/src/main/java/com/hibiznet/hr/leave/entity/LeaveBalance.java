package com.hibiznet.hr.leave.entity;

import com.hibiznet.hr.common.entity.BaseTimeEntity;
import com.hibiznet.hr.employee.entity.Employee;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "leave_balance", uniqueConstraints = {
    @UniqueConstraint(name = "uk_leave_balance_employee_type_year", columnNames = {"employee_id", "leave_type_code", "base_year"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LeaveBalance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "leave_type_code", nullable = false, length = 50)
    private String leaveTypeCode;

    @Column(name = "base_year", nullable = false)
    private Integer baseYear;

    @Column(name = "granted_days", nullable = false, precision = 6, scale = 2)
    private BigDecimal grantedDays;

    @Column(name = "used_days", nullable = false, precision = 6, scale = 2)
    private BigDecimal usedDays;

    @Column(name = "remaining_days", nullable = false, precision = 6, scale = 2)
    private BigDecimal remainingDays;

    @Column(name = "adjusted_days", nullable = false, precision = 6, scale = 2)
    private BigDecimal adjustedDays;
}
