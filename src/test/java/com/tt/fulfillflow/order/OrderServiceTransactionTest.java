package com.tt.fulfillflow.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;

import com.tt.fulfillflow.inventory.Inventory;
import com.tt.fulfillflow.inventory.InventoryRepository;
import com.tt.fulfillflow.sku.Sku;
import com.tt.fulfillflow.sku.SkuRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceTransactionTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SkuRepository skuRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @MockitoBean
    private OrderNumberGenerator orderNumberGenerator;

    @Test
    void orderSaveFailureShouldRollBackInventoryReservation() {
        Sku firstSku = createSkuWithInventory("ROLLBACK-FIRST", 5);
        Sku secondSku = createSkuWithInventory("ROLLBACK-SECOND", 5);
        given(orderNumberGenerator.nextOrderNo()).willReturn("FF-ROLLBACK-DUPLICATE");

        orderService.createOrder(new CreateOrderRequest(firstSku.getId(), 1));

        assertThatThrownBy(() -> orderService.createOrder(new CreateOrderRequest(secondSku.getId(), 3)))
                .isInstanceOf(DataIntegrityViolationException.class);

        Inventory inventoryAfterFailure = inventoryRepository.findById(secondSku.getId()).orElseThrow();
        assertThat(inventoryAfterFailure.getAvailableStock()).isEqualTo(5);
        assertThat(inventoryAfterFailure.getReservedStock()).isZero();
        assertThat(inventoryAfterFailure.getVersion()).isZero();
    }

    private Sku createSkuWithInventory(String skuCode, int initialStock) {
        Sku sku = skuRepository.save(new Sku(skuCode, "Rollback test SKU", new BigDecimal("10.00")));
        inventoryRepository.save(new Inventory(sku.getId(), initialStock));
        return sku;
    }
}
