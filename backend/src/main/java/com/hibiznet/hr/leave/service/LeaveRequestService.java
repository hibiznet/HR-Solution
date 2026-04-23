package com.hibiznet.hr.leave.service;

import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.common.audit.annotation.Auditable;
import com.hibiznet.hr.common.dto.PageResponse;
import com.hibiznet.hr.common.security.CurrentUserService;
import com.hibiznet.hr.employee.entity.Employee;
import com.hibiznet.hr.employee.repository.EmployeeRepository;
import com.hibiznet.hr.leave.dto.request.LeaveRequestUpsertRequest;
import com.hibiznet.hr.leave.dto.response.LeaveRequestResponse;
import com.hibiznet.hr.leave.entity.LeaveRequest;
import com.hibiznet.hr.leave.repository.LeaveRequestRepository;
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
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final CurrentUserService currentUserService;

    public PageResponse<LeaveRequestResponse> getLeaveRequests(AuthenticatedUser actor, String keyword, Long employeeId,
                                                               Long departmentId, String leaveTypeCode, String statusCode,
                                                               int page, int size) {
        if (employeeId != null) currentUserService.assertCanReadLeaveRequests(actor, findEmployee(employeeId));
        if (departmentId != null) currentUserService.assertCanReadDepartment(actor, departmentId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Long scopedDepartmentId = null;
        Long scopedEmployeeId = null;
        if (!currentUserService.hasAnyRole(actor, "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_SYS_ADMIN")) {
            Employee me = currentUserService.getEmployeeOrThrow(actor);
            if (currentUserService.hasAnyRole(actor, "ROLE_TEAM_MANAGER")) {
                scopedDepartmentId = departmentId != null ? departmentId : me.getDepartment().getId();
            } else {
                scopedEmployeeId = me.getId();
            }
        }
        final Long deptScope = scopedDepartmentId;
        final Long empScope = scopedEmployeeId;
        return PageResponse.from(leaveRequestRepository.findAll((root, query, cb) -> {
            root.fetch("employee", jakarta.persistence.criteria.JoinType.LEFT).fetch("department", jakarta.persistence.criteria.JoinType.LEFT);
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("employee").get("name")), like),
                    cb.like(cb.lower(root.get("employee").get("employeeNo")), like),
                    cb.like(cb.lower(root.get("reason")), like)
                ));
            }
            if (employeeId != null) predicates.add(cb.equal(root.get("employee").get("id"), employeeId));
            if (deptScope != null) predicates.add(cb.equal(root.get("employee").get("department").get("id"), deptScope));
            if (empScope != null) predicates.add(cb.equal(root.get("employee").get("id"), empScope));
            if (departmentId != null && deptScope == null) predicates.add(cb.equal(root.get("employee").get("department").get("id"), departmentId));
            if (leaveTypeCode != null && !leaveTypeCode.isBlank()) predicates.add(cb.equal(root.get("leaveTypeCode"), leaveTypeCode));
            if (statusCode != null && !statusCode.isBlank()) predicates.add(cb.equal(root.get("statusCode"), statusCode));
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(this::toResponse));
    }

    public LeaveRequestResponse getLeaveRequest(AuthenticatedUser actor, Long id) {
        LeaveRequest request = findLeaveRequest(id);
        currentUserService.assertCanReadLeaveRequests(actor, request.getEmployee());
        return toResponse(request);
    }

    @Transactional
    @Auditable(action = "CREATE", target = "leave_request")
    public LeaveRequestResponse createLeaveRequest(AuthenticatedUser actor, LeaveRequestUpsertRequest request) {
        Employee employee = findEmployee(request.employeeId());
        currentUserService.assertCanManageLeaveRequests(actor, employee);
        LeaveRequest entity = leaveRequestRepository.save(LeaveRequest.builder()
            .employee(employee)
            .leaveTypeCode(request.leaveTypeCode())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .startTime(request.startTime())
            .endTime(request.endTime())
            .daysCount(request.daysCount())
            .reason(request.reason())
            .statusCode(request.statusCode())
            .build());
        return toResponse(entity);
    }

    @Transactional
    @Auditable(action = "UPDATE", target = "leave_request")
    public LeaveRequestResponse updateLeaveRequest(AuthenticatedUser actor, Long id, LeaveRequestUpsertRequest request) {
        LeaveRequest entity = findLeaveRequest(id);
        Employee employee = findEmployee(request.employeeId());
        currentUserService.assertCanManageLeaveRequests(actor, entity.getEmployee());
        currentUserService.assertCanManageLeaveRequests(actor, employee);
        entity.update(employee, request.leaveTypeCode(), request.startDate(), request.endDate(),
            request.startTime(), request.endTime(), request.daysCount(), request.reason(), request.statusCode());
        return toResponse(entity);
    }

    @Transactional
    @Auditable(action = "DELETE", target = "leave_request")
    public void deleteLeaveRequest(AuthenticatedUser actor, Long id) {
        LeaveRequest entity = findLeaveRequest(id);
        currentUserService.assertCanManageLeaveRequests(actor, entity.getEmployee());
        leaveRequestRepository.delete(entity);
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("직원 정보를 찾을 수 없습니다."));
    }

    private LeaveRequest findLeaveRequest(Long id) {
        return leaveRequestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("휴가 요청을 찾을 수 없습니다."));
    }

    private LeaveRequestResponse toResponse(LeaveRequest entity) {
        Long departmentId = entity.getEmployee().getDepartment() != null ? entity.getEmployee().getDepartment().getId() : null;
        String departmentName = entity.getEmployee().getDepartment() != null ? entity.getEmployee().getDepartment().getDeptName() : null;
        return new LeaveRequestResponse(entity.getId(), entity.getEmployee().getId(), entity.getEmployee().getName(),
            departmentId, departmentName, entity.getLeaveTypeCode(), entity.getStartDate(), entity.getEndDate(),
            entity.getStartTime(), entity.getEndTime(), entity.getDaysCount(), entity.getReason(), entity.getStatusCode());
    }
}
