package com.hibiznet.hr.auth.repository;

import com.hibiznet.hr.auth.entity.Role;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleCode(String roleCode);
    List<Role> findByRoleCodeIn(Collection<String> roleCodes);
}
