package com.project.SuperC.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.*;

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
@Table(name = "superc")
@Data
public class Request {
    /**
     * Unique Identifier for the price tracking request
     * An auto-generated primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     * Recording the email of the user making the request
     * for future price update notifications
     */
    @Column(unique = true, nullable = false)
    private String email;
    /**
     * Recording the product number for future search
     * and price comparisons
     */
    private String productNumber;
    /**
     * The price threshold that will trigger a price notification to be sent
     */
    private float maxPrice;
}