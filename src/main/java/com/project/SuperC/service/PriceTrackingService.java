package com.project.SuperC.service;

import com.project.SuperC.models.PriceTrackingRequest;
import com.project.SuperC.models.User;
import com.project.SuperC.repository.ProductRequestRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer component for handling business logic related to price tracking requests.
 */
@Service
@AllArgsConstructor
@Slf4j
public class PriceTrackingService {

    private final ProductRequestRepository productRequestRepository;
    private final DailyPriceChecker dailyPriceChecker; // Assuming this is now in service package

    /**
     * The method to add new requests into the DB, associating them with an authenticated user.
     * The user's email is implicitly known through the associated User object.
     *
     * @param priceTrackingRequest An object created with each user submission (e.g., containing productNumber and maxPrice).
     * @param user The authenticated {@link User} object to associate with this request.
     * @return Returns the saved {@link PriceTrackingRequest} object as confirmation of its addition to the DB.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public PriceTrackingRequest createPriceTrackingRequest(PriceTrackingRequest priceTrackingRequest, User user){
        priceTrackingRequest.setUser(user);
        log.info("Saving new price tracking request for user {}: {}", user.getEmail(), priceTrackingRequest);
        return productRequestRepository.save(priceTrackingRequest);
    }

    /**
     * Find all the requests from the DB, makes it into a list so the list can be sent into
     * the {@link DailyPriceChecker} further to process into sending notifications on price alerts via email
     * @return returns a list of {@link PriceTrackingRequest} entities. The mapping to DTOs will happen in the controller.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<PriceTrackingRequest> getProductNumbersAndMaxPricesFromDB(){
        List<PriceTrackingRequest> returnedList = productRequestRepository.findAll();
        log.info("Fetched requests from DB (entities): {}", returnedList.size());
        dailyPriceChecker.fetchPrices(returnedList);
        return returnedList;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<PriceTrackingRequest> getUserPriceTrackingRequests(User user) {
        log.info("Fetching price tracking requests for user: {}", user.getEmail());
        return productRequestRepository.findByUser(user);
    }
}