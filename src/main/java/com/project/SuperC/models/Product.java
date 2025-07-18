/**
 * Represents a product entity in the database.
 * This entity stores  details about a product, including its
 * unique identifiers, name, image, pricing information, and other attributes.
 */
package com.project.SuperC.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import java.time.OffsetDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {


    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "product_number", nullable = false, unique = true)
    @JsonProperty("product_number")
    private String productNumber;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image_url", nullable = false)
    @JsonProperty("image_url")
    private String imageUrl;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp with time zone default now()")
    private OffsetDateTime createdAt;

    @Column(name = "html_snippet", nullable = false, columnDefinition = "TEXT")
    @JsonProperty("html_snippet")
    private String htmlSnippet;

    @Column(name = "unit")
    private String unit;

    @Column(name = "current_price")
    @JsonProperty("current_price")
    private Double currentPrice;

    @Column(name = "average_price")
    @JsonProperty("average_price")
    private Double averagePrice;

    @Column(name = "price_per_unit")
    @JsonProperty("price_per_unit")
    private String pricePerUnit;

    @Column(name = "validity_date")
    @JsonProperty("validity_date")
    private String validityDate;
}
