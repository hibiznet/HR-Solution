package com.hibiznet.hr.department.repository;

import com.hibiznet.hr.department.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DepartmentRepository extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {
    boolean existsByDeptCode(String deptCode);
    boolean existsByDeptCodeAndIdNot(String deptCode, Long id);
    boolean existsByParentId(Long parentId);
}
