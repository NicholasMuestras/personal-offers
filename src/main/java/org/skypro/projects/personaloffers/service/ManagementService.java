package org.skypro.projects.personaloffers.service;

import org.skypro.projects.personaloffers.config.BuildInfoConfig;
import org.skypro.projects.personaloffers.repository.ProductExternalRepository;
import org.springframework.stereotype.Service;

@Service
public class ManagementService {

    private final ProductExternalRepository productExternalRepository;

    private final BuildInfoConfig buildInfoConfig;

    public ManagementService(ProductExternalRepository productExternalRepository, BuildInfoConfig buildInfoConfig) {
        this.productExternalRepository = productExternalRepository;
        this.buildInfoConfig = buildInfoConfig;
    }

    public void clearCaches() {
        productExternalRepository.clearCaches();
    }

    public ServiceInfo getServiceInfo() {
        return new ServiceInfo(buildInfoConfig.getServiceName(), buildInfoConfig.getServiceVersion());
    }

    public static class ServiceInfo {
        private final String name;
        private final String version;

        public ServiceInfo(String name, String version) {
            this.name = name;
            this.version = version;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }
    }
}
