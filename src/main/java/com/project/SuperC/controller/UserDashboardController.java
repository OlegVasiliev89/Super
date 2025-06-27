package com.project.SuperC.controller;

import com.project.SuperC.models.PriceTrackingRequest;
import com.project.SuperC.models.User;
import com.project.SuperC.repository.UserRepository;
import com.project.SuperC.security.UserPrincipal;
import com.project.SuperC.service.UserProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/dashboard")
@AllArgsConstructor
@Slf4j
public class UserDashboardController {

    private final UserProductService userProductService;
    private final UserRepository userRepository;

    /**
     * Retrieves all price tracking requests for the currently authenticated user.
     * Requires the user to be authenticated.
     *
     * @param userPrincipal The authenticated UserPrincipal object, injected by Spring Security via @AuthenticationPrincipal.
     * @return A ResponseEntity containing a list of PriceTrackingRequest objects.
     */
    @GetMapping("/products")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PriceTrackingRequest>> getUserProducts(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (userPrincipal == null) {
            log.warn("Attempt to access user dashboard without authenticated user. userPrincipal is null.");
            return ResponseEntity.status(401).build();
        }

        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> {
                    log.error("Authenticated user with ID {} not found in database for dashboard!", userPrincipal.getId());
                    return new UsernameNotFoundException("Authenticated user not found!");
                });

        log.info("Fetching products for user: {}", currentUser.getEmail());
        List<PriceTrackingRequest> userProducts = userProductService.getUserPriceTrackingRequests(currentUser.getId());

        if (userProducts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userProducts);
    }

}