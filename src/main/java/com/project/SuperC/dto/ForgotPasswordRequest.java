/**
 * Represents the request body for initiating a password reset process.
 * This class contains the email address of the user who has forgotten
 * their password and wishes to reset it.
 */
package com.project.SuperC.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    /**
     * The email address of the user requesting a password reset.
     * Must not be empty and must be a valid email format.
     */
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;
}
