package com.hibiznet.hr.code.repository;

import com.hibiznet.hr.code.entity.CodeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CodeGroupRepository extends JpaRepository<CodeGroup, Long>, JpaSpecificationExecutor<CodeGroup> {
}
