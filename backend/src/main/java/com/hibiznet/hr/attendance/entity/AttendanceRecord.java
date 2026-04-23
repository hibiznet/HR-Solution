package com.hibiznet.hr.attendance.entity;

import com.hibiznet.hr.common.entity.BaseTimeEntity;
import com.hibiznet.hr.common.entity.YNConverter;
import com.hibiznet.hr.employee.entity.Employee;
import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "attendance_record", uniqueConstraints = {
    @UniqueConstraint(name = "uk_attendance_employee_work_date", columnNames = {"employee_id", "work_date"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttendanceRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "planned_start_time")
    private LocalDateTime plannedStartTime;

    @Column(name = "planned_end_time")
    private LocalDateTime plannedEndTime;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "break_minutes", nullable = false)
    private Integer breakMinutes = 0;

    @Column(name = "total_work_minutes", nullable = false)
    private Integer totalWorkMinutes = 0;

    @Column(name = "overtime_minutes", nullable = false)
    private Integer overtimeMinutes = 0;

    @Column(name = "attendance_status_code", nullable = false, length = 50)
    private String attendanceStatusCode;

    @Convert(converter = YNConverter.class)
    @Column(name = "is_closed", nullable = false, columnDefinition = "char(1)")
    private Boolean isClosed = false;

    @Builder
    public AttendanceRecord(Employee employee, LocalDate workDate, LocalDateTime plannedStartTime,
                            LocalDateTime plannedEndTime, LocalDateTime checkInTime, LocalDateTime checkOutTime,
                            Integer breakMinutes, Integer totalWorkMinutes, Integer overtimeMinutes,
                            String attendanceStatusCode, Boolean isClosed) {
        this.employee = employee;
        this.workDate = workDate;
        this.plannedStartTime = plannedStartTime;
        this.plannedEndTime = plannedEndTime;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.breakMinutes = breakMinutes != null ? breakMinutes : 0;
        this.totalWorkMinutes = totalWorkMinutes != null ? totalWorkMinutes : calculateTotalWorkMinutes(checkInTime, checkOutTime, this.breakMinutes);
        this.overtimeMinutes = overtimeMinutes != null ? overtimeMinutes : 0;
        this.attendanceStatusCode = attendanceStatusCode;
        this.isClosed = isClosed != null ? isClosed : Boolean.FALSE;
    }

    public void update(Employee employee, LocalDate workDate, LocalDateTime plannedStartTime,
                       LocalDateTime plannedEndTime, LocalDateTime checkInTime, LocalDateTime checkOutTime,
                       Integer breakMinutes, Integer overtimeMinutes, String attendanceStatusCode, Boolean isClosed) {
        this.employee = employee;
        this.workDate = workDate;
        this.plannedStartTime = plannedStartTime;
        this.plannedEndTime = plannedEndTime;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.breakMinutes = breakMinutes != null ? breakMinutes : 0;
        this.totalWorkMinutes = calculateTotalWorkMinutes(checkInTime, checkOutTime, this.breakMinutes);
        this.overtimeMinutes = overtimeMinutes != null ? overtimeMinutes : 0;
        this.attendanceStatusCode = attendanceStatusCode;
        this.isClosed = isClosed != null ? isClosed : Boolean.FALSE;
    }

    private int calculateTotalWorkMinutes(LocalDateTime checkInTime, LocalDateTime checkOutTime, Integer breakMinutes) {
        if (checkInTime == null || checkOutTime == null || checkOutTime.isBefore(checkInTime)) {
            return 0;
        }
        long worked = Duration.between(checkInTime, checkOutTime).toMinutes() - (breakMinutes != null ? breakMinutes : 0);
        return (int) Math.max(worked, 0);
    }
}
