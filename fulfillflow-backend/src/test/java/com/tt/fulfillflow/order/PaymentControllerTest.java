package com.tt.fulfillflow.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void payPendingOrderShouldKeepInventoryUnchanged() throws Exception {
        long skuId = createSku("PAY-SUCCESS-100", 5);
        long orderId = createOrder(skuId, 3);

        mockMvc.perform(post("/api/orders/{orderId}/payments", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.status").value("PAID"));

        assertInventory(skuId, 2, 3, 1);
    }

    @Test
    void repeatedPaymentShouldBeIdempotentAndKeepInventoryUnchanged() throws Exception {
        long skuId = createSku("PAY-REPEAT-100", 5);
        long orderId = createOrder(skuId, 3);

        mockMvc.perform(post("/api/orders/{orderId}/payments", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));

        mockMvc.perform(post("/api/orders/{orderId}/payments", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));

        assertInventory(skuId, 2, 3, 1);
    }

    @Test
    void payCancelledOrderShouldReturnConflict() throws Exception {
        long skuId = createSku("PAY-CANCELLED-100", 5);
        long orderId = createOrder(skuId, 3);

        mockMvc.perform(post("/api/orders/{orderId}/cancellations", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        mockMvc.perform(post("/api/orders/{orderId}/payments", orderId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(40902));

        assertInventory(skuId, 5, 0, 2);
    }

    @Test
    void cancelPaidOrderShouldReturnConflict() throws Exception {
        long skuId = createSku("CANCEL-PAID-100", 5);
        long orderId = createOrder(skuId, 3);

        mockMvc.perform(post("/api/orders/{orderId}/payments", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));

        mockMvc.perform(post("/api/orders/{orderId}/cancellations", orderId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(40902));

        assertInventory(skuId, 2, 3, 1);
    }

    private long createSku(String skuCode, int initialStock) throws Exception {
        String response = mockMvc.perform(post("/api/skus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"skuCode\":\"" + skuCode
                                + "\",\"name\":\"Payment tea\",\"unitPrice\":12.50,\"initialStock\":"
                                + initialStock + "}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return ((Number) JsonPath.read(response, "$.data.skuId")).longValue();
    }

    private long createOrder(long skuId, int quantity) throws Exception {
        String response = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"skuId\":" + skuId + ",\"quantity\":" + quantity + "}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return ((Number) JsonPath.read(response, "$.data.orderId")).longValue();
    }

    private void assertInventory(long skuId, int availableStock, int reservedStock, int version) throws Exception {
        mockMvc.perform(get("/api/skus/{skuId}/inventory", skuId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.availableStock").value(availableStock))
                .andExpect(jsonPath("$.data.reservedStock").value(reservedStock))
                .andExpect(jsonPath("$.data.version").value(version));
    }
}
