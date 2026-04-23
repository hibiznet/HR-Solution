package com.hibiznet.hr.auth.repository;

import com.hibiznet.hr.auth.entity.UserAccount;
import com.hibiznet.hr.auth.entity.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserAccount(UserAccount userAccount);
    void deleteByUserAccount(UserAccount userAccount);
}
