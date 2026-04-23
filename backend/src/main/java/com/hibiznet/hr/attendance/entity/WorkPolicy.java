package com.hibiznet.hr.attendance.entity;

import com.hibiznet.hr.common.entity.BaseTimeEntity;
import com.hibiznet.hr.common.entity.YNConverter;
import jakarta.persistence.*;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "work_policy")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkPolicy extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_name", nullable = false, length = 100)
    private String policyName;

    @Column(name = "work_type_code", nullable = false, length = 50)
    private String workTypeCode;

    @Column(name = "standard_start_time")
    private LocalTime standardStartTime;

    @Column(name = "standard_end_time")
    private LocalTime standardEndTime;

    @Column(name = "break_minutes", nullable = false)
    private Integer breakMinutes;

    @Convert(converter = YNConverter.class)
    @Column(name = "is_active", nullable = false, columnDefinition = "char(1)")
    private Boolean isActive;

    @Builder
    public WorkPolicy(String policyName, String workTypeCode, LocalTime standardStartTime, LocalTime standardEndTime, Integer breakMinutes, Boolean isActive) {
        this.policyName = policyName;
        this.workTypeCode = workTypeCode;
        this.standardStartTime = standardStartTime;
        this.standardEndTime = standardEndTime;
        this.breakMinutes = breakMinutes != null ? breakMinutes : 0;
        this.isActive = isActive != null ? isActive : Boolean.TRUE;
    }

    public void update(String policyName, String workTypeCode, LocalTime standardStartTime, LocalTime standardEndTime, Integer breakMinutes, Boolean isActive) {
        this.policyName = policyName;
        this.workTypeCode = workTypeCode;
        this.standardStartTime = standardStartTime;
        this.standardEndTime = standardEndTime;
        this.breakMinutes = breakMinutes != null ? breakMinutes : 0;
        this.isActive = isActive != null ? isActive : Boolean.TRUE;
    }
}
