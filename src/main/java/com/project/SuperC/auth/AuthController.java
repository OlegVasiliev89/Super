package com.project.SuperC.auth;

import com.project.SuperC.dto.ForgotPasswordRequest;
import com.project.SuperC.dto.ResetPasswordRequest;
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

/**
 * REST controller for handling authentication-related requests.
 * This includes user registration, login, and password management (forgot/reset).
 * All endpoints are mapped under the "/api/auth" base path.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    /**
     * Constructs an AuthController with the necessary AuthService.
     * Spring automatically injects the AuthService dependency.
     *
     * @param authService The service responsible for authentication and user management logic.
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles user registration requests.
     * Registers a new user with the provided details.
     *
     * @param registerRequest A {@link RegisterRequest} object containing user registration details (e.g., email, password).
     * The request body is validated using {@code @Valid}.
     * @return A {@link ResponseEntity} indicating the outcome of the registration.
     * Returns HTTP 201 (Created) on successful registration.
     * Returns HTTP 400 (Bad Request) if registration fails due to client-side errors (e.g., user already exists).
     * Logs errors internally for debugging.
     */
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

    /**
     * Handles user login and authentication requests.
     * Authenticates a user with the provided credentials and returns authentication tokens if successful.
     *
     * @param loginRequest A {@link LoginRequest} object containing user login credentials (e.g., email, password).
     * The request body is validated using {@code @Valid}.
     * @return A {@link ResponseEntity} containing an {@link AuthResponse} on success (HTTP 200 OK).
     * Returns HTTP 401 (Unauthorized) for bad credentials.
     * Returns HTTP 500 (Internal Server Error) for unexpected server-side issues.
     * Logs authentication attempts and outcomes.
     */
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

    /**
     * Handles requests for initiating a password reset.
     * Sends a password reset link to the user's email if the account exists.
     * This endpoint is designed to provide a generic success message to prevent email enumeration attacks.
     *
     * @param forgotPasswordRequest A {@link ForgotPasswordRequest} object containing the user's email address.
     * The request body is validated using {@code @Valid}.
     * @return A {@link ResponseEntity} with a generic success message (HTTP 200 OK)
     * Logs any errors during the process.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            authService.forgotPassword(forgotPasswordRequest);
            return ResponseEntity.ok("If an account with that email exists, a password reset link has been sent.");
        } catch (Exception e) {
            logger.error("Error initiating password reset for email {}: {}", forgotPasswordRequest.getEmail(), e.getMessage(), e);
            return ResponseEntity.ok("If an account with that email exists, a password reset link has been sent.");
        }
    }

    /**
     * Handles requests for resetting a user's password using a valid reset token.
     *
     * @param resetPasswordRequest A {@link ResetPasswordRequest} object containing the reset token and the new password.
     * The request body is validated using {@code @Valid}.
     * @return A {@link ResponseEntity} indicating the outcome of the password reset.
     * Returns HTTP 200 (OK) on successful password reset.
     * Returns HTTP 400 (Bad Request) if the reset fails (e.g., invalid/expired token, validation errors).
     * Returns HTTP 500 (Internal Server Error) for unexpected server-side issues.
     * Logs errors internally.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            authService.resetPassword(resetPasswordRequest);
            return ResponseEntity.ok("Password has been reset successfully!");
        } catch (RuntimeException e) {
            logger.error("Password reset failed for token {}: {}", resetPasswordRequest.getToken(), e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("An unexpected error occurred during password reset for token {}: {}", resetPasswordRequest.getToken(), e.getMessage(), e);
            return new ResponseEntity<>("An unexpected error occurred during password reset.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
