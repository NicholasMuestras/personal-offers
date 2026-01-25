package org.skypro.projects.personaloffers.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.skypro.projects.personaloffers.dto.ProductDto;
import org.skypro.projects.personaloffers.dto.RecommendationResponse;
import org.skypro.projects.personaloffers.model.Product;
import org.skypro.projects.personaloffers.service.OffersService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(OfferController.class)
public class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OffersService offersService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getRecommendation_ValidUserId_ReturnsOkWithRecommendations() throws Exception {
        // Arrange
        String userId = "f37ba8a8-3cd5-4976-9f74-2b21f105da67";
        UUID userUUID = UUID.fromString(userId);
        
        Product product = new Product();
        product.setId(UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"));
        product.setName("Top Saving");
        product.setDescription("Откройте свою собственную «Копилку»");
        
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setText(product.getDescription());
        
        RecommendationResponse response = new RecommendationResponse();
        response.setUser_id(userUUID);
        response.setRecommendations(List.of(productDto));
        
        when(offersService.getRecommendations(userUUID)).thenReturn(List.of(product));
        
        // Act & Assert
        mockMvc.perform(get("/recommendation/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.user_id").value(userId))
                .andExpect(jsonPath("$.recommendations[0].id").value("59efc529-2fff-41af-baff-90ccd7402925"))
                .andExpect(jsonPath("$.recommendations[0].name").value("Top Saving"))
                .andExpect(jsonPath("$.recommendations[0].text").value("Откройте свою собственную «Копилку»"));
    }

    @Test
    public void getRecommendation_InvalidUserId_ReturnsBadRequest() throws Exception {
        String invalidUserId = "invalid-uuid";
        
        mockMvc.perform(get("/recommendation/{userId}", invalidUserId))
                .andExpect(status().isBadRequest());
    }
}
