package com.hibiznet.hr.code.entity;

import com.hibiznet.hr.common.entity.BaseTimeEntity;
import com.hibiznet.hr.common.entity.YNConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "code_detail", uniqueConstraints = {
    @UniqueConstraint(name = "uk_code_detail_group_code", columnNames = {"group_id", "detail_code"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CodeDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private CodeGroup codeGroup;

    @Column(name = "detail_code", nullable = false, length = 50)
    private String detailCode;

    @Column(name = "detail_name", nullable = false, length = 100)
    private String detailName;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Convert(converter = YNConverter.class)
    @Column(name = "is_active", nullable = false, columnDefinition = "char(1)")
    private Boolean isActive;

    @Builder
    public CodeDetail(CodeGroup codeGroup, String detailCode, String detailName, Integer sortOrder, Boolean isActive) {
        this.codeGroup = codeGroup;
        this.detailCode = detailCode;
        this.detailName = detailName;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.isActive = isActive != null ? isActive : Boolean.TRUE;
    }

    public void update(CodeGroup codeGroup, String detailCode, String detailName, Integer sortOrder, Boolean isActive) {
        this.codeGroup = codeGroup;
        this.detailCode = detailCode;
        this.detailName = detailName;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.isActive = isActive != null ? isActive : Boolean.TRUE;
    }
}
