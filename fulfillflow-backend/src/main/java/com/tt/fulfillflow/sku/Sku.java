package com.tt.fulfillflow.sku;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sku")
public class Sku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku_code", nullable = false, unique = true, length = 64)
    private String skuCode;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Sku() {
    }

    public Sku(String skuCode, String name, BigDecimal unitPrice) {
        this.skuCode = skuCode;
        this.name = name;
        this.unitPrice = unitPrice;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getSkuCode() { return skuCode; }
    public String getName() { return name; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}