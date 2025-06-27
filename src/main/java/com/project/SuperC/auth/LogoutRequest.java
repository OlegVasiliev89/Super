package com.project.SuperC.auth;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class LogoutRequest {

    @NotBlank(message = "Refresh token for logout is required")
    private String refreshToken;
}