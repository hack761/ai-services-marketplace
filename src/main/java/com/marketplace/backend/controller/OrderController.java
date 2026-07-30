package com.marketplace.backend.controller;

import com.marketplace.backend.dto.OrderResponse;
import com.marketplace.backend.dto.PaymentVerifyRequest;
import com.marketplace.backend.security.JwtUtil;
import com.marketplace.backend.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    public OrderController(OrderService orderService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }

    private String getEmailFromToken(String authHeader) {
        String token = authHeader.substring(7);
        return jwtUtil.extractEmail(token);
    }

    // BUYER PLACES ORDER
    @PostMapping("/buyer/order/{serviceId}")
    public OrderResponse placeOrder(
            @PathVariable Long serviceId,
            @RequestHeader("Authorization") String authHeader) {
        String email = getEmailFromToken(authHeader);
        return orderService.placeOrder(serviceId, email);
    }

    // BUYER VIEWS THEIR ORDERS
    @GetMapping("/buyer/orders")
    public List<OrderResponse> getBuyerOrders(
            @RequestHeader("Authorization") String authHeader) {
        String email = getEmailFromToken(authHeader);
        return orderService.getMyOrdersAsBuyer(email);
    }

    // SELLER VIEWS ORDERS RECEIVED
    @GetMapping("/seller/orders")
    public List<OrderResponse> getSellerOrders(
            @RequestHeader("Authorization") String authHeader) {
        String email = getEmailFromToken(authHeader);
        return orderService.getMyOrdersAsSeller(email);
    }

    // SELLER UPDATES ORDER STATUS
    @PutMapping("/seller/order/{orderId}/status")
    public OrderResponse updateStatus(
            @PathVariable Long orderId,
            @RequestParam String status,
            @RequestHeader("Authorization") String authHeader) {
        String email = getEmailFromToken(authHeader);
        return orderService.updateOrderStatus(orderId, status, email);
    }

    @PostMapping("/buyer/payment/verify")
    public OrderResponse verifyPayment(@RequestBody PaymentVerifyRequest request) {
        return orderService.verifyPayment(request);
    }
}