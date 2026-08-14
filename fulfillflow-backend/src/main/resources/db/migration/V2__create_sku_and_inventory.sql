CREATE TABLE sku (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sku_code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    unit_price DECIMAL(19, 2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inventory (
    sku_id BIGINT PRIMARY KEY,
    available_stock INT NOT NULL DEFAULT 0,
    reserved_stock INT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventory_sku FOREIGN KEY (sku_id) REFERENCES sku (id),
    CONSTRAINT chk_inventory_available_stock CHECK (available_stock >= 0),
    CONSTRAINT chk_inventory_reserved_stock CHECK (reserved_stock >= 0)
);