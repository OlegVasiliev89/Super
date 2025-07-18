/**
 * REST controller for managing price tracking requests.
 * This controller handles operations related to adding, viewing, and deleting
 * price tracking requests for authenticated users. It also provides an endpoint
 * for triggering daily price checks.
 * All endpoints are mapped under the "/app" base path.
 */
package com.project.SuperC.controller;

import com.project.SuperC.dto.PriceTrackingRequestDto;
import com.project.SuperC.dto.UserDashboardProductDto;
import com.project.SuperC.models.PriceTrackingRequest;
import com.project.SuperC.models.User;
import com.project.SuperC.repository.UserRepository;
import com.project.SuperC.security.UserDetailsImpl;
import com.project.SuperC.service.PriceTrackingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/app")
public class PriceTrackingController {

    private final PriceTrackingService priceTrackingService;
    private final UserRepository userRepository;

    /**
     * Handles requests to create a new price tracking entry for the authenticated user.
     * Requires ADMIN or USER role.
     *
     * @param priceTrackingRequestDto A {@link PriceTrackingRequestDto} containing the product number and maximum price.
     * @return A {@link ResponseEntity} with the created {@link PriceTrackingRequestDto} and HTTP 201 (Created) on success.
     * Returns HTTP 401 (Unauthorized) if no valid user is found in the security context.
     * Returns HTTP 400 (Bad Request) if the product number is null.
     * Returns HTTP 409 (Conflict) if a duplicate tracking request exists.
     * Returns HTTP 500 (Internal Server Error) for other unexpected issues.
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PriceTrackingRequestDto> createPriceTrackingEntry(
            @RequestBody PriceTrackingRequestDto priceTrackingRequestDto
    ) {
        log.info("Received request to add a new price tracking entry (DTO): {}", priceTrackingRequestDto);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            log.warn("createPriceTrackingEntry: No valid UserDetailsImpl found in SecurityContextHolder. Authentication: {}", authentication);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        log.info("createPriceTrackingEntry: UserDetails retrieved from SecurityContextHolder. ID: {}, Username: {}",
                userDetails.getId(), userDetails.getUsername());

        if (userDetails.getId() == null) {
            log.warn("Attempt to create price tracking entry with NULL user ID from UserDetailsImpl retrieved from SecurityContextHolder. Username: {}. Returning UNAUTHORIZED.", userDetails.getUsername());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (priceTrackingRequestDto.getProductNumber() == null) {
            log.error("PriceTrackingRequestDto received with productNumber as NULL. Frontend payload might be incorrect or deserialization failed. DTO: {}", priceTrackingRequestDto);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> {
                    log.error("Authenticated user with ID {} not found in database!", userDetails.getId());
                    return new UsernameNotFoundException("Authenticated user not found!");
                });

        log.debug("Authenticated user from principal: {}", currentUser.getEmail());

        PriceTrackingRequest newRequest = new PriceTrackingRequest();
        newRequest.setProductNumber(priceTrackingRequestDto.getProductNumber());
        newRequest.setMaxPrice(priceTrackingRequestDto.getMaxPrice());

        try {
            PriceTrackingRequest savedRequest = priceTrackingService.createPriceTrackingRequest(newRequest, currentUser);

            PriceTrackingRequestDto responseDto = new PriceTrackingRequestDto();
            responseDto.setId(savedRequest.getId());
            responseDto.setProductNumber(savedRequest.getProductNumber());
            responseDto.setMaxPrice(savedRequest.getMaxPrice());
            if (savedRequest.getUser() != null) {
                responseDto.setUserId(savedRequest.getUser().getId());
            }

            log.info("Price tracking request created successfully for user {}. Response DTO: {}", currentUser.getEmail(), responseDto);

            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            log.warn("Attempted to add duplicate price tracking request for user {}: {}", currentUser.getEmail(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception e) {
            log.error("Error creating price tracking request for user {}: {}", currentUser.getEmail(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create price tracking request: " + e.getMessage());
        }
    }

    /**
     * Triggers a daily price check for all tracked products.
     * @return A {@link ResponseEntity} containing a list of {@link PriceTrackingRequestDto}
     * representing all active price tracking requests.
     */
    @GetMapping("/dailyPriceCheck")
    public ResponseEntity<List<PriceTrackingRequestDto>> checkPricesToday() {
        log.info("Triggering daily price check for all tracked products and preparing DTO response.");

        List<PriceTrackingRequest> entities = priceTrackingService.getProductNumbersAndMaxPricesFromDB();

        List<PriceTrackingRequestDto> dtoList = entities.stream()
                .map(entity -> {
                    PriceTrackingRequestDto dto = new PriceTrackingRequestDto();
                    dto.setId(entity.getId());
                    dto.setProductNumber(entity.getProductNumber());
                    dto.setMaxPrice(entity.getMaxPrice());
                    if (entity.getUser() != null) {
                        dto.setUserId(entity.getUser().getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    /**
     * Retrieves all price tracking requests associated with the authenticated user.
     * @param userDetails The authenticated user's details, injected by Spring Security.
     * @return A {@link ResponseEntity} containing a list of {@link UserDashboardProductDto}
     * representing the user's tracked products and their details.
     * Returns HTTP 204 (No Content) if the user has no tracking requests.
     * Returns HTTP 401 (Unauthorized) if user details are null or user ID is missing.
     */
    @GetMapping("/myRequests")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<UserDashboardProductDto>> getMyPriceTrackingRequests(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("getMyPriceTrackingRequests endpoint hit.");

        if (userDetails == null) {
            log.warn("getMyPriceTrackingRequests: UserDetails is null. Returning UNAUTHORIZED.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        log.info("getMyPriceTrackingRequests: UserDetails from @AuthenticationPrincipal. ID: {}, Username: {}",
                userDetails.getId(), userDetails.getUsername());

        if (userDetails.getId() == null) {
            log.error("Authenticated user with NULL ID from principal for myRequests! Username: {}", userDetails.getUsername());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> {
                    log.error("Authenticated user with ID {} not found in database for myRequests!", userDetails.getId());
                    return new UsernameNotFoundException("Authenticated user not found!");
                });

        List<UserDashboardProductDto> dtoList = priceTrackingService.getUserDashboardProductDetails(currentUser.getId());

        log.info("getMyPriceTrackingRequests: Fetched {} price tracking requests for user {}.", dtoList.size(), currentUser.getEmail());
        log.debug("getMyPriceTrackingRequests: DTO List content: {}", dtoList);

        if (dtoList.isEmpty()) {
            log.info("getMyPriceTrackingRequests: Returning 204 No Content as the list is empty.");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(dtoList);
    }

    /**
     * Handles requests to delete a specific price tracking request.
     * @param id The ID of the price tracking request to delete.
     * @param userDetails The authenticated user's details, injected by Spring Security.
     * @return A {@link ResponseEntity} with a success message (HTTP 200 OK) on successful deletion.
     * Returns HTTP 401 (Unauthorized) if no authenticated user is found.
     * Returns HTTP 404 (Not Found) if the authenticated user is not found in the database.
     * Returns HTTP 403 (Forbidden) if the user attempts to delete a request that does not belong to them.
     * Returns HTTP 500 (Internal Server Error) for other unexpected issues during deletion.
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deletePriceTrackingRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails == null) {
            log.warn("Attempt to delete price tracking request without authenticated user. userDetails is null.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required.");
        }

        log.info("deletePriceTrackingRequest: UserDetails from @AuthenticationPrincipal. ID: {}, Username: {}",
                userDetails.getId(), userDetails.getUsername());

        try {
            User currentUser = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> {
                        log.error("Authenticated user with ID {} not found in database during delete operation!", userDetails.getId());
                        return new UsernameNotFoundException("Authenticated user not found!");
                    });

            log.info("Attempting to delete price tracking request with ID: {} for user: {}", id, currentUser.getEmail());

            priceTrackingService.deleteRequest(id, currentUser.getId());
            log.info("Successfully deleted price tracking request with ID: {}", id);
            return ResponseEntity.ok("Price tracking request with ID " + id + " deleted successfully.");

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Error deleting price tracking request {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error deleting price tracking request {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during deletion.");
        }
    }
}
