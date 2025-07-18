/**
 * Repository interface for managing {@link PriceTrackingRequest} entities.
 * This interface extends {@link JpaRepository} to provide standard CRUD operations
 * and custom query methods for price tracking requests.
 */
package com.project.SuperC.repository;

import com.project.SuperC.models.PriceTrackingRequest;
import com.project.SuperC.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceTrackingRequestRepository extends JpaRepository<PriceTrackingRequest, Long> {
    /**
     * Finds a price tracking request by the associated user and product number.
     * @param user The {@link User} entity associated with the request.
     * @param productNumber The unique product number of the item being tracked.
     * @return An {@link Optional} containing the {@link PriceTrackingRequest} if found, or empty if not.
     */
    Optional<PriceTrackingRequest> findByUserAndProductNumber(User user, String productNumber);

    /**
     * Finds all price tracking requests for a given user ID.
     *
     * @param userId The ID of the user whose price tracking requests are to be retrieved.
     * @return A {@link List} of {@link PriceTrackingRequest} entities associated with the specified user ID.
     */
    List<PriceTrackingRequest> findByUserId(Long userId);
}
