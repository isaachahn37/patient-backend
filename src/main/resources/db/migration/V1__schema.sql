
CREATE TABLE IF NOT EXISTS addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    address  VARCHAR(255) NOT NULL,
    suburb   VARCHAR(128) NOT NULL,
    state    VARCHAR(32)  NOT NULL,
    postcode VARCHAR(16)  NOT NULL
);

CREATE TABLE IF NOT EXISTS patients (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pid VARCHAR(64) NOT NULL UNIQUE,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender        VARCHAR(10) NOT NULL,
    phone         VARCHAR(32) NOT NULL,
    address_id    BIGINT NULL,   -- NULL for seeding; add FK later
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_patients_name (last_name, first_name)   -- << add it here
);
