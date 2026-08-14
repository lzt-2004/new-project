package com.tt.fulfillflow.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(
        @NotNull @Positive Long skuId,
        @Positive int quantity
) {
}
