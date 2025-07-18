/**
 * REST controller for handling user dashboard related requests.
 * This class provides endpoints for retrieving data specific to an authenticated user's dashboard,
 * such as their products.
 * All endpoints are mapped under the "/api/user/dashboard" base path.
 */
package com.project.SuperC.controller;

import com.project.SuperC.security.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/dashboard")
public class UserDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(UserDashboardController.class);

    /**
     * Handles requests to retrieve products associated with the authenticated user.
     * This endpoint extracts the authenticated user's ID from the security context
     * and returns a success message.
     * @return A {@link ResponseEntity} indicating the outcome of the request.
     */
    @GetMapping("/products")
    public ResponseEntity<String> getUserProducts() {
        logger.info("Received request for user products.");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            logger.error("Authentication object is NULL in SecurityContextHolder.");
            return ResponseEntity.status(401).body("User not authenticated.");
        }

        Object principal = authentication.getPrincipal();
        logger.info("Principal from SecurityContextHolder.getAuthentication().getPrincipal(): {}", principal);

        UserDetailsImpl userDetails = null;
        if (principal instanceof UserDetailsImpl) {
            userDetails = (UserDetailsImpl) principal;
            logger.info("Successfully cast principal to UserDetailsImpl.");
            logger.info("UserDetailsImpl from SecurityContextHolder - ID: {}", userDetails.getId());
            logger.info("UserDetailsImpl from SecurityContextHolder - Username: {}", userDetails.getUsername());
            logger.info("UserDetailsImpl from SecurityContextHolder - Authorities: {}", userDetails.getAuthorities());
        } else if (principal != null) {
            logger.error("Principal is not an instance of UserDetailsImpl. Actual type: {}", principal.getClass().getName());
            return ResponseEntity.status(401).body("Authentication principal is not of expected type.");
        } else {
            logger.error("Principal from SecurityContextHolder is NULL.");
            return ResponseEntity.status(401).body("Authentication principal is null.");
        }


        if (userDetails.getId() == null) {
            logger.error("Error: userDetails.getId() is NULL for user: {}", userDetails.getUsername());
            return ResponseEntity.status(401).body("User ID not found in authentication context.");
        }

        return ResponseEntity.ok("Successfully retrieved products for user ID: " + userDetails.getId());
    }
}
