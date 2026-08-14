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
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createOrderShouldReserveStockAndSavePriceSnapshot() throws Exception {
        long skuId = createSku("TEA-BAG-100", 5, "12.50");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"skuId\":" + skuId + ",\"quantity\":3}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.skuId").value(skuId))
                .andExpect(jsonPath("$.data.quantity").value(3))
                .andExpect(jsonPath("$.data.unitPrice").value(12.50))
                .andExpect(jsonPath("$.data.totalAmount").value(37.50))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        mockMvc.perform(get("/api/skus/{skuId}/inventory", skuId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.availableStock").value(2))
                .andExpect(jsonPath("$.data.reservedStock").value(3))
                .andExpect(jsonPath("$.data.version").value(1));
    }

    @Test
    void createOrderShouldRejectInsufficientStockWithoutChangingInventory() throws Exception {
        long skuId = createSku("TEA-BAG-INSUFFICIENT", 2, "8.00");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"skuId\":" + skuId + ",\"quantity\":3}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(40901));

        mockMvc.perform(get("/api/skus/{skuId}/inventory", skuId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.availableStock").value(2))
                .andExpect(jsonPath("$.data.reservedStock").value(0))
                .andExpect(jsonPath("$.data.version").value(0));
    }

    private long createSku(String skuCode, int initialStock, String unitPrice) throws Exception {
        String response = mockMvc.perform(post("/api/skus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"skuCode\":\"" + skuCode + "\",\"name\":\"Tea bag\",\"unitPrice\":"
                                + unitPrice + ",\"initialStock\":" + initialStock + "}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return ((Number) JsonPath.read(response, "$.data.skuId")).longValue();
    }
}
