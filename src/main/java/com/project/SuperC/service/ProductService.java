package com.project.SuperC.service;

import com.project.SuperC.models.Product;
import com.project.SuperC.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    /**
     * Searches for products whose name contains the given search term.
     *
     * @param searchTerm The string to search for in product names.
     * @return A list of matching Product entities.
     */
    public List<Product> searchProducts(String searchTerm) {
        logger.info("Searching for products with term: {}", searchTerm);
        List<Product> products = productRepository.findByNameContainingIgnoreCase(searchTerm);
        logger.info("Found {} products for search term: {}", products.size(), searchTerm);
        return products;
    }

    public Optional<Product> getProductByProductNumber(String productNumber) {
        return productRepository.findByProductNumber(productNumber);
    }
}
