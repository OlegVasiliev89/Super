package com.project.SuperC.auth;

import com.project.SuperC.exception.UserAlreadyExistsException;
import com.project.SuperC.exception.UserNotFoundException;
import com.project.SuperC.models.Role;
import com.project.SuperC.models.RoleName;
import com.project.SuperC.models.User;
import com.project.SuperC.repository.RoleRepository;
import com.project.SuperC.repository.UserRepository;
import com.project.SuperC.security.JwtTokenProvider;
import com.project.SuperC.token.RefreshToken;
import com.project.SuperC.token.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final RoleRepository roleRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RefreshTokenService refreshTokenService,
                       RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.roleRepository = roleRepository;
    }

    /**
     * Handles user login by authenticating credentials, generating an access token,
     * and creating and saving a refresh token.
     *
     * @param loginRequest The DTO containing user email and password.
     * @return An AuthResponse containing the access token, refresh token, and a message.
     * @throws UserNotFoundException if the user is not found after successful authentication.
     */
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found after authentication: " + loginRequest.getEmail()));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(accessToken, refreshToken.getToken(), "Login successful");
    }

    /**
     * Handles user registration by checking for existing users, encoding the password,
     * assigning a default role, and saving the new user.
     *
     * @param registerRequest The DTO containing user registration details (email, password).
     * @return The newly registered User entity.
     * @throws UserAlreadyExistsException if a user with the given email already exists.
     * @throws RuntimeException if the default 'ROLE_USER' role is not found in the database.
     */
    @Transactional
    public User register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + registerRequest.getEmail() + " already exists.");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER role not found in database! Please ensure 'ROLE_USER' role is created at application startup."));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    /**
     * Handles user logout by finding and deleting the provided refresh token.
     *
     * @param refreshToken The refresh token string to be invalidated.
     */
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.findByToken(refreshToken)
                .ifPresent(refreshTokenService::delete);
    }
}
