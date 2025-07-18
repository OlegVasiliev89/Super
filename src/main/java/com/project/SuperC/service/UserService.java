/**
 * Service class for managing user-related operations.
 * This service handles business logic such as user registration and retrieving user details.
 */
package com.project.SuperC.service;

import com.project.SuperC.auth.RegisterRequest;
import com.project.SuperC.models.Role;
import com.project.SuperC.models.RoleName;
import com.project.SuperC.models.User;
import com.project.SuperC.repository.RoleRepository;
import com.project.SuperC.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a UserService with the necessary repositories and password encoder.
     * Spring automatically injects these dependencies.
     *
     * @param userRepository The repository for accessing user data.
     * @param roleRepository The repository for accessing role data.
     * @param passwordEncoder The encoder for hashing user passwords.
     */
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user in the application.
     * Before registration, it checks if a user with the provided email already exists.
     * The user's password will be encoded, and the ROLE_USER will be assigned by default.
     *
     * @param registerRequest A {@link RegisterRequest} object containing the user's email and password.
     * @return The newly registered and saved {@link User} entity.
     * @throws RuntimeException if a user with the given email already exists, or if ROLE_USER is not found.
     */
    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists!");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found in the database. Please ensure it exists."));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    /**
     * Retrieves a user by their unique identifier.
     * This operation is read-only.
     *
     * @param id The unique ID of the user to retrieve.
     * @return The {@link User} entity if found.
     * @throws UsernameNotFoundException if the user with the specified ID is not found.
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }
}
