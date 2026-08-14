package com.tt.fulfillflow.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InventoryResponse(
        Long skuId,
        String skuCode,
        String name,
        BigDecimal unitPrice,
        int availableStock,
        int reservedStock,
        long version,
        LocalDateTime updatedAt
) {
}