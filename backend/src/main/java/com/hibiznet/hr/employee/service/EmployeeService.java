package com.hibiznet.hr.employee.service;

import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.common.audit.annotation.Auditable;
import com.hibiznet.hr.common.dto.PageResponse;
import com.hibiznet.hr.common.security.CurrentUserService;
import com.hibiznet.hr.department.entity.Department;
import com.hibiznet.hr.department.repository.DepartmentRepository;
import com.hibiznet.hr.employee.dto.request.EmployeeUpsertRequest;
import com.hibiznet.hr.employee.dto.response.EmployeeResponse;
import com.hibiznet.hr.employee.entity.Employee;
import com.hibiznet.hr.employee.entity.JobTitle;
import com.hibiznet.hr.employee.repository.EmployeeRepository;
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
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final CurrentUserService currentUserService;

    public PageResponse<EmployeeResponse> getEmployees(AuthenticatedUser actor, String keyword, Long departmentId, String statusCode, int page, int size) {
        if (departmentId != null) currentUserService.assertCanReadDepartment(actor, departmentId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Employee me = currentUserService.getEmployeeOrThrow(actor);
        final Long scopedDepartmentId = currentUserService.hasAnyRole(actor, "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_SYS_ADMIN")
            ? departmentId
            : (currentUserService.hasAnyRole(actor, "ROLE_TEAM_MANAGER") ? (departmentId != null ? departmentId : me.getDepartment().getId()) : me.getDepartment().getId());
        return PageResponse.from(employeeRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("employeeNo")), likeKeyword),
                    cb.like(cb.lower(root.get("name")), likeKeyword),
                    cb.like(cb.lower(root.get("email")), likeKeyword)
                ));
            }
            if (scopedDepartmentId != null) predicates.add(cb.equal(root.get("department").get("id"), scopedDepartmentId));
            if (statusCode != null && !statusCode.isBlank()) predicates.add(cb.equal(root.get("statusCode"), statusCode));
            if (!currentUserService.hasAnyRole(actor, "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_SYS_ADMIN", "ROLE_TEAM_MANAGER")) {
                predicates.add(cb.equal(root.get("id"), me.getId()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(this::toResponse));
    }

    public List<EmployeeResponse> getAllEmployees(AuthenticatedUser actor) {
        Employee me = currentUserService.getEmployeeOrThrow(actor);
        if (currentUserService.hasAnyRole(actor, "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_SYS_ADMIN")) {
            return employeeRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream().map(this::toResponse).toList();
        }
        return employeeRepository.findAll((root, query, cb) -> cb.equal(root.get("department").get("id"), me.getDepartment().getId()), Sort.by(Sort.Direction.ASC, "name")).stream().map(this::toResponse).toList();
    }

    public EmployeeResponse getEmployee(AuthenticatedUser actor, Long id) {
        Employee employee = findEmployee(id);
        currentUserService.assertCanReadEmployee(actor, employee);
        return toResponse(employee);
    }

    @Transactional
    @Auditable(action = "CREATE", target = "employee")
    public EmployeeResponse createEmployee(AuthenticatedUser actor, EmployeeUpsertRequest request) {
        currentUserService.assertCanManageEmployees(actor);
        validateDuplicatesForCreate(request);
        Department department = findDepartment(request.departmentId());
        Employee manager = request.managerEmployeeId() != null ? findEmployee(request.managerEmployeeId()) : null;
        JobTitle jobTitle = null;
        Employee employee = Employee.builder()
            .employeeNo(request.employeeNo())
            .name(request.name())
            .email(request.email())
            .phone(request.phone())
            .hireDate(request.hireDate())
            .resignDate(request.resignDate())
            .statusCode(request.statusCode())
            .employmentTypeCode(request.employmentTypeCode())
            .department(department)
            .jobTitle(jobTitle)
            .manager(manager)
            .probationEndDate(request.probationEndDate())
            .workTypeCode(request.workTypeCode())
            .build();
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional
    @Auditable(action = "UPDATE", target = "employee")
    public EmployeeResponse updateEmployee(AuthenticatedUser actor, Long id, EmployeeUpsertRequest request) {
        currentUserService.assertCanManageEmployees(actor);
        Employee employee = findEmployee(id);
        if (employeeRepository.existsByEmployeeNoAndIdNot(request.employeeNo(), id)) throw new IllegalArgumentException("이미 사용 중인 사번입니다.");
        if (employeeRepository.existsByEmailAndIdNot(request.email(), id)) throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        Department department = findDepartment(request.departmentId());
        Employee manager = request.managerEmployeeId() != null ? findEmployee(request.managerEmployeeId()) : null;
        if (manager != null && manager.getId().equals(id)) throw new IllegalArgumentException("자기 자신을 관리자(상사)로 지정할 수 없습니다.");
        employee.update(request.employeeNo(), request.name(), request.email(), request.phone(), request.hireDate(), request.resignDate(), request.statusCode(), request.employmentTypeCode(), department, manager, request.probationEndDate(), request.workTypeCode());
        return toResponse(employee);
    }

    @Transactional
    public void deleteEmployee(AuthenticatedUser actor, Long id) {
        currentUserService.assertCanManageEmployees(actor);
        employeeRepository.delete(findEmployee(id));
    }

    private void validateDuplicatesForCreate(EmployeeUpsertRequest request) {
        if (employeeRepository.existsByEmployeeNo(request.employeeNo())) throw new IllegalArgumentException("이미 사용 중인 사번입니다.");
        if (employeeRepository.existsByEmail(request.email())) throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다."));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다."));
    }

    private EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
            employee.getId(), employee.getEmployeeNo(), employee.getName(), employee.getEmail(), employee.getPhone(),
            employee.getHireDate(), employee.getResignDate(), employee.getStatusCode(), employee.getEmploymentTypeCode(),
            employee.getDepartment() != null ? employee.getDepartment().getId() : null,
            employee.getDepartment() != null ? employee.getDepartment().getDeptName() : null,
            employee.getJobTitle() != null ? employee.getJobTitle().getId() : null,
            employee.getJobTitle() != null ? employee.getJobTitle().getTitleName() : null,
            employee.getManager() != null ? employee.getManager().getId() : null,
            employee.getManager() != null ? employee.getManager().getName() : null,
            employee.getProbationEndDate(), employee.getWorkTypeCode()
        );
    }
}
