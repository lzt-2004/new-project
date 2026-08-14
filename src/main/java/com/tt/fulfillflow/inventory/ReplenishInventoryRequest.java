package com.tt.fulfillflow.inventory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReplenishInventoryRequest(
        @NotNull(message = "must not be null")
        @Positive(message = "must be greater than 0")
        Integer quantity
) {
}