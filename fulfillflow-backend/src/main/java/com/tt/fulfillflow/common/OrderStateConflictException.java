package com.tt.fulfillflow.common;

import com.tt.fulfillflow.order.OrderStatus;

public class OrderStateConflictException extends RuntimeException {

    public OrderStateConflictException(Long orderId, OrderStatus currentStatus, String targetAction) {
        super("order " + orderId + " cannot be " + targetAction + " when status is " + currentStatus);
    }
}
