package com.hibiznet.hr.auth.entity;

import com.hibiznet.hr.common.entity.BaseTimeEntity;
import com.hibiznet.hr.common.entity.YNConverter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAccount extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Convert(converter = YNConverter.class)
    @Column(name = "is_active", nullable = false, columnDefinition = "char(1)")
    private Boolean isActive;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Builder
    public UserAccount(String username, String passwordHash, String email, Boolean isActive) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.isActive = isActive != null ? isActive : Boolean.TRUE;
    }

    public void update(String username, String email, Boolean isActive) {
        this.username = username;
        this.email = email;
        this.isActive = isActive != null ? isActive : Boolean.TRUE;
    }

    public void updatePasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void updateLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}
