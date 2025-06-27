package com.project.SuperC.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Data Transfer Object for Price Tracking Requests.
 * Used to send and receive data from the API without exposing internal JPA entities.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceTrackingRequestDto {

    private Long id;
    private Long productNumber;
    private Double maxPrice;
    private Long userId;
}