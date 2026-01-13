package org.skypro.projects.personaloffers.service;

import org.skypro.projects.personaloffers.repository.ProductExternalRepository;
import org.springframework.stereotype.Service;

@Service
public class ManagementService {

    private final ProductExternalRepository productExternalRepository;

    public ManagementService(ProductExternalRepository productExternalRepository) {
        this.productExternalRepository = productExternalRepository;
    }

    public void clearCaches() {
        productExternalRepository.clearCaches();
    }
}
