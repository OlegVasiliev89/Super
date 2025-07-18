/**
 * Custom JWT authentication filter that extends {@link OncePerRequestFilter}.
 * This filter intercepts incoming HTTP requests to extract and validate JWT tokens
 * from the Authorization header. If a valid token is found, it authenticates the user
 * and sets the authentication in the Spring Security context. This ensures that
 * subsequent security filters and controllers can rely on the authenticated user.
 */
package com.project.SuperC.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Constructs a JwtAuthFilter with the necessary JwtService and CustomUserDetailsService.
     * Spring automatically injects these dependencies.
     *
     * @param jwtService The service for JWT token operations.
     * @param customUserDetailsService The service for loading user details from the database.
     */
    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Performs the actual filtering logic for each incoming request.
     * This method extracts the JWT token, validates it, loads user details,
     * and sets the authentication in the SecurityContextHolder if the token is valid.
     *
     * @param request The {@link HttpServletRequest} to be processed.
     * @param response The {@link HttpServletResponse} to be processed.
     * @param filterChain The {@link FilterChain} to continue the request processing.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("Processing request (JwtAuthFilter): {} {}", request.getMethod(), request.getRequestURI());

        try {
            String jwt = getJwtFromRequest(request);

            if (jwt != null) {
                String username = null;
                UserDetails userDetails = null;

                try {
                    username = jwtService.extractUsername(jwt);
                } catch (Exception e) {
                    logger.warn("JwtAuthFilter: Could not extract username from JWT token: {}", e.getMessage());
                    filterChain.doFilter(request, response);
                    return;
                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    logger.debug("JwtAuthFilter: Username extracted: {}. SecurityContextHolder is empty. Loading user details.", username);
                    userDetails = customUserDetailsService.loadUserByUsername(username);

                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        if (SecurityContextHolder.getContext().getAuthentication() != null) {
                            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                            if (principal instanceof UserDetailsImpl) {
                                UserDetailsImpl authenticatedUserDetails = (UserDetailsImpl) principal;
                                logger.info("JwtAuthFilter: SecurityContextHolder set with User ID: {}, Username: {}",
                                        authenticatedUserDetails.getId(), authenticatedUserDetails.getUsername());
                            } else {
                                logger.warn("JwtAuthFilter: Principal in SecurityContextHolder is not UserDetailsImpl. Type: {}", principal.getClass().getName());
                            }
                        } else {
                            logger.warn("JwtAuthFilter: SecurityContextHolder.getContext().getAuthentication() is NULL after setting!");
                        }

                        logger.info("JwtAuthFilter: Successfully authenticated user: {}", username);
                    } else {
                        logger.warn("JwtAuthFilter: JWT token is invalid for user: {}", username);
                    }
                } else if (username != null && SecurityContextHolder.getContext().getAuthentication() != null) {
                    logger.debug("JwtAuthFilter: User '{}' already authenticated in SecurityContextHolder. Skipping re-authentication.", username);
                }
            } else {
                logger.debug("JwtAuthFilter: No JWT token found in request for {}.", request.getRequestURI());
            }
        } catch (Exception ex) {
            logger.error("JwtAuthFilter: Could not set user authentication in security context", ex);
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            logger.info("JwtAuthFilter: Before doFilter, SecurityContextHolder contains User ID: {}, Username: {}",
                    principal.getId(), principal.getUsername());
        } else {
            logger.info("JwtAuthFilter: Before doFilter, SecurityContextHolder is NOT authenticated with UserDetailsImpl.");
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header of the HTTP request.
     *
     * @param request The {@link HttpServletRequest} from which to extract the token.
     * @return The JWT token string, or null if not found or not in the expected format.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
