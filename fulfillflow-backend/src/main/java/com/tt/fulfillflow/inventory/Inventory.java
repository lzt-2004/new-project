package com.tt.fulfillflow.inventory;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @Column(name = "sku_id")
    private Long skuId;

    @Column(name = "available_stock", nullable = false)
    private int availableStock;

    @Column(name = "reserved_stock", nullable = false)
    private int reservedStock;

    @Column(nullable = false)
    private long version;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Inventory() {
    }

    public Inventory(Long skuId, int initialStock) {
        this.skuId = skuId;
        this.availableStock = initialStock;
        this.reservedStock = 0;
        this.version = 0;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getSkuId() { return skuId; }
    public int getAvailableStock() { return availableStock; }
    public int getReservedStock() { return reservedStock; }
    public long getVersion() { return version; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}