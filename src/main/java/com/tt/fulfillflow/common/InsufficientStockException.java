package com.tt.fulfillflow.common;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long skuId) {
        super("insufficient available stock for sku " + skuId);
    }
}
