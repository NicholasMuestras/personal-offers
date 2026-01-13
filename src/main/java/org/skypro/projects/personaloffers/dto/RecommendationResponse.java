package org.skypro.projects.personaloffers.dto;

import java.util.List;
import java.util.UUID;

public class RecommendationResponse {
    private UUID user_id;
    private List<ProductDto> recommendations;

    public RecommendationResponse() {
    }

    public RecommendationResponse(UUID user_id, List<ProductDto> recommendations) {
        this.user_id = user_id;
        this.recommendations = recommendations;
    }

    public UUID getUser_id() {
        return user_id;
    }

    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }

    public List<ProductDto> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<ProductDto> recommendations) {
        this.recommendations = recommendations;
    }
}
