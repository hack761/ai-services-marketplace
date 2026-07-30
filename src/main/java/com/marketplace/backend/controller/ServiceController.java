package com.marketplace.backend.controller;

import com.marketplace.backend.dto.ServiceRequest;
import com.marketplace.backend.dto.ServiceResponse;
import com.marketplace.backend.security.JwtUtil;
import com.marketplace.backend.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller")
public class ServiceController {

    private final ServiceService serviceService;
    private final JwtUtil jwtUtil;

    public ServiceController(ServiceService serviceService, JwtUtil jwtUtil) {
        this.serviceService = serviceService;
        this.jwtUtil = jwtUtil;
    }

    // helper method - extract email from "Bearer <token>"
    private String getEmailFromToken(String authHeader) {
        String token = authHeader.substring(7); // removes "Bearer "
        return jwtUtil.extractEmail(token);
    }

    @PostMapping("/service")
    public ServiceResponse createService(
            @Valid @RequestBody ServiceRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String email = getEmailFromToken(authHeader);
        return serviceService.createService(request, email);
    }

    @GetMapping("/services")
    public List<ServiceResponse> getMyServices(
            @RequestHeader("Authorization") String authHeader) {
        String email = getEmailFromToken(authHeader);
        return serviceService.getMyServices(email);
    }

    @PutMapping("/service/{id}")
    public ServiceResponse updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String email = getEmailFromToken(authHeader);
        return serviceService.updateService(id, request, email);
    }

    @DeleteMapping("/service/{id}")
    public String deleteService(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        String email = getEmailFromToken(authHeader);
        return serviceService.deleteService(id, email);
    }
}