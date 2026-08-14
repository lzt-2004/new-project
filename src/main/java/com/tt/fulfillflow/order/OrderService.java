package com.tt.fulfillflow.order;

import java.time.LocalDateTime;

import com.tt.fulfillflow.common.InsufficientStockException;
import com.tt.fulfillflow.common.ResourceNotFoundException;
import com.tt.fulfillflow.inventory.InventoryRepository;
import com.tt.fulfillflow.sku.Sku;
import com.tt.fulfillflow.sku.SkuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final SkuRepository skuRepository;
    private final InventoryRepository inventoryRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final OrderNumberGenerator orderNumberGenerator;

    public OrderService(
            SkuRepository skuRepository,
            InventoryRepository inventoryRepository,
            SalesOrderRepository salesOrderRepository,
            OrderNumberGenerator orderNumberGenerator
    ) {
        this.skuRepository = skuRepository;
        this.inventoryRepository = inventoryRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.orderNumberGenerator = orderNumberGenerator;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Sku sku = skuRepository.findById(request.skuId())
                .orElseThrow(() -> new ResourceNotFoundException("sku not found: " + request.skuId()));

        LocalDateTime now = LocalDateTime.now();
        int updatedRows = inventoryRepository.reserveAvailableStock(request.skuId(), request.quantity(), now);
        if (updatedRows == 0) {
            throw new InsufficientStockException(request.skuId());
        }

        SalesOrder order = new SalesOrder(
                orderNumberGenerator.nextOrderNo(),
                sku.getId(),
                request.quantity(),
                sku.getUnitPrice(),
                now
        );
        return OrderResponse.from(salesOrderRepository.saveAndFlush(order));
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("order not found: " + orderId));

        LocalDateTime now = LocalDateTime.now();
        int cancelledRows = salesOrderRepository.cancelIfPending(
                orderId,
                OrderStatus.PENDING,
                OrderStatus.CANCELLED,
                now
        );
        if (cancelledRows == 0) {
            return OrderResponse.from(salesOrderRepository.findById(orderId).orElseThrow());
        }

        int releasedRows = inventoryRepository.releaseReservedStock(order.getSkuId(), order.getQuantity(), now);
        if (releasedRows == 0) {
            throw new IllegalStateException("reserved stock is inconsistent for order " + orderId);
        }
        return OrderResponse.from(salesOrderRepository.findById(orderId).orElseThrow());
    }
}
