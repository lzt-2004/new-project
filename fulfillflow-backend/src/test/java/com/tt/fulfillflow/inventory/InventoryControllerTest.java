package com.tt.fulfillflow.inventory;

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
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createSkuThenReplenishShouldOnlyChangeAvailableStock() throws Exception {
        String createSkuBody = """
                {"skuCode":"COFFEE-BEAN-1KG","name":"Coffee bean 1kg","unitPrice":88.00,"initialStock":5}
                """;

        String createResponse = mockMvc.perform(post("/api/skus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSkuBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.availableStock").value(5))
                .andExpect(jsonPath("$.data.reservedStock").value(0))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long skuId = ((Number) JsonPath.read(createResponse, "$.data.skuId")).longValue();

        mockMvc.perform(post("/api/skus/{skuId}/inventory/replenishments", skuId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.availableStock").value(15))
                .andExpect(jsonPath("$.data.reservedStock").value(0))
                .andExpect(jsonPath("$.data.version").value(1));

        mockMvc.perform(get("/api/skus/{skuId}/inventory", skuId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.availableStock").value(15))
                .andExpect(jsonPath("$.data.reservedStock").value(0));
    }

    @Test
    void replenishShouldRejectZeroQuantity() throws Exception {
        mockMvc.perform(post("/api/skus/1/inventory/replenishments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(40001));
    }
}