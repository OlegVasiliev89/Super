/**
 * Service class for managing price tracking requests.
 * This service handles the business logic related to creating, retrieving,
 * and deleting price tracking requests, as well as preparing data for the user dashboard.
 */
package com.project.SuperC.service;

import com.project.SuperC.dto.PriceTrackingRequestDto;
import com.project.SuperC.dto.UserDashboardProductDto;
import com.project.SuperC.models.PriceTrackingRequest;
import com.project.SuperC.models.User;
import com.project.SuperC.repository.PriceTrackingRequestRepository;
import com.project.SuperC.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class PriceTrackingService {

    private final PriceTrackingRequestRepository priceTrackingRequestRepository;
    private final ProductRepository productRepository;

    /**
     * Creates a new price tracking request for a given user and product.
     * Before saving, it checks if the user is already tracking the same product
     * to prevent duplicate entries.
     *
     * @param newRequest The {@link PriceTrackingRequest} object containing the details of the request.
     * @param user The {@link User} for whom the price tracking request is being created.
     * @return The saved {@link PriceTrackingRequest} entity.
     * @throws IllegalStateException if the user is already tracking the specified product.
     */
    @Transactional
    public PriceTrackingRequest createPriceTrackingRequest(PriceTrackingRequest newRequest, User user) {
        Optional<PriceTrackingRequest> existingRequest = priceTrackingRequestRepository
                .findByUserAndProductNumber(user, newRequest.getProductNumber());

        if (existingRequest.isPresent()) {
            throw new IllegalStateException("You are already tracking this product.");
        }

        newRequest.setUser(user);
        return priceTrackingRequestRepository.save(newRequest);
    }

    /**
     * Retrieves all price tracking requests from the database.
     * This method can be used for operations that require a list of all tracked items,
     * such as a daily price check.
     *
     * @return A {@link List} of all {@link PriceTrackingRequest} entities.
     */
    public List<PriceTrackingRequest> getProductNumbersAndMaxPricesFromDB() {
        return priceTrackingRequestRepository.findAll();
    }

    /**
     * Deletes a price tracking request by its ID, ensuring that only the owner of the request
     * can delete it.
     *
     * @param requestId The ID of the price tracking request to delete.
     * @param userId The ID of the user attempting to delete the request.
     * @throws IllegalArgumentException if the request is not found or the user is not authorized.
     */
    @Transactional
    public void deleteRequest(Long requestId, Long userId) {
        PriceTrackingRequest request = priceTrackingRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Price tracking request not found with ID: " + requestId));

        if (!request.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this price tracking request.");
        }

        priceTrackingRequestRepository.delete(request);
    }

    /**
     * Retrieves a list of product details for a user's dashboard, based on their price tracking requests.
     * This method fetches the user's tracking requests and then enriches them with product details
     * from the {@link ProductRepository}.
     *
     * @param userId The ID of the user for whom to retrieve dashboard product details.
     * @return A {@link List} of {@link UserDashboardProductDto} containing aggregated product information.
     */
    public List<UserDashboardProductDto> getUserDashboardProductDetails(Long userId) {
        List<PriceTrackingRequest> trackingRequests = priceTrackingRequestRepository.findByUserId(userId);

        return trackingRequests.stream()
                .map(request -> {
                    UserDashboardProductDto dto = new UserDashboardProductDto();
                    dto.setPriceTrackingRequestId(request.getId());
                    dto.setProductNumber(request.getProductNumber());
                    dto.setMaxPrice(request.getMaxPrice());

                    productRepository.findByProductNumber(request.getProductNumber()).ifPresent(product -> {
                        dto.setProductName(product.getName());
                        dto.setProductImageUrl(product.getImageUrl());
                        dto.setCurrentPrice(product.getCurrentPrice()); // Added current price
                    });
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
