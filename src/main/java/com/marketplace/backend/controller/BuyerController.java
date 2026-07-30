package com.marketplace.backend.controller;

import com.marketplace.backend.dto.ServiceResponse;
import com.marketplace.backend.service.BuyerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer")
public class BuyerController {

    private final BuyerService buyerService;

    public BuyerController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    // GET ALL SERVICES
    @GetMapping("/services")
    public List<ServiceResponse> getAllServices() {
        return buyerService.getAllServices();
    }

    // FILTER BY CATEGORY
    @GetMapping("/services/category/{category}")
    public List<ServiceResponse> getByCategory(@PathVariable String category) {
        return buyerService.getByCategory(category);
    }

    // SEARCH BY KEYWORD
    @GetMapping("/services/search")
    public List<ServiceResponse> search(@RequestParam String keyword) {
        return buyerService.searchByKeyword(keyword);
    }

    // GET SINGLE SERVICE
    @GetMapping("/services/{id}")
    public ServiceResponse getServiceById(@PathVariable Long id) {
        return buyerService.getServiceById(id);
    }
}