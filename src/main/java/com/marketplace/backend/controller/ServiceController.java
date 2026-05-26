package com.marketplace.backend.controller;

import com.marketplace.backend.dto.ServiceRequest;
import com.marketplace.backend.dto.ServiceResponse;
import com.marketplace.backend.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping("/service")
    public ServiceResponse createService(
            @Valid @RequestBody ServiceRequest request,
            @RequestHeader("X-Seller-Email") String sellerEmail) {
        return serviceService.createService(request, sellerEmail);
    }

    @GetMapping("/services")
    public List<ServiceResponse> getMyServices(
            @RequestHeader("X-Seller-Email") String sellerEmail) {
        return serviceService.getMyServices(sellerEmail);
    }

    @PutMapping("/service/{id}")
    public ServiceResponse updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequest request,
            @RequestHeader("X-Seller-Email") String sellerEmail) {
        return serviceService.updateService(id, request, sellerEmail);
    }

    @DeleteMapping("/service/{id}")
    public String deleteService(
            @PathVariable Long id,
            @RequestHeader("X-Seller-Email") String sellerEmail) {
        return serviceService.deleteService(id, sellerEmail);
    }
}