package com.project.SuperC.controller;

import com.project.SuperC.dto.PriceTrackingRequestDto;
import com.project.SuperC.models.PriceTrackingRequest;
import com.project.SuperC.models.User;
import com.project.SuperC.repository.UserRepository;
import com.project.SuperC.security.UserPrincipal;
import com.project.SuperC.service.PriceTrackingService;
import com.project.SuperC.service.UserProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/app")
public class PriceTrackingController {

    private final PriceTrackingService priceTrackingService;
    private final UserProductService userProductService;
    private final UserRepository userRepository;


    /**
     * Handles POST requests to add a new price tracking request.
     * This endpoint requires an authenticated user, whose details will be used
     * to link the new price tracking request.
     *
     * @param priceTrackingRequestDto The {@link PriceTrackingRequestDto} object containing the user's price tracking details (input).
     * @param userPrincipal The authenticated {@link UserPrincipal} object, injected by Spring Security via {@code @AuthenticationPrincipal}.
     * @return A {@link ResponseEntity} containing the created {@link PriceTrackingRequestDto} object
     * and an HTTP status of 201 Created.
     */
    @PostMapping("/add")
    public ResponseEntity<PriceTrackingRequestDto> createPriceTrackingEntry(
            @RequestBody PriceTrackingRequestDto priceTrackingRequestDto,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        log.info("Received request to add a new price tracking entry (DTO): {}", priceTrackingRequestDto);

        if (userPrincipal == null) {
            log.warn("Attempt to create price tracking entry without authenticated user. userPrincipal is null.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> {
                    log.error("Authenticated user with ID {} not found in database!", userPrincipal.getId());
                    return new UsernameNotFoundException("Authenticated user not found!");
                });

        log.debug("Authenticated user from principal: {}", currentUser.getEmail());


        PriceTrackingRequest newRequest = new PriceTrackingRequest();
        newRequest.setProductNumber(priceTrackingRequestDto.getProductNumber());
        newRequest.setMaxPrice(priceTrackingRequestDto.getMaxPrice());
        newRequest.setUser(currentUser);

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
    }

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

    @GetMapping("/myRequests")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<PriceTrackingRequestDto>> getMyPriceTrackingRequests(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (userPrincipal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> {
                    log.error("Authenticated user with ID {} not found in database for myRequests!", userPrincipal.getId());
                    return new UsernameNotFoundException("Authenticated user not found!");
                });

        List<PriceTrackingRequest> entities = userProductService.getUserPriceTrackingRequests(currentUser.getId());

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
}