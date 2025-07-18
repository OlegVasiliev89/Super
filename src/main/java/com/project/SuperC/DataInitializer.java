package com.project.SuperC;

import com.project.SuperC.models.Role;
import com.project.SuperC.models.RoleName;
import com.project.SuperC.models.User;
import com.project.SuperC.repository.RoleRepository;
import com.project.SuperC.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Component that initializes essential data in the database upon application startup.
 * This class implements {@link CommandLineRunner}, meaning its {@code run} method
 * will be executed once the Spring application context is fully loaded.
 * It's primarily used to ensure that default roles and an initial admin user exist.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a DataInitializer with necessary repositories and password encoder.
     * Spring's dependency injection automatically provides these beans.
     *
     * @param roleRepository The repository for managing {@link Role} entities.
     * @param userRepository The repository for managing {@link User} entities.
     * @param passwordEncoder The encoder used for hashing user passwords.
     */
    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Callback method executed after the application context is loaded.
     * This method orchestrates the initialization of roles and the admin user.
     *
     * @param args Command line arguments (not used in this implementation).
     * @throws Exception If an error occurs during data initialization.
     */
    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeAdminUser();
    }

    /**
     * Initializes default application roles (ROLE_USER and ROLE_ADMIN) if they do not already exist in the database.
     * This ensures that the foundational roles required for user authentication and authorization are present.
     */
    private void initializeRoles() {
        // Check if ROLE_USER exists, create if not
        Optional<Role> userRoleOptional = roleRepository.findByName(RoleName.ROLE_USER);
        if (userRoleOptional.isEmpty()) {
            Role userRole = new Role();
            userRole.setName(RoleName.ROLE_USER);
            roleRepository.save(userRole);
            System.out.println("Created ROLE_USER");
        }

        Optional<Role> adminRoleOptional = roleRepository.findByName(RoleName.ROLE_ADMIN);
        if (adminRoleOptional.isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(RoleName.ROLE_ADMIN);
            roleRepository.save(adminRole);
            System.out.println("Created ROLE_ADMIN");
        }
    }

    /**
     * Initializes a default administrator user ("admin@example.com") if one does not already exist.
     * This method also assigns the ROLE_ADMIN to the newly created user.
     *
     * <p><b>Security Note:</b> The password is currently an empty string after encoding.
     * For production environments, this should be replaced with a strong, securely managed password
     * or a mechanism for the administrator to set their password upon first login.</p>
     */
    private void initializeAdminUser() {
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User adminUser = new User();
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(passwordEncoder.encode(""));

            Optional<Role> adminRoleOptional = roleRepository.findByName(RoleName.ROLE_ADMIN);

            if (adminRoleOptional.isPresent()) {
                Set<Role> roles = new HashSet<>();
                roles.add(adminRoleOptional.get());
                adminUser.setRoles(roles);
                userRepository.save(adminUser);
                System.out.println("Created admin user: admin@example.com");
            } else {
                System.err.println("Warning: ROLE_ADMIN not found, admin user not created with role.");
            }
        }
    }
}
