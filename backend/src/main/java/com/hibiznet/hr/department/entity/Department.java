package com.hibiznet.hr.department.entity;

import com.hibiznet.hr.common.entity.BaseTimeEntity;
import com.hibiznet.hr.common.entity.YNConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "department")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Department parent;

    @Column(name = "dept_code", nullable = false, unique = true, length = 50)
    private String deptCode;

    @Column(name = "dept_name", nullable = false, length = 100)
    private String deptName;

    @Column(name = "dept_type", length = 50)
    private String deptType;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Convert(converter = YNConverter.class)
    @Column(name = "is_active", nullable = false, columnDefinition = "char(1)")
    private Boolean isActive;

    @Builder
    public Department(Department parent, String deptCode, String deptName, String deptType, Integer sortOrder, Boolean isActive) {
        this.parent = parent;
        this.deptCode = deptCode;
        this.deptName = deptName;
        this.deptType = deptType;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.isActive = isActive != null ? isActive : Boolean.TRUE;
    }

    public void update(Department parent, String deptCode, String deptName, String deptType, Integer sortOrder, Boolean isActive) {
        this.parent = parent;
        this.deptCode = deptCode;
        this.deptName = deptName;
        this.deptType = deptType;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.isActive = isActive != null ? isActive : Boolean.TRUE;
    }
}
