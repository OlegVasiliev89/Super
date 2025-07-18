/**
 * This service is responsible for loading user-specific data during the authentication process.
 * It retrieves user details from the database using the {@link UserRepository}
 * and constructs a {@link UserDetails} object for Spring Security.
 */
package com.project.SuperC.security;

import com.project.SuperC.models.User;
import com.project.SuperC.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    /**
     * Constructs a CustomUserDetailsService with the necessary UserRepository.
     *
     * @param userRepository The repository for accessing user data.
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Locates the user based on the username (email in this case).
     * @param email The email address of the user.
     * @return A fully populated user record (an instance of {@link UserDetailsImpl})
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     * granted authorities.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Attempting to load user by email: {}", email);
        Optional<User> userOptional = userRepository.findByEmail(email);

        User user = userOptional.orElseThrow(() -> {
            logger.error("User not found with email: {}", email);
            return new UsernameNotFoundException("User not found with email: " + email);
        });

        logger.info("Successfully loaded user: {} from DB. User ID: {}", user.getEmail(), user.getId());

        return UserDetailsImpl.build(user);
    }
}
