package com.hibiznet.hr.attendance.repository;

import com.hibiznet.hr.attendance.entity.WorkPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorkPolicyRepository extends JpaRepository<WorkPolicy, Long>, JpaSpecificationExecutor<WorkPolicy> {
}
