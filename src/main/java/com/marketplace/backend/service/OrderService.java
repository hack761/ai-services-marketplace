package com.marketplace.backend.service;

import com.marketplace.backend.dto.OrderResponse;
import com.marketplace.backend.dto.PaymentVerifyRequest;
import com.marketplace.backend.entity.AiService;
import com.marketplace.backend.entity.Order;
import com.marketplace.backend.entity.User;
import com.marketplace.backend.repository.OrderRepository;
import com.marketplace.backend.repository.ServiceRepository;
import com.marketplace.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final RazorpayService razorpayService;

    public OrderService(OrderRepository orderRepository,
                        ServiceRepository serviceRepository,
                        UserRepository userRepository,
                        RazorpayService razorpayService) {
        this.orderRepository = orderRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.razorpayService = razorpayService;
    }

    // BUYER PLACES AN ORDER
    public OrderResponse placeOrder(Long serviceId, String buyerEmail) {

        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Buyer not found"));

        AiService service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // buyer cant buy their own service
        if (service.getSeller().getEmail().equals(buyerEmail)) {
            throw new RuntimeException("You cannot buy your own service");
        }

        // service must be active
        if (!service.getStatus().equals("ACTIVE")) {
            throw new RuntimeException("Service is not available");
        }

        Order order = new Order();
        order.setBuyer(buyer);
        order.setService(service);
        order.setAmount(service.getPrice());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setIsPaid(false);

        // create razorpay order
        try {
            String razorpayOrderId = razorpayService.createRazorpayOrder(service.getPrice());
            order.setRazorpayOrderId(razorpayOrderId);
        } catch (Exception e) {
            throw new RuntimeException("Payment gateway error: " + e.getMessage());
        }

        orderRepository.save(order);

        return mapToResponse(order);
    }

    // BUYER - GET MY ORDERS
    public List<OrderResponse> getMyOrdersAsBuyer(String buyerEmail) {

        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Buyer not found"));

        return orderRepository.findByBuyer(buyer)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // SELLER - GET ORDERS RECEIVED
    public List<OrderResponse> getMyOrdersAsSeller(String sellerEmail) {

        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Seller not found"));

        return orderRepository.findByServiceSeller(seller)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // SELLER - UPDATE ORDER STATUS
    public OrderResponse updateOrderStatus(Long orderId, String status, String sellerEmail) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // make sure this seller owns this order
        if (!order.getService().getSeller().getEmail().equals(sellerEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        order.setStatus(status.toUpperCase());
        orderRepository.save(order);

        return mapToResponse(order);
    }

    // BUYER - VERIFY PAYMENT
    public OrderResponse verifyPayment(PaymentVerifyRequest request) {

        Order order = orderRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        boolean isValid = razorpayService.verifyPayment(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        if (!isValid) {
            throw new RuntimeException("Payment verification failed");
        }

        order.setRazorpayPaymentId(request.getRazorpayPaymentId());
        order.setIsPaid(true);
        order.setStatus("COMPLETED");

        orderRepository.save(order);

        return mapToResponse(order);
    }

    // HELPER
    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getService().getTitle(),
                order.getBuyer().getUsername(),
                order.getService().getSeller().getUsername(),
                order.getAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getRazorpayOrderId(),
                order.getIsPaid()
        );
    }
}