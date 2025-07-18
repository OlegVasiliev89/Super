/**
 * Custom authentication entry point for JWT-based authentication.
 * This class implements Spring Security's {@link AuthenticationEntryPoint} interface
 * and is responsible for handling unauthorized access attempts.
 * When an unauthenticated user tries to access a secured resource, this entry point
 * will be invoked to send an appropriate error response.
 */
package com.project.SuperC.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    /**
     * Commences an authentication scheme.
     * This method is called when an unauthenticated user attempts to access a secured resource.
     * It logs the unauthorized error and sends an HTTP 401 Unauthorized response to the client.
     *
     * @param request The {@link HttpServletRequest} that resulted in an {@link AuthenticationException}.
     * @param response The {@link HttpServletResponse} to send the error response.
     * @param authException The {@link AuthenticationException} that caused the authentication failure.
     * @throws IOException if an input or output error occurs.
     * @throws ServletException if a servlet-specific error occurs.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("Unauthorized error: {}", authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized - " + authException.getMessage());
    }
}
