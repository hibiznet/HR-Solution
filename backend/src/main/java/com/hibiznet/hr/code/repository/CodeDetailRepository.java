package com.hibiznet.hr.code.repository;

import com.hibiznet.hr.code.entity.CodeDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CodeDetailRepository extends JpaRepository<CodeDetail, Long>, JpaSpecificationExecutor<CodeDetail> {
}
