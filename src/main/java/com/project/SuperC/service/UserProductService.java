package com.project.SuperC.service;

import com.project.SuperC.models.PriceTrackingRequest;
import com.project.SuperC.repository.PriceTrackingRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserProductService {

    private final PriceTrackingRequestRepository priceTrackingRequestRepository;

    /**
     * Retrieves all price tracking requests for a specific user.
     * This method ensures that a user can only see their own requests.
     *
     * @param userId The ID of the user whose price tracking requests are to be retrieved.
     * @return A list of PriceTrackingRequest objects associated with the user.
     */
    @Transactional(readOnly = true)
    public List<PriceTrackingRequest> getUserPriceTrackingRequests(Long userId) {
        return priceTrackingRequestRepository.findByUser_Id(userId);
    }

}