CREATE TABLE code_group (
    id BIGINT NOT NULL AUTO_INCREMENT,
    group_code VARCHAR(50) NOT NULL,
    group_name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL,
    is_active CHAR(1) NOT NULL DEFAULT 'Y',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_code_group PRIMARY KEY (id),
    CONSTRAINT uk_code_group_group_code UNIQUE (group_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE code_detail (
    id BIGINT NOT NULL AUTO_INCREMENT,
    group_id BIGINT NOT NULL,
    detail_code VARCHAR(50) NOT NULL,
    detail_name VARCHAR(100) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    is_active CHAR(1) NOT NULL DEFAULT 'Y',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_code_detail PRIMARY KEY (id),
    CONSTRAINT uk_code_detail_group_code UNIQUE (group_id, detail_code),
    CONSTRAINT fk_code_detail_group_id FOREIGN KEY (group_id) REFERENCES code_group(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE user_account (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL,
    is_active CHAR(1) NOT NULL DEFAULT 'Y',
    last_login_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_user_account PRIMARY KEY (id),
    CONSTRAINT uk_user_account_username UNIQUE (username),
    CONSTRAINT uk_user_account_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE role (
    id BIGINT NOT NULL AUTO_INCREMENT,
    role_code VARCHAR(50) NOT NULL,
    role_name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL,
    CONSTRAINT pk_role PRIMARY KEY (id),
    CONSTRAINT uk_role_role_code UNIQUE (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE user_role (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT pk_user_role PRIMARY KEY (id),
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id),
    CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES user_account(id),
    CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE department (
    id BIGINT NOT NULL AUTO_INCREMENT,
    parent_id BIGINT NULL,
    dept_code VARCHAR(50) NOT NULL,
    dept_name VARCHAR(100) NOT NULL,
    dept_type VARCHAR(50) NULL,
    sort_order INT NOT NULL DEFAULT 0,
    is_active CHAR(1) NOT NULL DEFAULT 'Y',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_department PRIMARY KEY (id),
    CONSTRAINT uk_department_dept_code UNIQUE (dept_code),
    CONSTRAINT fk_department_parent_id FOREIGN KEY (parent_id) REFERENCES department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE job_title (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title_code VARCHAR(50) NOT NULL,
    title_name VARCHAR(100) NOT NULL,
    title_type VARCHAR(50) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    is_active CHAR(1) NOT NULL DEFAULT 'Y',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_job_title PRIMARY KEY (id),
    CONSTRAINT uk_job_title_title_code UNIQUE (title_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE employee (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NULL,
    employee_no VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(30) NULL,
    hire_date DATE NOT NULL,
    resign_date DATE NULL,
    status_code VARCHAR(50) NOT NULL,
    employment_type_code VARCHAR(50) NOT NULL,
    department_id BIGINT NOT NULL,
    job_title_id BIGINT NULL,
    manager_employee_id BIGINT NULL,
    probation_end_date DATE NULL,
    work_type_code VARCHAR(50) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_employee PRIMARY KEY (id),
    CONSTRAINT uk_employee_employee_no UNIQUE (employee_no),
    CONSTRAINT uk_employee_email UNIQUE (email),
    CONSTRAINT fk_employee_user_id FOREIGN KEY (user_id) REFERENCES user_account(id),
    CONSTRAINT fk_employee_department_id FOREIGN KEY (department_id) REFERENCES department(id),
    CONSTRAINT fk_employee_job_title_id FOREIGN KEY (job_title_id) REFERENCES job_title(id),
    CONSTRAINT fk_employee_manager_employee_id FOREIGN KEY (manager_employee_id) REFERENCES employee(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE INDEX idx_employee_department_id ON employee(department_id);
CREATE INDEX idx_employee_manager_employee_id ON employee(manager_employee_id);
CREATE INDEX idx_employee_status_code ON employee(status_code);
