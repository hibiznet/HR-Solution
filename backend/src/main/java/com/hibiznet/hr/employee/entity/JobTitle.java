package com.hibiznet.hr.employee.entity;

import com.hibiznet.hr.common.entity.BaseTimeEntity;
import com.hibiznet.hr.common.entity.YNConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "job_title")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobTitle extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title_code", nullable = false, unique = true, length = 50)
    private String titleCode;

    @Column(name = "title_name", nullable = false, length = 100)
    private String titleName;

    @Column(name = "title_type", nullable = false, length = 50)
    private String titleType;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Convert(converter = YNConverter.class)
    @Column(name = "is_active", nullable = false, columnDefinition = "char(1)")
    private Boolean isActive;
}
