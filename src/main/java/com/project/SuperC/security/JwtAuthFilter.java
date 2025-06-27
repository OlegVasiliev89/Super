package com.project.SuperC.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Custom Spring Security filter responsible for intercepting incoming HTTP requests,
 * extracting a JSON Web Token (JWT) from the Authorization header, validating it,
 * and then setting the authenticated user's details in Spring's SecurityContextHolder.
 */

@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String userEmail = null;

        log.info("Processing request: {} {}", request.getMethod(), request.getRequestURI());


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No Bearer token found in Authorization header. Continuing filter chain.");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        log.debug("Extracted JWT: {}", jwt);

        try {
            userEmail = jwtService.extractUsername(jwt);
            log.debug("Extracted user email from JWT: {}", userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("User email found: {}. No existing authentication in SecurityContext. Loading UserDetails...", userEmail);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                log.debug("Loaded UserDetails for {}: Username={}, Authorities={}", userEmail, userDetails.getUsername(), userDetails.getAuthorities());
                log.debug("Is token valid for {}: Checking against UserDetails...", userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Successfully authenticated user: {}", userEmail);
                } else {
                    log.warn("JWT token is invalid for user: {} or validation failed.", userEmail);
                }
            } else if (userEmail == null) {
                log.warn("Could not extract user email from JWT: {}. Token might be malformed.", jwt);
            } else {
                log.debug("User {} already authenticated in SecurityContext. Skipping JWT processing.", userEmail);
            }
        } catch (Exception e) {
            log.error("Error processing JWT token for user {}: {}", userEmail, e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}