CREATE TABLE IF NOT EXISTS tb_group (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS tb_status_page (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(500) NOT NULL,
    path VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS tb_client (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    method VARCHAR(10) NOT NULL,
    name VARCHAR(255) NOT NULL,
    http_code_for_check_if_service_is_active INTEGER NOT NULL,
    check_period BIGINT NOT NULL,
    timeout_connection BIGINT NOT NULL,
    max_failure_for_check_if_service_is_inactive INTEGER NOT NULL,
    period_for_new_check_after_failure BIGINT NOT NULL,
    group_id INTEGER,
    CONSTRAINT tb_client__tb_group FOREIGN KEY (group_id) REFERENCES tb_group(id)
);

CREATE TABLE IF NOT EXISTS tb_status_page_group (
    status_page_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    CONSTRAINT pk_status_page_group PRIMARY KEY (status_page_id, group_id),
    CONSTRAINT tb_status_page_group__tb_status_page FOREIGN KEY (status_page_id) REFERENCES tb_status_page(id),
    CONSTRAINT tb_status_page_group__tb_group FOREIGN KEY (group_id) REFERENCES tb_group(id)
);

CREATE TABLE IF NOT EXISTS tb_status_page_client (
    status_page_id INTEGER NOT NULL,
    client_id INTEGER NOT NULL,
    CONSTRAINT pk_status_page_client PRIMARY KEY (status_page_id, client_id),
    CONSTRAINT tb_status_page_client__tb_status_page FOREIGN KEY (status_page_id) REFERENCES tb_status_page(id),
    CONSTRAINT tb_status_page_client__tb_client FOREIGN KEY (client_id) REFERENCES tb_client(id)
);

CREATE TABLE IF NOT EXISTS tb_history (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    client_id INTEGER NOT NULL,
    date_time TIMESTAMP NOT NULL,
    http_response_code INTEGER NOT NULL,
    ping_time BIGINT NOT NULL,
    message VARCHAR(1000) NULL,
    active BOOLEAN NOT NULL,
    CONSTRAINT pk_history PRIMARY KEY (id),
    CONSTRAINT fk_tb_history__tb_client FOREIGN KEY (client_id) REFERENCES tb_client(id)
);

CREATE TABLE IF NOT EXISTS tb_user (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(255) NOT NULL,
    password VARCHAR(3000) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tb_token (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    token_id VARCHAR(100) NOT NULL,
    renovation_key VARCHAR(100) NOT NULL
);