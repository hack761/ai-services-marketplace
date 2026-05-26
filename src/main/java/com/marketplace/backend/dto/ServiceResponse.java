package com.marketplace.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServiceResponse {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private String category;
    private String status;
    private String sellerName;
}