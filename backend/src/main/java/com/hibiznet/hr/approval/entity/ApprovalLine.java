package com.hibiznet.hr.approval.entity;

import com.hibiznet.hr.employee.entity.Employee;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "approval_line")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private ApprovalDocument document;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "approver_employee_id", nullable = false)
    private Employee approverEmployee;

    @Column(name = "approver_type_code", nullable = false, length = 50)
    private String approverTypeCode;

    @Column(name = "status_code", nullable = false, length = 50)
    private String statusCode;

    @Column(name = "acted_at")
    private LocalDateTime actedAt;

    @Column(name = "comment", length = 500)
    private String comment;
}
