CREATE TABLE IF NOT EXISTS tb_group (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS tb_status_page (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description VARCHAR NOT NULL,
    path VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS tb_client (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    url VARCHAR NOT NULL,
    method VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    http_code_for_check_if_service_is_active INT NOT NULL,
    check_period BIGINT NOT NULL,
    timeout_connection BIGINT NOT NULL,
    max_failure_for_check_if_service_is_inactive INT NOT NULL,
    period_for_new_check_after_failure BIGINT NOT NULL,
    group_id INT,
    CONSTRAINT tb_client__tb_group FOREIGN KEY (group_id) REFERENCES tb_group(id),
);

CREATE TABLE IF NOT EXISTS tb_status_page_client (
    status_page_id INT NOT NULL,
    client_id INT NOT NULL,
    PRIMARY KEY (status_page_id, client_id),
    CONSTRAINT tb_status_page_client__tb_status_page FOREIGN KEY (status_page_id) REFERENCES tb_status_page(id),
    CONSTRAINT tb_status_page_client__tb_client FOREIGN KEY (client_id) REFERENCES tb_client(id)
);

CREATE TABLE IF NOT EXISTS tb_status_page_group (
    status_page_id INT NOT NULL,
    group_id INT NOT NULL,
    PRIMARY KEY (status_page_id, group_id),
    CONSTRAINT tb_status_page_group__tb_status_page FOREIGN KEY (status_page_id) REFERENCES tb_status_page(id),
    CONSTRAINT tb_status_page_group__tb_group FOREIGN KEY (group_id) REFERENCES tb_group(id)
);

CREATE TABLE IF NOT EXISTS tb_history (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    client_id INT NOT NULL,
    date_time TIMESTAMP NOT NULL,
    http_response_code INT NOT NULL,
    ping_time BIGINT NOT NULL,
    message VARCHAR NULL,
    active BOOLEAN NOT NULL,
    CONSTRAINT fk_tb_history__tb_client FOREIGN KEY (client_id) REFERENCES tb_client(id)
);

CREATE TABLE IF NOT EXISTS tb_user (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    login VARCHAR NOT NULL,
    password VARCHAR NOT NULL
);