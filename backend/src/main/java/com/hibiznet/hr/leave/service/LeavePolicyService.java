package com.hibiznet.hr.leave.service;

import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.common.audit.annotation.Auditable;
import com.hibiznet.hr.common.dto.PageResponse;
import com.hibiznet.hr.common.security.CurrentUserService;
import com.hibiznet.hr.leave.dto.request.LeavePolicyUpsertRequest;
import com.hibiznet.hr.leave.dto.response.LeavePolicyResponse;
import com.hibiznet.hr.leave.entity.LeavePolicy;
import com.hibiznet.hr.leave.repository.LeavePolicyRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
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
@Transactional(readOnly = true)
public class LeavePolicyService {

    private final LeavePolicyRepository leavePolicyRepository;
    private final CurrentUserService currentUserService;

    public PageResponse<LeavePolicyResponse> getPolicies(AuthenticatedUser actor, String keyword, Boolean isActive, int page, int size) {
        currentUserService.assertCanManageLeavePolicies(actor);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "policyName", "id"));
        return PageResponse.from(leavePolicyRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("policyName")), like),
                    cb.like(cb.lower(root.get("accrualBasisCode")), like)
                ));
            }
            if (isActive != null) predicates.add(cb.equal(root.get("isActive"), isActive));
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(this::toResponse));
    }

    public LeavePolicyResponse getPolicy(AuthenticatedUser actor, Long id) {
        currentUserService.assertCanManageLeavePolicies(actor);
        return toResponse(findPolicy(id));
    }

    @Transactional
    @Auditable(action = "CREATE", target = "leave_policy")
    public LeavePolicyResponse createPolicy(AuthenticatedUser actor, LeavePolicyUpsertRequest request) {
        currentUserService.assertCanManageLeavePolicies(actor);
        LeavePolicy policy = leavePolicyRepository.save(LeavePolicy.builder()
            .policyName(request.policyName())
            .accrualBasisCode(request.accrualBasisCode())
            .monthlyLeaveForFirstYear(request.monthlyLeaveForFirstYear())
            .annualLeaveDays(request.annualLeaveDays())
            .isActive(request.isActive())
            .build());
        return toResponse(policy);
    }

    @Transactional
    @Auditable(action = "UPDATE", target = "leave_policy")
    public LeavePolicyResponse updatePolicy(AuthenticatedUser actor, Long id, LeavePolicyUpsertRequest request) {
        currentUserService.assertCanManageLeavePolicies(actor);
        LeavePolicy policy = findPolicy(id);
        policy.update(request.policyName(), request.accrualBasisCode(), request.monthlyLeaveForFirstYear(), request.annualLeaveDays(), request.isActive());
        return toResponse(policy);
    }

    @Transactional
    @Auditable(action = "DELETE", target = "leave_policy")
    public void deletePolicy(AuthenticatedUser actor, Long id) {
        currentUserService.assertCanManageLeavePolicies(actor);
        leavePolicyRepository.delete(findPolicy(id));
    }

    private LeavePolicy findPolicy(Long id) {
        return leavePolicyRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("휴가 정책을 찾을 수 없습니다."));
    }

    private LeavePolicyResponse toResponse(LeavePolicy entity) {
        return new LeavePolicyResponse(entity.getId(), entity.getPolicyName(), entity.getAccrualBasisCode(),
            entity.getMonthlyLeaveForFirstYear(), entity.getAnnualLeaveDays(), entity.getIsActive());
    }
}
