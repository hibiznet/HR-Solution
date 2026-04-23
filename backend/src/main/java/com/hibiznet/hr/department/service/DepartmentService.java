package com.hibiznet.hr.department.service;

import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.common.audit.annotation.Auditable;
import com.hibiznet.hr.common.dto.PageResponse;
import com.hibiznet.hr.common.security.CurrentUserService;
import com.hibiznet.hr.department.dto.request.DepartmentUpsertRequest;
import com.hibiznet.hr.department.dto.response.DepartmentResponse;
import com.hibiznet.hr.department.entity.Department;
import com.hibiznet.hr.department.repository.DepartmentRepository;
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
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final CurrentUserService currentUserService;

    public PageResponse<DepartmentResponse> getDepartments(AuthenticatedUser actor, String keyword, Boolean isActive, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sortOrder", "id"));
        Long scopedDepartmentId = currentUserService.hasAnyRole(actor, "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_SYS_ADMIN") ? null : currentUserService.getEmployeeOrThrow(actor).getDepartment().getId();
        return PageResponse.from(departmentRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("deptCode")), likeKeyword), cb.like(cb.lower(root.get("deptName")), likeKeyword), cb.like(cb.lower(root.get("deptType")), likeKeyword)));
            }
            if (isActive != null) predicates.add(cb.equal(root.get("isActive"), isActive));
            if (scopedDepartmentId != null) predicates.add(cb.equal(root.get("id"), scopedDepartmentId));
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(this::toResponse));
    }

    public List<DepartmentResponse> getAllDepartments(AuthenticatedUser actor) {
        if (currentUserService.hasAnyRole(actor, "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_SYS_ADMIN")) {
            return departmentRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder", "id")).stream().map(this::toResponse).toList();
        }
        Long departmentId = currentUserService.getEmployeeOrThrow(actor).getDepartment().getId();
        return List.of(toResponse(findDepartment(departmentId)));
    }

    public DepartmentResponse getDepartment(AuthenticatedUser actor, Long id) {
        currentUserService.assertCanReadDepartment(actor, id);
        return toResponse(findDepartment(id));
    }

    @Transactional
    @Auditable(action = "CREATE", target = "department")
    public DepartmentResponse createDepartment(AuthenticatedUser actor, DepartmentUpsertRequest request) {
        currentUserService.assertCanManageDepartments(actor);
        if (departmentRepository.existsByDeptCode(request.deptCode())) throw new IllegalArgumentException("이미 사용 중인 부서 코드입니다.");
        Department parent = request.parentId() != null ? findDepartment(request.parentId()) : null;
        Department department = Department.builder().parent(parent).deptCode(request.deptCode()).deptName(request.deptName()).deptType(request.deptType()).sortOrder(request.sortOrder()).isActive(request.isActive()).build();
        return toResponse(departmentRepository.save(department));
    }

    @Transactional
    @Auditable(action = "UPDATE", target = "department")
    public DepartmentResponse updateDepartment(AuthenticatedUser actor, Long id, DepartmentUpsertRequest request) {
        currentUserService.assertCanManageDepartments(actor);
        Department department = findDepartment(id);
        if (departmentRepository.existsByDeptCodeAndIdNot(request.deptCode(), id)) throw new IllegalArgumentException("이미 사용 중인 부서 코드입니다.");
        Department parent = request.parentId() != null ? findDepartment(request.parentId()) : null;
        if (parent != null && parent.getId().equals(id)) throw new IllegalArgumentException("자기 자신을 상위 부서로 지정할 수 없습니다.");
        department.update(parent, request.deptCode(), request.deptName(), request.deptType(), request.sortOrder(), request.isActive());
        return toResponse(department);
    }

    @Transactional
    public void deleteDepartment(AuthenticatedUser actor, Long id) {
        currentUserService.assertCanManageDepartments(actor);
        if (departmentRepository.existsByParentId(id)) throw new IllegalArgumentException("하위 부서가 존재하여 삭제할 수 없습니다.");
        if (employeeRepository.existsByDepartmentId(id)) throw new IllegalArgumentException("소속 직원이 존재하여 삭제할 수 없습니다.");
        departmentRepository.delete(findDepartment(id));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다."));
    }

    private DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(department.getId(), department.getDeptCode(), department.getDeptName(), department.getDeptType(), department.getSortOrder(), department.getIsActive(), department.getParent() != null ? department.getParent().getId() : null, department.getParent() != null ? department.getParent().getDeptName() : null);
    }
}
