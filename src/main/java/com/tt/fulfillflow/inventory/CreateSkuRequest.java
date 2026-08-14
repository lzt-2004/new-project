package com.tt.fulfillflow.inventory;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateSkuRequest(
        @NotBlank(message = "must not be blank") String skuCode,
        @NotBlank(message = "must not be blank") String name,
        @NotNull(message = "must not be null")
        @DecimalMin(value = "0.00", inclusive = false, message = "must be greater than 0")
        BigDecimal unitPrice,
        @NotNull(message = "must not be null")
        @PositiveOrZero(message = "must be greater than or equal to 0")
        Integer initialStock
) {
}