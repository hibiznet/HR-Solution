package com.hibiznet.hr.notification.entity;

import com.hibiznet.hr.common.entity.BaseTimeEntity;
import com.hibiznet.hr.common.entity.YNConverter;
import com.hibiznet.hr.employee.entity.Employee;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_employee_id", nullable = false)
    private Employee receiverEmployee;

    @Column(name = "notification_type_code", nullable = false, length = 50)
    private String notificationTypeCode;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "ref_table", length = 50)
    private String refTable;

    @Column(name = "ref_id")
    private Long refId;

    @Convert(converter = YNConverter.class)
    @Column(name = "is_read", nullable = false, columnDefinition = "char(1)")
    private Boolean isRead;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}
