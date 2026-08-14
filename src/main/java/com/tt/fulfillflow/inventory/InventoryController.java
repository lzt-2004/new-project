package com.tt.fulfillflow.inventory;

import com.tt.fulfillflow.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/skus")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InventoryResponse> createSku(@Valid @RequestBody CreateSkuRequest request) {
        return ApiResponse.success(inventoryService.createSku(request));
    }

    @GetMapping("/{skuId}/inventory")
    public ApiResponse<InventoryResponse> getInventory(@PathVariable Long skuId) {
        return ApiResponse.success(inventoryService.getInventory(skuId));
    }

    @PostMapping("/{skuId}/inventory/replenishments")
    public ApiResponse<InventoryResponse> replenish(
            @PathVariable Long skuId,
            @Valid @RequestBody ReplenishInventoryRequest request
    ) {
        return ApiResponse.success(inventoryService.replenish(skuId, request.quantity()));
    }
}