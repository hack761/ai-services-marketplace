package com.marketplace.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private String serviceTitle;
    private String buyerName;
    private String sellerName;
    private Double amount;
    private String status;
    private LocalDateTime createdAt;
    private String razorpayOrderId;
    private Boolean isPaid;
}