package com.project.SuperC.auth;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) { // Update constructor
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            authService.register(registerRequest);
            return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
        } catch (RuntimeException e) {
            logger.error("Registration failed for user {}: {}", registerRequest.getEmail(), e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.login(loginRequest);
            logger.info("Login successful for user: {}", loginRequest.getEmail());
            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            logger.error("Authentication failed for user {}: Bad credentials", loginRequest.getEmail(), e);
            return new ResponseEntity<>(new AuthResponse(null, null, "Bad credentials"), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("An unexpected error occurred during authentication for user {}: {}", loginRequest.getEmail(), e.getMessage(), e);
            return new ResponseEntity<>(new AuthResponse(null, null, "An unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}