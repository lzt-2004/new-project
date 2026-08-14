CREATE TABLE sales_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    sku_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(19, 2) NOT NULL,
    total_amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sales_order_sku FOREIGN KEY (sku_id) REFERENCES sku (id),
    CONSTRAINT chk_sales_order_quantity CHECK (quantity > 0),
    CONSTRAINT chk_sales_order_total_amount CHECK (total_amount >= 0)
);

CREATE INDEX idx_sales_order_sku_id ON sales_order (sku_id);
CREATE INDEX idx_sales_order_status_created_at ON sales_order (status, created_at);
