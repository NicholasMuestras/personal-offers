package org.skypro.projects.personaloffers.dto;

import java.util.List;
import java.util.UUID;

public class RecommendationResponse {
    private UUID user_id;
    private List<OfferDto> recommendations;

    public RecommendationResponse() {
    }

    public RecommendationResponse(UUID user_id, List<OfferDto> recommendations) {
        this.user_id = user_id;
        this.recommendations = recommendations;
    }

    public UUID getUser_id() {
        return user_id;
    }

    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }

    public List<OfferDto> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<OfferDto> recommendations) {
        this.recommendations = recommendations;
    }
}
