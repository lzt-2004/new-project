CREATE TABLE app_metadata (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    meta_key VARCHAR(100) NOT NULL UNIQUE,
    meta_value VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO app_metadata (meta_key, meta_value) VALUES ('schema_version', 'V1');
