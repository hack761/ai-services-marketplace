package com.marketplace.backend.service;

import com.marketplace.backend.dto.ServiceRequest;
import com.marketplace.backend.dto.ServiceResponse;
import com.marketplace.backend.entity.AiService;
import com.marketplace.backend.entity.User;
import com.marketplace.backend.repository.ServiceRepository;
import com.marketplace.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public ServiceService(ServiceRepository serviceRepository,
                          UserRepository userRepository) {
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }

    // CREATE SERVICE
    public ServiceResponse createService(ServiceRequest request, String sellerEmail) {

        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Seller not found"));

        AiService service = new AiService();
        service.setTitle(request.getTitle());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setCategory(request.getCategory());
        service.setStatus("ACTIVE");
        service.setSeller(seller);

        serviceRepository.save(service);

        return mapToResponse(service);
    }

    // GET ALL SERVICES BY SELLER
    public List<ServiceResponse> getMyServices(String sellerEmail) {

        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Seller not found"));

        return serviceRepository.findBySeller(seller)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // UPDATE SERVICE
    public ServiceResponse updateService(Long id, ServiceRequest request, String sellerEmail) {

        AiService service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // make sure this seller owns this service
        if (!service.getSeller().getEmail().equals(sellerEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        service.setTitle(request.getTitle());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setCategory(request.getCategory());

        serviceRepository.save(service);

        return mapToResponse(service);
    }

    // DELETE SERVICE
    public String deleteService(Long id, String sellerEmail) {

        AiService service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        if (!service.getSeller().getEmail().equals(sellerEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        serviceRepository.delete(service);

        return "Service deleted successfully";
    }

    // HELPER - convert entity to response
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