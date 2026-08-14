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
class OrderCancellationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void cancelOrderShouldReleaseReservedStock() throws Exception {
        long skuId = createSku("CANCEL-TEA-100", 5);
        long orderId = createOrder(skuId, 3);

        mockMvc.perform(post("/api/orders/{orderId}/cancellations", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        mockMvc.perform(get("/api/skus/{skuId}/inventory", skuId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.availableStock").value(5))
                .andExpect(jsonPath("$.data.reservedStock").value(0))
                .andExpect(jsonPath("$.data.version").value(2));
    }

    @Test
    void repeatedCancellationShouldNotReleaseStockTwice() throws Exception {
        long skuId = createSku("CANCEL-REPEAT-100", 5);
        long orderId = createOrder(skuId, 3);

        mockMvc.perform(post("/api/orders/{orderId}/cancellations", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        mockMvc.perform(post("/api/orders/{orderId}/cancellations", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        mockMvc.perform(get("/api/skus/{skuId}/inventory", skuId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.availableStock").value(5))
                .andExpect(jsonPath("$.data.reservedStock").value(0))
                .andExpect(jsonPath("$.data.version").value(2));
    }

    @Test
    void cancelMissingOrderShouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/api/orders/{orderId}/cancellations", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(40401));
    }
    private long createSku(String skuCode, int initialStock) throws Exception {
        String response = mockMvc.perform(post("/api/skus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"skuCode\":\"" + skuCode
                                + "\",\"name\":\"Cancel tea\",\"unitPrice\":12.50,\"initialStock\":"
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
}
