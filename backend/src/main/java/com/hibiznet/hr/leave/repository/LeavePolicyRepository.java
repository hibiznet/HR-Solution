package com.hibiznet.hr.leave.repository;

import com.hibiznet.hr.leave.entity.LeavePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LeavePolicyRepository extends JpaRepository<LeavePolicy, Long>, JpaSpecificationExecutor<LeavePolicy> {
}
