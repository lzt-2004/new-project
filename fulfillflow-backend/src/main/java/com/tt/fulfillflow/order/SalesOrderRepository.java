package com.tt.fulfillflow.order;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select salesOrder from SalesOrder salesOrder where salesOrder.id = :orderId")
    java.util.Optional<SalesOrder> findByIdForUpdate(@Param("orderId") Long orderId);

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

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update SalesOrder salesOrder
            set salesOrder.status = :paidStatus,
                salesOrder.updatedAt = :updatedAt
            where salesOrder.id = :orderId
              and salesOrder.status = :pendingStatus
            """)
    int payIfPending(
            @Param("orderId") Long orderId,
            @Param("pendingStatus") OrderStatus pendingStatus,
            @Param("paidStatus") OrderStatus paidStatus,
            @Param("updatedAt") LocalDateTime updatedAt
    );
}
