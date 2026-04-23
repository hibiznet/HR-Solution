package com.hibiznet.hr.employee.entity;

import com.hibiznet.hr.auth.entity.UserAccount;
import com.hibiznet.hr.common.entity.BaseTimeEntity;
import com.hibiznet.hr.department.entity.Department;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "employee", indexes = {
    @Index(name = "idx_employee_department_id", columnList = "department_id"),
    @Index(name = "idx_employee_manager_employee_id", columnList = "manager_employee_id"),
    @Index(name = "idx_employee_status_code", columnList = "status_code")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAccount userAccount;

    @Column(name = "employee_no", nullable = false, unique = true, length = 50)
    private String employeeNo;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "resign_date")
    private LocalDate resignDate;

    @Column(name = "status_code", nullable = false, length = 50)
    private String statusCode;

    @Column(name = "employment_type_code", nullable = false, length = 50)
    private String employmentTypeCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_title_id")
    private JobTitle jobTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_employee_id")
    private Employee manager;

    @Column(name = "probation_end_date")
    private LocalDate probationEndDate;

    @Column(name = "work_type_code", length = 50)
    private String workTypeCode;

    @Builder
    public Employee(UserAccount userAccount, String employeeNo, String name, String email, String phone, LocalDate hireDate,
                    LocalDate resignDate, String statusCode, String employmentTypeCode, Department department,
                    JobTitle jobTitle, Employee manager, LocalDate probationEndDate, String workTypeCode) {
        this.userAccount = userAccount;
        this.employeeNo = employeeNo;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.hireDate = hireDate;
        this.resignDate = resignDate;
        this.statusCode = statusCode;
        this.employmentTypeCode = employmentTypeCode;
        this.department = department;
        this.jobTitle = jobTitle;
        this.manager = manager;
        this.probationEndDate = probationEndDate;
        this.workTypeCode = workTypeCode;
    }

    public void update(String employeeNo, String name, String email, String phone, LocalDate hireDate,
                       LocalDate resignDate, String statusCode, String employmentTypeCode, Department department,
                       Employee manager, LocalDate probationEndDate, String workTypeCode) {
        this.employeeNo = employeeNo;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.hireDate = hireDate;
        this.resignDate = resignDate;
        this.statusCode = statusCode;
        this.employmentTypeCode = employmentTypeCode;
        this.department = department;
        this.manager = manager;
        this.probationEndDate = probationEndDate;
        this.workTypeCode = workTypeCode;
    }
}
