package com.project.SuperC.controller;

import com.project.SuperC.models.Product;
import com.project.SuperC.service.ProductService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    /**
     * Endpoint to search for products by name.
     * @param query The search term for product names.
     * @return A ResponseEntity containing a list of matching Product objects.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam("query") String query) {
        logger.info("Received product search request for query: {}", query);
        List<Product> products = productService.searchProducts(query);

        if (products.isEmpty()) {
            logger.info("No products found for query: {}", query);
            return ResponseEntity.ok(products);
        }

        logger.info("Returning {} products for query: {}", products.size(), query);
        return ResponseEntity.ok(products);
    }
}
