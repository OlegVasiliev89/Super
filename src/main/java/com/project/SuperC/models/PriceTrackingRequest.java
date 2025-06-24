package com.project.SuperC.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.*;
//TODO: Find a proper name for Request
import lombok.Data; // Import lombok annotation to create getters and setters for the class below

/**
 * Represents a user's price tracking request for a product.
 * This class is mapped to the "superc" table in the database.
 * Uses Lombok's @Data to automatically generate getters, setters
 * @author Oleg Vasiliev
 * @version 1.0
 * @since 2025-06-07
 */
@Entity // Marks this class as a JPA entity, to manage the class as a persistent class
@Table(name = "superc") //TODO: Rename the table in Supabase to PriceTrackingRequests
@Data
public class PriceTrackingRequest {
    /**
     * Unique Identifier for the price tracking request
     * An auto-generated primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     * Email of the user making the request. Used for price update notifications
     */
    @Column(unique = true, nullable = false)
    private String email;
    /**
     * Unique identifier of a product
     */
    private Long productNumber; //TODO: Find more tables to implement, like products and sales or personal savings
    /**
     * The price threshold that will trigger a price notification to be sent
     */
    private float maxPrice;
}