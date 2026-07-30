package com.marketplace.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private AiService service;

    private String status; // PENDING, COMPLETED, CANCELLED

    private Double amount;

    private LocalDateTime createdAt;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private Boolean isPaid = false;
}