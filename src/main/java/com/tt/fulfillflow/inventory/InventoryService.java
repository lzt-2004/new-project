package com.tt.fulfillflow.inventory;

import java.time.LocalDateTime;

import com.tt.fulfillflow.common.ResourceNotFoundException;
import com.tt.fulfillflow.sku.Sku;
import com.tt.fulfillflow.sku.SkuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final SkuRepository skuRepository;
    private final InventoryRepository inventoryRepository;

    public InventoryService(SkuRepository skuRepository, InventoryRepository inventoryRepository) {
        this.skuRepository = skuRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public InventoryResponse createSku(CreateSkuRequest request) {
        Sku sku = skuRepository.save(new Sku(request.skuCode(), request.name(), request.unitPrice()));
        Inventory inventory = inventoryRepository.save(new Inventory(sku.getId(), request.initialStock()));
        return toResponse(sku, inventory);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventory(Long skuId) {
        Sku sku = findSku(skuId);
        return toResponse(sku, findInventory(skuId));
    }

    @Transactional
    public InventoryResponse replenish(Long skuId, int quantity) {
        findSku(skuId);
        int updatedRows = inventoryRepository.increaseAvailableStock(skuId, quantity, LocalDateTime.now());
        if (updatedRows == 0) {
            throw new ResourceNotFoundException("inventory not found for sku " + skuId);
        }
        return getInventory(skuId);
    }

    private Sku findSku(Long skuId) {
        return skuRepository.findById(skuId)
                .orElseThrow(() -> new ResourceNotFoundException("sku not found: " + skuId));
    }

    private Inventory findInventory(Long skuId) {
        return inventoryRepository.findById(skuId)
                .orElseThrow(() -> new ResourceNotFoundException("inventory not found for sku " + skuId));
    }

    private InventoryResponse toResponse(Sku sku, Inventory inventory) {
        return new InventoryResponse(
                sku.getId(),
                sku.getSkuCode(),
                sku.getName(),
                sku.getUnitPrice(),
                inventory.getAvailableStock(),
                inventory.getReservedStock(),
                inventory.getVersion(),
                inventory.getUpdatedAt()
        );
    }
}