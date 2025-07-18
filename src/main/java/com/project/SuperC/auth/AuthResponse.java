/**
 * Represents the authentication response sent to the client after a successful
 * login or token refresh operation. This class encapsulates the access token,
 * refresh token, and an optional message to provide feedback to the client.
 */
package com.project.SuperC.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    /**
     * The access token used for authenticating subsequent requests to protected resources.
     * This token has a short expiry time.
     */
    private String accessToken;

    /**
     * The refresh token used to obtain a new access token once the current access token expires.
     * This token has a long expiry time.
     */
    private String refreshToken;

    /**
     * An optional message providing additional information or feedback about the authentication process.
     * For example, it could indicate "Login successful" or "Token refreshed."
     */
    private String message;
}
