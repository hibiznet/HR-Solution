package com.hibiznet.hr.common.audit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.common.audit.dto.AuditLogResponse;
import com.hibiznet.hr.common.audit.entity.AuditLog;
import com.hibiznet.hr.common.audit.repository.AuditLogRepository;
import com.hibiznet.hr.common.dto.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Transactional
    public void log(AuthenticatedUser actor, String action, String targetTable, Long targetId, Object beforeData, Object afterData) {
        AuditLog log = AuditLog.builder()
            .actorUserId(actor != null ? actor.userId() : null)
            .actionType(action)
            .targetTable(targetTable)
            .targetId(targetId != null ? targetId : 0L)
            .beforeDataJson(writeJson(beforeData))
            .afterDataJson(writeJson(afterData))
            .ipAddress(request.getRemoteAddr())
            .userAgent(truncate(request.getHeader("User-Agent"), 255))
            .createdAt(LocalDateTime.now())
            .build();
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> getLogs(String actionType, String targetTable, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return PageResponse.from(auditLogRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (actionType != null && !actionType.isBlank()) predicates.add(cb.equal(root.get("actionType"), actionType));
            if (targetTable != null && !targetTable.isBlank()) predicates.add(cb.equal(root.get("targetTable"), targetTable));
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("beforeDataJson")), like),
                    cb.like(cb.lower(root.get("afterDataJson")), like),
                    cb.like(cb.lower(root.get("userAgent")), like)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(AuditLogResponse::from));
    }

    private String writeJson(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return String.valueOf(value);
        }
    }

    private String truncate(String value, int max) {
        if (value == null) return null;
        return value.length() <= max ? value : value.substring(0, max);
    }
}
