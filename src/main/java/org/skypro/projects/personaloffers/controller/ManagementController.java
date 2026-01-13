package org.skypro.projects.personaloffers.controller;

import org.skypro.projects.personaloffers.service.ManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/management")
public class ManagementController {

    private final ManagementService managementService;

    public ManagementController(ManagementService managementService) {
        this.managementService = managementService;
    }

    @PostMapping("/clear-caches")
    @ResponseStatus(HttpStatus.OK)
    public void clearCaches() {
        managementService.clearCaches();
    }

    @GetMapping("/info")
    public ManagementService.ServiceInfo getInfo() {
        return managementService.getServiceInfo();
    }
}
