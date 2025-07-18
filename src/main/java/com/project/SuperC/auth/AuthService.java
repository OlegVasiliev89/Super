package com.project.SuperC.auth;

import com.project.SuperC.dto.ForgotPasswordRequest;
import com.project.SuperC.dto.ResetPasswordRequest;
import com.project.SuperC.models.User;
import com.project.SuperC.models.Role;
import com.project.SuperC.models.RoleName;
import com.project.SuperC.models.PasswordResetToken;
import com.project.SuperC.repository.RoleRepository;
import com.project.SuperC.repository.UserRepository;
import com.project.SuperC.repository.PasswordResetTokenRepository;
import com.project.SuperC.security.JwtService;
import com.project.SuperC.security.UserDetailsImpl;
import com.project.SuperC.service.EmailSenderService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailSenderService emailSenderService;

    /**
     * Handles user registration.
     *
     * @param request The RegisterRequest DTO containing user registration details.
     * @return The newly registered User entity.
     * @throws RuntimeException if the email is already taken or the default role is not found.
     */
    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already taken!");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role 'ROLE_USER' not found. Please ensure it exists in your database and is populated during application startup or migration."));
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    /**
     * Authenticates a user and generates JWT tokens.
     *
     * @param request The LoginRequest DTO containing user login credentials.
     * @return An AuthResponse DTO containing the access token, and potentially a refresh token and message.
     */
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(userDetails);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(accessToken);
        authResponse.setMessage("Login successful!");

        return authResponse;
    }

    /**
     * Initiates the password reset process by generating a token and sending an email.
     * @param request ForgotPasswordRequest containing the user's email.
     */
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user != null) {
            Optional<PasswordResetToken> existingToken = passwordResetTokenRepository.findByUser(user);

            String token = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);

            PasswordResetToken resetToken;
            if (existingToken.isPresent()) {
                resetToken = existingToken.get();
                resetToken.setToken(token);
                resetToken.setExpiryDate(expiryDate);
            } else {
                resetToken = PasswordResetToken.builder()
                        .token(token)
                        .user(user)
                        .expiryDate(expiryDate)
                        .build();
            }

            passwordResetTokenRepository.save(resetToken);

            String resetLink = "http://localhost:8080/reset-password?token=" + token;
            String subject = "Password Reset Request for SuperC Price Tracker";
            String body = "Dear " + user.getEmail() + ",\n\n"
                    + "You have requested to reset your password for your SuperC Price Tracker account.\n"
                    + "Please click on the following link to reset your password:\n"
                    + resetLink + "\n\n"
                    + "This link will expire in 1 hour. If you did not request a password reset, please ignore this email.\n\n"
                    + "Thank you,\n"
                    + "SuperC Price Tracker Team";

            emailSenderService.sendNotificationsEmail(user.getEmail(), subject, body);
        }
    }

    /**
     * Resets the user's password using a valid token.
     * @param request ResetPasswordRequest containing the token and new password.
     * @throws RuntimeException if the token is invalid, expired, or the user is not found.
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired password reset token."));

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new RuntimeException("Password reset token has expired. Please request a new one.");
        }

        User user = resetToken.getUser();
        if (user == null) {
            throw new RuntimeException("User associated with this token not found.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }
}
