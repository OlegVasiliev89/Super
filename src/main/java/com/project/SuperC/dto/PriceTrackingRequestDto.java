/**
 * Data Transfer Object for price tracking requests.
 * This class is used to transfer data between the client and the server
 * for operations related to price tracking, such as creating a new request
 * or displaying existing requests.
 */
package com.project.SuperC.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceTrackingRequestDto {
    /**
     * The unique identifier for the price tracking request.
     * This field is populated when retrieving existing requests.
     */
    private Long id;

    /**
     * The unique product identifier for the item being tracked.
     */
    private String productNumber;

    /**
     * The maximum price threshold for the product. An alert or notification
     * might be triggered if the product's price drops below or reaches this value.
     */
    private double maxPrice;

    /**
     * The unique identifier of the user who owns this price tracking request.
     */
    private Long userId;
}
