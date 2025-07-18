/**
 * Represents the request body for resetting a user's password.
 * This class contains the password reset token received by the user
 * and the new password they wish to set.
 */
package com.project.SuperC.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    /**
     * The password reset token received by the user.
     * This token is used to verify the user's authorization to reset the password.
     * Must not be empty.
     */
    @NotBlank(message = "Token cannot be empty")
    private String token;

    /**
     * The new password that the user wishes to set.
     * Must not be empty and must be at least 6 characters long.
     */
    @NotBlank(message = "New password cannot be empty")
    @Size(min = 6, message = "New password must be at least 6 characters long")
    private String newPassword;
}
