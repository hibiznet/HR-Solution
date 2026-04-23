package com.hibiznet.hr.auth.service;

import com.hibiznet.hr.attendance.entity.AttendanceRecord;
import com.hibiznet.hr.attendance.entity.WorkPolicy;
import com.hibiznet.hr.attendance.repository.AttendanceRecordRepository;
import com.hibiznet.hr.attendance.repository.WorkPolicyRepository;
import com.hibiznet.hr.auth.entity.Role;
import com.hibiznet.hr.auth.entity.UserAccount;
import com.hibiznet.hr.auth.entity.UserRole;
import com.hibiznet.hr.auth.repository.RoleRepository;
import com.hibiznet.hr.auth.repository.UserAccountRepository;
import com.hibiznet.hr.auth.repository.UserRoleRepository;
import com.hibiznet.hr.department.entity.Department;
import com.hibiznet.hr.department.repository.DepartmentRepository;
import com.hibiznet.hr.employee.entity.Employee;
import com.hibiznet.hr.employee.repository.EmployeeRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("local")
public class LocalAuthDataInitializer {
    @Bean
    CommandLineRunner initializeAuthData(UserAccountRepository userAccountRepository, RoleRepository roleRepository,
        UserRoleRepository userRoleRepository, DepartmentRepository departmentRepository,
        EmployeeRepository employeeRepository, AttendanceRecordRepository attendanceRecordRepository,
        WorkPolicyRepository workPolicyRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Role adminRole = roleRepository.findByRoleCode("ROLE_ADMIN").orElseGet(() -> roleRepository.save(Role.builder().roleCode("ROLE_ADMIN").roleName("시스템 관리자").description("전체 관리자").build()));
            Role hrRole = roleRepository.findByRoleCode("ROLE_HR_MANAGER").orElseGet(() -> roleRepository.save(Role.builder().roleCode("ROLE_HR_MANAGER").roleName("인사 관리자").description("직원 및 부서 관리").build()));
            Role teamRole = roleRepository.findByRoleCode("ROLE_TEAM_MANAGER").orElseGet(() -> roleRepository.save(Role.builder().roleCode("ROLE_TEAM_MANAGER").roleName("팀 관리자").description("소속 조직 조회").build()));

            Department hq = departmentRepository.findAll().stream().filter(d -> "HQ".equals(d.getDeptCode())).findFirst().orElseGet(() -> departmentRepository.save(Department.builder().deptCode("HQ").deptName("본사").deptType("본부").sortOrder(1).isActive(true).build()));
            Department dev = departmentRepository.findAll().stream().filter(d -> "DEV".equals(d.getDeptCode())).findFirst().orElseGet(() -> departmentRepository.save(Department.builder().parent(hq).deptCode("DEV").deptName("개발팀").deptType("팀").sortOrder(2).isActive(true).build()));

            UserAccount admin = userAccountRepository.findByUsername("admin").orElseGet(() -> userAccountRepository.save(UserAccount.builder().username("admin").passwordHash(passwordEncoder.encode("admin1234!")).email("admin@hibiznet.com").isActive(true).build()));
            UserAccount hr = userAccountRepository.findByUsername("hrmanager").orElseGet(() -> userAccountRepository.save(UserAccount.builder().username("hrmanager").passwordHash(passwordEncoder.encode("hr1234!")).email("hr@hibiznet.com").isActive(true).build()));
            UserAccount team = userAccountRepository.findByUsername("teamlead").orElseGet(() -> userAccountRepository.save(UserAccount.builder().username("teamlead").passwordHash(passwordEncoder.encode("team1234!")).email("teamlead@hibiznet.com").isActive(true).build()));

            if (userRoleRepository.findByUserAccount(admin).isEmpty()) userRoleRepository.save(UserRole.builder().userAccount(admin).role(adminRole).build());
            if (userRoleRepository.findByUserAccount(hr).isEmpty()) userRoleRepository.save(UserRole.builder().userAccount(hr).role(hrRole).build());
            if (userRoleRepository.findByUserAccount(team).isEmpty()) userRoleRepository.save(UserRole.builder().userAccount(team).role(teamRole).build());

            Employee adminEmployee = employeeRepository.findByUserAccountId(admin.getId()).orElseGet(() -> employeeRepository.save(Employee.builder().userAccount(admin).employeeNo("E2026001").name("관리자").email("admin@hibiznet.com").phone("010-0000-0000").hireDate(LocalDate.now().minusMonths(6)).statusCode("ACTIVE").employmentTypeCode("REGULAR").department(hq).workTypeCode("OFFICE").build()));
            Employee hrEmployee = employeeRepository.findByUserAccountId(hr.getId()).orElseGet(() -> employeeRepository.save(Employee.builder().userAccount(hr).employeeNo("E2026002").name("인사담당자").email("hr@hibiznet.com").phone("010-0000-0001").hireDate(LocalDate.now().minusMonths(4)).statusCode("ACTIVE").employmentTypeCode("REGULAR").department(hq).workTypeCode("OFFICE").build()));
            Employee teamLead = employeeRepository.findByUserAccountId(team.getId()).orElseGet(() -> employeeRepository.save(Employee.builder().userAccount(team).employeeNo("E2026003").name("개발팀장").email("teamlead@hibiznet.com").phone("010-0000-0002").hireDate(LocalDate.now().minusMonths(5)).statusCode("ACTIVE").employmentTypeCode("REGULAR").department(dev).workTypeCode("OFFICE").build()));
            Employee developer = employeeRepository.findByEmployeeNo("E2026004").orElseGet(() -> employeeRepository.save(Employee.builder().employeeNo("E2026004").name("개발팀원").email("developer1@hibiznet.com").phone("010-0000-0003").hireDate(LocalDate.now().minusMonths(2)).statusCode("ACTIVE").employmentTypeCode("REGULAR").department(dev).manager(teamLead).workTypeCode("HYBRID").build()));

            workPolicyRepository.findAll().stream().filter(p -> "기본 근무제".equals(p.getPolicyName())).findFirst().orElseGet(() -> workPolicyRepository.save(WorkPolicy.builder().policyName("기본 근무제").workTypeCode("OFFICE").standardStartTime(LocalTime.of(9, 0)).standardEndTime(LocalTime.of(18, 0)).breakMinutes(60).isActive(true).build()));

            seedAttendance(attendanceRecordRepository, adminEmployee, LocalDate.now().minusDays(1), 9, 18, 60, 0, "NORMAL", true);
            seedAttendance(attendanceRecordRepository, hrEmployee, LocalDate.now().minusDays(1), 9, 18, 60, 30, "NORMAL", true);
            seedAttendance(attendanceRecordRepository, teamLead, LocalDate.now().minusDays(1), 9, 19, 60, 60, "OVERTIME", true);
            seedAttendance(attendanceRecordRepository, developer, LocalDate.now().minusDays(1), 10, 19, 60, 30, "LATE", true);
            seedAttendance(attendanceRecordRepository, teamLead, LocalDate.now(), 9, 18, 60, 0, "NORMAL", false);
            seedAttendance(attendanceRecordRepository, developer, LocalDate.now(), 10, 18, 60, 0, "REMOTE", false);
        };
    }

    private void seedAttendance(AttendanceRecordRepository attendanceRecordRepository, Employee employee, LocalDate workDate,
                                int startHour, int endHour, int breakMinutes, int overtimeMinutes,
                                String statusCode, boolean isClosed) {
        if (attendanceRecordRepository.existsByEmployeeIdAndWorkDate(employee.getId(), workDate)) return;
        LocalDateTime start = workDate.atTime(startHour, 0);
        LocalDateTime end = workDate.atTime(endHour, 0);
        attendanceRecordRepository.save(AttendanceRecord.builder()
            .employee(employee)
            .workDate(workDate)
            .plannedStartTime(workDate.atTime(9, 0))
            .plannedEndTime(workDate.atTime(18, 0))
            .checkInTime(start)
            .checkOutTime(end)
            .breakMinutes(breakMinutes)
            .overtimeMinutes(overtimeMinutes)
            .attendanceStatusCode(statusCode)
            .isClosed(isClosed)
            .build());
    }
}
