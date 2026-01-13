package org.skypro.projects.personaloffers.controller;

import org.skypro.projects.personaloffers.dto.ProductDto;
import org.skypro.projects.personaloffers.dto.RecommendationResponse;
import org.skypro.projects.personaloffers.model.Product;
import org.skypro.projects.personaloffers.service.OffersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class OfferController {

    @Autowired
    private OffersService offersService;

    @GetMapping("/recommendation/{userId}")
    public ResponseEntity<RecommendationResponse> getRecommendation(@PathVariable String userId) {
        try {
            UUID userUUID = UUID.fromString(userId);
            List<Product> offers = offersService.getRecommendations(userUUID);

            List<ProductDto> offerDtos = offers.stream()
                    .map(offer -> new ProductDto(offer.getId(), offer.getName(),
                            offer.getDescription()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new RecommendationResponse(userUUID, offerDtos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
