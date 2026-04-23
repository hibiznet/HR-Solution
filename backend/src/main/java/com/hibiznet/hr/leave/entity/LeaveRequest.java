package com.hibiznet.hr.leave.entity;

import com.hibiznet.hr.common.entity.BaseTimeEntity;
import com.hibiznet.hr.employee.entity.Employee;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "leave_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LeaveRequest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "leave_type_code", nullable = false, length = 50)
    private String leaveTypeCode;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "days_count", nullable = false, precision = 5, scale = 2)
    private BigDecimal daysCount;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "status_code", nullable = false, length = 50)
    private String statusCode;

    @Builder
    public LeaveRequest(
            Employee employee,
            String leaveTypeCode,
            LocalDate startDate,
            LocalDate endDate,
            LocalTime startTime,
            LocalTime endTime,
            BigDecimal daysCount,
            String reason,
            String statusCode
    ) {
        this.employee = employee;
        this.leaveTypeCode = leaveTypeCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.daysCount = daysCount;
        this.reason = reason;
        this.statusCode = statusCode;
    }

    public void update(
            Employee employee,
            String leaveTypeCode,
            LocalDate startDate,
            LocalDate endDate,
            LocalTime startTime,
            LocalTime endTime,
            BigDecimal daysCount,
            String reason,
            String statusCode
    ) {
        this.employee = employee;
        this.leaveTypeCode = leaveTypeCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.daysCount = daysCount;
        this.reason = reason;
        this.statusCode = statusCode;
    }
}

