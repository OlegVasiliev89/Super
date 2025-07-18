/**
 * Repository interface for managing {@link Product} entities.
 * This interface extends {@link JpaRepository} to provide standard CRUD operations
 * and custom query methods for product data.
 */
package com.project.SuperC.repository;

import com.project.SuperC.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    /**
     * Finds a product by its unique product number.
     *
     * @param productNumber The unique product number of the product.
     * @return An {@link Optional} containing the {@link Product} if found, or empty if not.
     */
    Optional<Product> findByProductNumber(String productNumber);

    /**
     * Finds a list of products whose names contain the given string, ignoring case.
     * @param name The string to search for within product names.
     * @return A {@link List} of {@link Product} entities whose names contain the specified string.
     */
    List<Product> findByNameContainingIgnoreCase(String name);
}
