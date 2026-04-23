package com.hibiznet.hr.common.audit.dto;

import com.hibiznet.hr.common.audit.entity.AuditLog;
import java.time.LocalDateTime;

public record AuditLogResponse(
    Long id,
    Long actorUserId,
    String actionType,
    String targetTable,
    Long targetId,
    String beforeDataJson,
    String afterDataJson,
    String ipAddress,
    String userAgent,
    LocalDateTime createdAt
) {
    public static AuditLogResponse from(AuditLog entity) {
        return new AuditLogResponse(
            entity.getId(), entity.getActorUserId(), entity.getActionType(), entity.getTargetTable(), entity.getTargetId(),
            entity.getBeforeDataJson(), entity.getAfterDataJson(), entity.getIpAddress(), entity.getUserAgent(), entity.getCreatedAt()
        );
    }
}
