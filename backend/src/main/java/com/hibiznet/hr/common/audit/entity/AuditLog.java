package com.hibiznet.hr.common.audit.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "audit_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "actor_user_id")
    private Long actorUserId;
    @Column(name = "actor_employee_id")
    private Long actorEmployeeId;
    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;
    @Column(name = "target_table", nullable = false, length = 100)
    private String targetTable;
    @Column(name = "target_id", nullable = false)
    private Long targetId;
    @Column(name = "before_data_json", columnDefinition = "longtext")
    private String beforeDataJson;
    @Column(name = "after_data_json", columnDefinition = "longtext")
    private String afterDataJson;
    @Column(name = "ip_address", length = 50)
    private String ipAddress;
    @Column(name = "user_agent", length = 255)
    private String userAgent;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public AuditLog(Long actorUserId, Long actorEmployeeId, String actionType, String targetTable, Long targetId,
                    String beforeDataJson, String afterDataJson, String ipAddress, String userAgent, LocalDateTime createdAt) {
        this.actorUserId = actorUserId;
        this.actorEmployeeId = actorEmployeeId;
        this.actionType = actionType;
        this.targetTable = targetTable;
        this.targetId = targetId;
        this.beforeDataJson = beforeDataJson;
        this.afterDataJson = afterDataJson;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
    }
}
