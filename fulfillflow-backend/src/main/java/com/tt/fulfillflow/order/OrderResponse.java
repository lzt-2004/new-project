package com.tt.fulfillflow.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long orderId,
        String orderNo,
        Long skuId,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalAmount,
        OrderStatus status,
        LocalDateTime createdAt
) {
    public static OrderResponse from(SalesOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNo(),
                order.getSkuId(),
                order.getQuantity(),
                order.getUnitPrice(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
