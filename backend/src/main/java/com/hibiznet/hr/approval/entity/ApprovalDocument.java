package com.hibiznet.hr.approval.entity;

import com.hibiznet.hr.common.entity.BaseTimeEntity;
import com.hibiznet.hr.employee.entity.Employee;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "approval_document")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalDocument extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_no", nullable = false, unique = true, length = 50)
    private String documentNo;

    @Column(name = "document_type_code", nullable = false, length = 50)
    private String documentTypeCode;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "drafter_employee_id", nullable = false)
    private Employee drafterEmployee;

    @Column(name = "status_code", nullable = false, length = 50)
    private String statusCode;

    @Column(name = "current_step_order")
    private Integer currentStepOrder;

    @Column(name = "final_approved_at")
    private LocalDateTime finalApprovedAt;
}
