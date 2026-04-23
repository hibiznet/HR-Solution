CREATE TABLE work_policy (
    id BIGINT NOT NULL AUTO_INCREMENT,
    policy_name VARCHAR(100) NOT NULL,
    work_type_code VARCHAR(50) NOT NULL,
    standard_start_time TIME NULL,
    standard_end_time TIME NULL,
    break_minutes INT NOT NULL DEFAULT 0,
    is_active CHAR(1) NOT NULL DEFAULT 'Y',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_work_policy PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE attendance_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    work_date DATE NOT NULL,
    planned_start_time DATETIME NULL,
    planned_end_time DATETIME NULL,
    check_in_time DATETIME NULL,
    check_out_time DATETIME NULL,
    break_minutes INT NOT NULL DEFAULT 0,
    total_work_minutes INT NOT NULL DEFAULT 0,
    overtime_minutes INT NOT NULL DEFAULT 0,
    attendance_status_code VARCHAR(50) NOT NULL,
    is_closed CHAR(1) NOT NULL DEFAULT 'N',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_attendance_record PRIMARY KEY (id),
    CONSTRAINT uk_attendance_employee_work_date UNIQUE (employee_id, work_date),
    CONSTRAINT fk_attendance_record_employee_id FOREIGN KEY (employee_id) REFERENCES employee(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE leave_policy (
    id BIGINT NOT NULL AUTO_INCREMENT,
    policy_name VARCHAR(100) NOT NULL,
    accrual_basis_code VARCHAR(50) NOT NULL,
    monthly_leave_for_first_year CHAR(1) NOT NULL DEFAULT 'N',
    annual_leave_days DECIMAL(6,2) NULL,
    is_active CHAR(1) NOT NULL DEFAULT 'Y',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_leave_policy PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE leave_balance (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    leave_type_code VARCHAR(50) NOT NULL,
    base_year INT NOT NULL,
    granted_days DECIMAL(6,2) NOT NULL DEFAULT 0,
    used_days DECIMAL(6,2) NOT NULL DEFAULT 0,
    remaining_days DECIMAL(6,2) NOT NULL DEFAULT 0,
    adjusted_days DECIMAL(6,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_leave_balance PRIMARY KEY (id),
    CONSTRAINT uk_leave_balance_employee_type_year UNIQUE (employee_id, leave_type_code, base_year),
    CONSTRAINT fk_leave_balance_employee_id FOREIGN KEY (employee_id) REFERENCES employee(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE leave_request (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    leave_type_code VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    start_time TIME NULL,
    end_time TIME NULL,
    days_count DECIMAL(5,2) NOT NULL,
    reason VARCHAR(255) NULL,
    status_code VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_leave_request PRIMARY KEY (id),
    CONSTRAINT fk_leave_request_employee_id FOREIGN KEY (employee_id) REFERENCES employee(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE approval_document (
    id BIGINT NOT NULL AUTO_INCREMENT,
    document_no VARCHAR(50) NOT NULL,
    document_type_code VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    drafter_employee_id BIGINT NOT NULL,
    status_code VARCHAR(50) NOT NULL,
    current_step_order INT NULL,
    final_approved_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_approval_document PRIMARY KEY (id),
    CONSTRAINT uk_approval_document_document_no UNIQUE (document_no),
    CONSTRAINT fk_approval_document_drafter_employee_id FOREIGN KEY (drafter_employee_id) REFERENCES employee(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE approval_line (
    id BIGINT NOT NULL AUTO_INCREMENT,
    document_id BIGINT NOT NULL,
    step_order INT NOT NULL,
    approver_employee_id BIGINT NOT NULL,
    approver_type_code VARCHAR(50) NOT NULL,
    status_code VARCHAR(50) NOT NULL,
    acted_at TIMESTAMP NULL,
    comment VARCHAR(500) NULL,
    CONSTRAINT pk_approval_line PRIMARY KEY (id),
    CONSTRAINT fk_approval_line_document_id FOREIGN KEY (document_id) REFERENCES approval_document(id),
    CONSTRAINT fk_approval_line_approver_employee_id FOREIGN KEY (approver_employee_id) REFERENCES employee(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE file_attachment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    original_name VARCHAR(255) NOT NULL,
    stored_name VARCHAR(255) NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    mime_type VARCHAR(100) NULL,
    file_size BIGINT NOT NULL,
    uploaded_by_employee_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_file_attachment PRIMARY KEY (id),
    CONSTRAINT fk_file_attachment_uploaded_by_employee_id FOREIGN KEY (uploaded_by_employee_id) REFERENCES employee(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE notification (
    id BIGINT NOT NULL AUTO_INCREMENT,
    receiver_employee_id BIGINT NOT NULL,
    notification_type_code VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    ref_table VARCHAR(50) NULL,
    ref_id BIGINT NULL,
    is_read CHAR(1) NOT NULL DEFAULT 'N',
    read_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_notification PRIMARY KEY (id),
    CONSTRAINT fk_notification_receiver_employee_id FOREIGN KEY (receiver_employee_id) REFERENCES employee(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE audit_log (
    id BIGINT NOT NULL AUTO_INCREMENT,
    actor_user_id BIGINT NULL,
    actor_employee_id BIGINT NULL,
    action_type VARCHAR(50) NOT NULL,
    target_table VARCHAR(100) NOT NULL,
    target_id BIGINT NOT NULL,
    before_data_json LONGTEXT NULL,
    after_data_json LONGTEXT NULL,
    ip_address VARCHAR(50) NULL,
    user_agent VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_audit_log PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
