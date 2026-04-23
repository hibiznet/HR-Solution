package com.hibiznet.hr.common.security;

import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.employee.entity.Employee;
import com.hibiznet.hr.employee.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final EmployeeRepository employeeRepository;

    public boolean hasAnyRole(AuthenticatedUser user, String... roles) {
        Set<String> current = Set.copyOf(user.roles());
        for (String role : roles) {
            if (current.contains(role)) return true;
        }
        return false;
    }

    public Employee getEmployeeOrThrow(AuthenticatedUser user) {
        return employeeRepository.findByUserAccountId(user.userId())
            .orElseThrow(() -> new EntityNotFoundException("사용자에 연결된 직원 정보가 없습니다."));
    }

    public void assertCanReadDepartment(AuthenticatedUser user, Long targetDepartmentId) {
        if (targetDepartmentId == null) return;
        if (hasAnyRole(user, "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_SYS_ADMIN")) return;
        Employee me = getEmployeeOrThrow(user);
        Long myDepartmentId = me.getDepartment() != null ? me.getDepartment().getId() : null;
        if (myDepartmentId == null || !myDepartmentId.equals(targetDepartmentId)) {
            throw new AccessDeniedException("해당 부서 정보에 접근할 수 없습니다.");
        }
    }

    public void assertCanManageEmployees(AuthenticatedUser user) {
        if (!hasAnyRole(user, "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_SYS_ADMIN")) {
            throw new AccessDeniedException("직원 관리 권한이 없습니다.");
        }
    }

    public void assertCanManageDepartments(AuthenticatedUser user) {
        if (!hasAnyRole(user, "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_SYS_ADMIN")) {
            throw new AccessDeniedException("부서 관리 권한이 없습니다.");
        }
    }

    public void assertCanReadEmployee(AuthenticatedUser user, Employee target) {
        if (hasAnyRole(user, "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_SYS_ADMIN")) return;
        Employee me = getEmployeeOrThrow(user);
        if (hasAnyRole(user, "ROLE_TEAM_MANAGER")) {
            Long myDepartmentId = me.getDepartment() != null ? me.getDepartment().getId() : null;
            Long targetDepartmentId = target.getDepartment() != null ? target.getDepartment().getId() : null;
            if (myDepartmentId != null && myDepartmentId.equals(targetDepartmentId)) return;
        }
        if (me.getId().equals(target.getId())) return;
        throw new AccessDeniedException("해당 직원 정보에 접근할 수 없습니다.");
    }

    public void assertCanReadAttendance(AuthenticatedUser user, Employee target) {
        assertCanReadEmployee(user, target);
    }

    public void assertCanManageAttendance(AuthenticatedUser user, Employee target) {
        if (hasAnyRole(user, "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_SYS_ADMIN")) return;
        if (hasAnyRole(user, "ROLE_TEAM_MANAGER")) {
            Employee me = getEmployeeOrThrow(user);
            Long myDepartmentId = me.getDepartment() != null ? me.getDepartment().getId() : null;
            Long targetDepartmentId = target.getDepartment() != null ? target.getDepartment().getId() : null;
            if (myDepartmentId != null && myDepartmentId.equals(targetDepartmentId)) return;
        }
        throw new AccessDeniedException("근태 관리 권한이 없습니다.");
    }

    public void assertCanManageLeavePolicies(AuthenticatedUser user) {
        if (!hasAnyRole(user, "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_SYS_ADMIN")) {
            throw new AccessDeniedException("휴가 정책 관리 권한이 없습니다.");
        }
    }

    public void assertCanReadLeaveRequests(AuthenticatedUser user, Employee target) {
        assertCanReadEmployee(user, target);
    }

    public void assertCanManageLeaveRequests(AuthenticatedUser user, Employee target) {
        if (hasAnyRole(user, "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_SYS_ADMIN")) return;
        if (hasAnyRole(user, "ROLE_TEAM_MANAGER")) {
            Employee me = getEmployeeOrThrow(user);
            Long myDepartmentId = me.getDepartment() != null ? me.getDepartment().getId() : null;
            Long targetDepartmentId = target.getDepartment() != null ? target.getDepartment().getId() : null;
            if (myDepartmentId != null && myDepartmentId.equals(targetDepartmentId)) return;
        }
        if (getEmployeeOrThrow(user).getId().equals(target.getId())) return;
        throw new AccessDeniedException("휴가 요청 관리 권한이 없습니다.");
    }

    public void assertCanManageUsers(AuthenticatedUser user) {
        if (!hasAnyRole(user, "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_SYS_ADMIN")) {
            throw new AccessDeniedException("사용자/권한 관리 권한이 없습니다.");
        }
    }

    public void assertCanManageCodes(AuthenticatedUser user) {
        if (!hasAnyRole(user, "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_SYS_ADMIN")) {
            throw new AccessDeniedException("코드 관리 권한이 없습니다.");
        }
    }
}
