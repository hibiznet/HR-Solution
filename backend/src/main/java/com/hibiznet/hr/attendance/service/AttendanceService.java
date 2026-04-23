package com.hibiznet.hr.attendance.service;

import com.hibiznet.hr.attendance.dto.request.AttendanceUpsertRequest;
import com.hibiznet.hr.attendance.dto.response.AttendanceResponse;
import com.hibiznet.hr.attendance.entity.AttendanceRecord;
import com.hibiznet.hr.attendance.repository.AttendanceRecordRepository;
import com.hibiznet.hr.auth.security.AuthenticatedUser;
import com.hibiznet.hr.common.audit.annotation.Auditable;
import com.hibiznet.hr.common.dto.PageResponse;
import com.hibiznet.hr.common.security.CurrentUserService;
import com.hibiznet.hr.employee.entity.Employee;
import com.hibiznet.hr.employee.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
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
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EmployeeRepository employeeRepository;
    private final CurrentUserService currentUserService;

    public PageResponse<AttendanceResponse> getAttendanceRecords(AuthenticatedUser actor, String keyword, Long employeeId,
                                                                 Long departmentId, String attendanceStatusCode,
                                                                 Boolean isClosed, LocalDate startDate, LocalDate endDate,
                                                                 int page, int size) {
        if (employeeId != null) {
            Employee targetEmployee = findEmployee(employeeId);
            currentUserService.assertCanReadAttendance(actor, targetEmployee);
        }
        if (departmentId != null) currentUserService.assertCanReadDepartment(actor, departmentId);

        Employee me = currentUserService.getEmployeeOrThrow(actor);
        final Long scopedDepartmentId;
        final Long scopedEmployeeId;
        if (currentUserService.hasAnyRole(actor, "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_SYS_ADMIN")) {
            scopedDepartmentId = departmentId;
            scopedEmployeeId = employeeId;
        } else if (currentUserService.hasAnyRole(actor, "ROLE_TEAM_MANAGER")) {
            scopedDepartmentId = departmentId != null ? departmentId : me.getDepartment().getId();
            scopedEmployeeId = employeeId;
        } else {
            scopedDepartmentId = me.getDepartment().getId();
            scopedEmployeeId = me.getId();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "workDate").and(Sort.by(Sort.Direction.DESC, "id")));
        return PageResponse.from(attendanceRecordRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("employee").get("employeeNo")), likeKeyword),
                    cb.like(cb.lower(root.get("employee").get("name")), likeKeyword),
                    cb.like(cb.lower(root.get("employee").get("department").get("deptName")), likeKeyword)
                ));
            }
            if (scopedEmployeeId != null) predicates.add(cb.equal(root.get("employee").get("id"), scopedEmployeeId));
            if (scopedDepartmentId != null) predicates.add(cb.equal(root.get("employee").get("department").get("id"), scopedDepartmentId));
            if (attendanceStatusCode != null && !attendanceStatusCode.isBlank()) predicates.add(cb.equal(root.get("attendanceStatusCode"), attendanceStatusCode));
            if (isClosed != null) predicates.add(cb.equal(root.get("isClosed"), isClosed));
            if (startDate != null) predicates.add(cb.greaterThanOrEqualTo(root.get("workDate"), startDate));
            if (endDate != null) predicates.add(cb.lessThanOrEqualTo(root.get("workDate"), endDate));
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(this::toResponse));
    }

    public AttendanceResponse getAttendanceRecord(AuthenticatedUser actor, Long id) {
        AttendanceRecord attendanceRecord = findAttendanceRecord(id);
        currentUserService.assertCanReadAttendance(actor, attendanceRecord.getEmployee());
        return toResponse(attendanceRecord);
    }

    @Transactional
    @Auditable(action = "CREATE", target = "attendance_record")
    public AttendanceResponse createAttendanceRecord(AuthenticatedUser actor, AttendanceUpsertRequest request) {
        Employee employee = findEmployee(request.employeeId());
        currentUserService.assertCanManageAttendance(actor, employee);
        validateDuplicateForCreate(request.employeeId(), request.workDate());
        AttendanceRecord attendanceRecord = AttendanceRecord.builder()
            .employee(employee)
            .workDate(request.workDate())
            .plannedStartTime(request.plannedStartTime())
            .plannedEndTime(request.plannedEndTime())
            .checkInTime(request.checkInTime())
            .checkOutTime(request.checkOutTime())
            .breakMinutes(request.breakMinutes())
            .overtimeMinutes(request.overtimeMinutes())
            .attendanceStatusCode(request.attendanceStatusCode())
            .isClosed(request.isClosed())
            .build();
        return toResponse(attendanceRecordRepository.save(attendanceRecord));
    }

    @Transactional
    @Auditable(action = "UPDATE", target = "attendance_record")
    public AttendanceResponse updateAttendanceRecord(AuthenticatedUser actor, Long id, AttendanceUpsertRequest request) {
        AttendanceRecord attendanceRecord = findAttendanceRecord(id);
        Employee employee = findEmployee(request.employeeId());
        currentUserService.assertCanManageAttendance(actor, employee);
        validateDuplicateForUpdate(id, request.employeeId(), request.workDate());
        attendanceRecord.update(employee, request.workDate(), request.plannedStartTime(), request.plannedEndTime(), request.checkInTime(), request.checkOutTime(), request.breakMinutes(), request.overtimeMinutes(), request.attendanceStatusCode(), request.isClosed());
        return toResponse(attendanceRecord);
    }

    @Transactional
    @Auditable(action = "DELETE", target = "attendance_record")
    public void deleteAttendanceRecord(AuthenticatedUser actor, Long id) {
        AttendanceRecord attendanceRecord = findAttendanceRecord(id);
        currentUserService.assertCanManageAttendance(actor, attendanceRecord.getEmployee());
        attendanceRecordRepository.delete(attendanceRecord);
    }

    private void validateDuplicateForCreate(Long employeeId, LocalDate workDate) {
        if (attendanceRecordRepository.existsByEmployeeIdAndWorkDate(employeeId, workDate)) {
            throw new IllegalArgumentException("해당 직원의 같은 근무일 근태 기록이 이미 존재합니다.");
        }
    }

    private void validateDuplicateForUpdate(Long id, Long employeeId, LocalDate workDate) {
        if (attendanceRecordRepository.existsByEmployeeIdAndWorkDateAndIdNot(employeeId, workDate, id)) {
            throw new IllegalArgumentException("해당 직원의 같은 근무일 근태 기록이 이미 존재합니다.");
        }
    }

    private AttendanceRecord findAttendanceRecord(Long id) {
        return attendanceRecordRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("근태 기록을 찾을 수 없습니다."));
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다."));
    }

    private AttendanceResponse toResponse(AttendanceRecord record) {
        Employee employee = record.getEmployee();
        return new AttendanceResponse(
            record.getId(),
            employee.getId(),
            employee.getEmployeeNo(),
            employee.getName(),
            employee.getDepartment() != null ? employee.getDepartment().getId() : null,
            employee.getDepartment() != null ? employee.getDepartment().getDeptName() : null,
            record.getWorkDate(),
            record.getPlannedStartTime(),
            record.getPlannedEndTime(),
            record.getCheckInTime(),
            record.getCheckOutTime(),
            record.getBreakMinutes(),
            record.getTotalWorkMinutes(),
            record.getOvertimeMinutes(),
            record.getAttendanceStatusCode(),
            record.getIsClosed()
        );
    }
}
