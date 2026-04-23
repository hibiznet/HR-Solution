package com.hibiznet.hr.code.entity;

import com.hibiznet.hr.common.entity.BaseTimeEntity;
import com.hibiznet.hr.common.entity.YNConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "code_group")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CodeGroup extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_code", nullable = false, unique = true, length = 50)
    private String groupCode;

    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    @Column(name = "description", length = 255)
    private String description;

    @Convert(converter = YNConverter.class)
    @Column(name = "is_active", nullable = false, columnDefinition = "char(1)")
    private Boolean isActive;

    @Builder
    public CodeGroup(String groupCode, String groupName, String description, Boolean isActive) {
        this.groupCode = groupCode;
        this.groupName = groupName;
        this.description = description;
        this.isActive = isActive != null ? isActive : Boolean.TRUE;
    }

    public void update(String groupCode, String groupName, String description, Boolean isActive) {
        this.groupCode = groupCode;
        this.groupName = groupName;
        this.description = description;
        this.isActive = isActive != null ? isActive : Boolean.TRUE;
    }
}
