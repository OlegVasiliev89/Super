package com.project.SuperC.security;

import com.project.SuperC.models.User;
import com.project.SuperC.repository.UserRepository;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(MyUserDetailsService.class); // Add logger

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user with email: {}", username); // Log username being loaded
        User user = (User) userRepository.findByEmail(username);

        if(user == null){
            log.warn("User with email {} not found.", username); // Log if user not found
            throw new UsernameNotFoundException("User does not exist!");
        }
        log.info("User found: {}", user.getEmail()); // Log user found
        // IMPORTANT: DO NOT LOG THE ACTUAL PASSWORD HERE IN PRODUCTION
        // For debugging, you could temporarily log the hashed password from DB:
        // log.info("User's stored hashed password: {}", user.getPassword());
        return new UserPrincipal(user);
    }
}