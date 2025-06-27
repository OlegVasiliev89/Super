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

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeAdminUser();
    }

    private void initializeRoles() {
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