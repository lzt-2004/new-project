package com.tt.fulfillflow.inventory;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Inventory inventory
            set inventory.availableStock = inventory.availableStock + :quantity,
                inventory.version = inventory.version + 1,
                inventory.updatedAt = :updatedAt
            where inventory.skuId = :skuId
            """)
    int increaseAvailableStock(
            @Param("skuId") Long skuId,
            @Param("quantity") int quantity,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Inventory inventory
            set inventory.availableStock = inventory.availableStock - :quantity,
                inventory.reservedStock = inventory.reservedStock + :quantity,
                inventory.version = inventory.version + 1,
                inventory.updatedAt = :updatedAt
            where inventory.skuId = :skuId
              and inventory.availableStock >= :quantity
            """)
    int reserveAvailableStock(
            @Param("skuId") Long skuId,
            @Param("quantity") int quantity,
            @Param("updatedAt") LocalDateTime updatedAt
    );
}
