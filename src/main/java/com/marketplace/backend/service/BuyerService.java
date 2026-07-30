package com.marketplace.backend.service;

import com.marketplace.backend.dto.ServiceResponse;
import com.marketplace.backend.entity.AiService;
import com.marketplace.backend.repository.ServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuyerService {

    private final ServiceRepository serviceRepository;

    public BuyerService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    // GET ALL ACTIVE SERVICES
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findByStatus("ACTIVE")
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // FILTER BY CATEGORY
    public List<ServiceResponse> getByCategory(String category) {
        return serviceRepository.findByStatusAndCategory("ACTIVE", category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // SEARCH BY KEYWORD
    public List<ServiceResponse> searchByKeyword(String keyword) {
        return serviceRepository.findByStatusAndTitleContainingIgnoreCase("ACTIVE", keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // GET SINGLE SERVICE DETAIL
    public ServiceResponse getServiceById(Long id) {
        AiService service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        return mapToResponse(service);
    }

    private ServiceResponse mapToResponse(AiService service) {
        return new ServiceResponse(
                service.getId(),
                service.getTitle(),
                service.getDescription(),
                service.getPrice(),
                service.getCategory(),
                service.getStatus(),
                service.getSeller().getUsername()
        );
    }
}