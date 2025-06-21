package com.project.SuperC.service;

import com.project.SuperC.models.User;
import com.project.SuperC.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder; // Only need this interface
import org.springframework.stereotype.Service; // Changed from @Component to @Service for semantic clarity
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
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