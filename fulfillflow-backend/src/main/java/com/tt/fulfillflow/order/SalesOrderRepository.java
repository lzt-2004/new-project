package com.tt.fulfillflow.order;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update SalesOrder salesOrder
            set salesOrder.status = :cancelledStatus,
                salesOrder.updatedAt = :updatedAt
            where salesOrder.id = :orderId
              and salesOrder.status = :pendingStatus
            """)
    int cancelIfPending(
            @Param("orderId") Long orderId,
            @Param("pendingStatus") OrderStatus pendingStatus,
            @Param("cancelledStatus") OrderStatus cancelledStatus,
            @Param("updatedAt") LocalDateTime updatedAt
    );
}
