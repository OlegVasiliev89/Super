/**
 * Represents a price tracking request entity in the database.
 * This entity stores information about a user's request to track the price of a specific product,
 * including the maximum desired price and a link to the user and product entities.
 */
package com.project.SuperC.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "price_tracking_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceTrackingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "product_number", nullable = true)
    private String productNumber;

    @Column(nullable = false)
    private double maxPrice;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_number", referencedColumnName = "product_number", insertable = false, updatable = false)
    private Product product;
}
