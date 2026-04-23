package com.hibiznet.hr.common.audit.aspect;

import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.common.audit.annotation.Auditable;
import com.hibiznet.hr.common.audit.service.AuditLogService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Object result = joinPoint.proceed();
        auditLogService.log(currentUser().orElse(null), auditable.action(), auditable.target(), extractId(result), null, result);
        return result;
    }

    private Optional<AuthenticatedUser> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return Optional.empty();
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUser authenticatedUser) return Optional.of(authenticatedUser);
        return Optional.empty();
    }

    private Long extractId(Object result) {
        if (result == null) return 0L;
        try {
            var method = result.getClass().getMethod("id");
            Object value = method.invoke(result);
            if (value instanceof Long l) return l;
        } catch (Exception ignored) {}
        return 0L;
    }
}
