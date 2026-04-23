package com.hibiznet.hr.employee.repository;

import com.hibiznet.hr.employee.entity.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Optional<Employee> findByEmployeeNo(String employeeNo);
    Optional<Employee> findByUserAccountId(Long userAccountId);
    boolean existsByEmployeeNo(String employeeNo);
    boolean existsByEmail(String email);
    boolean existsByEmployeeNoAndIdNot(String employeeNo, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByDepartmentId(Long departmentId);
}
