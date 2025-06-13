package com.project.SuperC.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.*;

import lombok.Data;

/**
 * Represents a user's price tracking request for a product.
 * This class is mapped to the "superc" table in the database.
 * Uses Lombok's @Data to automatically generate getters, setters
 */
@Entity // Marks this class as a JPA entity, to manage the class as a persistent class
@Table(name = "superc")
@Data
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String email;

    private String productNumber;
    private float maxPrice;
}