/**
 * Data Transfer Object representing a product entry on a user's dashboard.
 * This class aggregates information about a product being tracked by a user,
 * including details from the price tracking request and the associated product itself,
 * tailored for display on a user interface.
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
public class UserDashboardProductDto {

    private Long priceTrackingRequestId;

    private String productNumber;

    private String productName;

    private String productImageUrl;

    private Double maxPrice;

    private Double currentPrice;
}
