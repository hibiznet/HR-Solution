package com.hibiznet.hr.attendance.repository;

import com.hibiznet.hr.attendance.entity.AttendanceRecord;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long>, JpaSpecificationExecutor<AttendanceRecord> {
    boolean existsByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);
    boolean existsByEmployeeIdAndWorkDateAndIdNot(Long employeeId, LocalDate workDate, Long id);
}
