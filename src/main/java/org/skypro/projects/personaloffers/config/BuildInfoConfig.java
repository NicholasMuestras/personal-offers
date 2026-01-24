package org.skypro.projects.personaloffers.config;

import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BuildInfoConfig {

    private final BuildProperties buildProperties;

    public BuildInfoConfig(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    public String getServiceName() {
        return buildProperties.getArtifact();
    }

    public String getServiceVersion() {
        return buildProperties.getVersion();
    }
}
