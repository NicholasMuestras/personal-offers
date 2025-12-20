package org.skypro.projects.personaloffers.controller;

import org.skypro.projects.personaloffers.dto.OfferDto;
import org.skypro.projects.personaloffers.dto.RecommendationResponse;
import org.skypro.projects.personaloffers.model.Offer;
import org.skypro.projects.personaloffers.service.OfferService;
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
    private OfferService offerService;

    @GetMapping("/recommendation/{userId}")
    public ResponseEntity<RecommendationResponse> getRecommendation(@PathVariable String userId) {
        try {
            UUID userUUID = UUID.fromString(userId);
            List<Offer> offers = offerService.getRecommendations(userUUID);

            List<OfferDto> offerDtos = offers.stream()
                    .map(offer -> new OfferDto(offer.getId(), offer.getName(),
                            offer.getDescription()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new RecommendationResponse(userUUID, offerDtos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
