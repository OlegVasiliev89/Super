package com.project.SuperC.service;

import com.project.SuperC.models.User;
import com.project.SuperC.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder; // Only need this interface
import org.springframework.stereotype.Service; // Changed from @Component to @Service for semantic clarity
import org.springframework.beans.factory.annotation.Autowired;

@Service // Use @Service for service classes
public class UserService {

    // Only need the interface here, as SecurityConfig provides the BCryptPasswordEncoder bean
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired // @AllArgsConstructor from Lombok would also work
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User addUser (User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }
}