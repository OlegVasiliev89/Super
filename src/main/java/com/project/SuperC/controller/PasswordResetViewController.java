package com.project.SuperC.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PasswordResetViewController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetViewController.class);

    /**
     * Handles GET requests to /reset-password and redirects to /reset-password.html,
     * preserving any query parameters (like the token).
     * @param token The password reset token from the URL query parameter.
     * @return A RedirectView that redirects to the static HTML page with the token.
     */
    @GetMapping("/reset-password")
    public RedirectView handlePasswordResetView(@RequestParam(required = false) String token) {
        logger.info("Received GET request for /reset-password. Token: {}", token != null ? "[PRESENT]" : "[MISSING]");
        String redirectUrl = "/reset-password.html";
        if (token != null && !token.isEmpty()) {
            redirectUrl += "?token=" + token;
        }
        logger.info("Redirecting to: {}", redirectUrl);
        return new RedirectView(redirectUrl);
    }
}
