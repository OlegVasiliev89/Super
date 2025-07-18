/**
 * Represents the request body for user login operations.
 * This class captures the credentials (email and password) provided by a user
 * attempting to authenticate.
 */
package com.project.SuperC.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    /**
     * The email address of the user attempting to log in.
     * Must not be blank and must be a valid email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * The password provided by the user for authentication.
     * Must not be blank.
     */
    @NotBlank(message = "Password is required")
    private String password;
}
