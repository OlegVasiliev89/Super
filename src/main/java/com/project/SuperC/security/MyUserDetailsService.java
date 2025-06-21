package com.project.SuperC.security;

import com.project.SuperC.models.User;
import com.project.SuperC.repository.UserRepository;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(MyUserDetailsService.class);

    private final UserRepository userRepository;

    @Autowired
    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user with email: {}", username);
        User user = (User) userRepository.findByEmail(username);

        if(user == null){
            log.warn("User with email {} not found.", username);
            throw new UsernameNotFoundException("User does not exist!");
        }

        return new UserPrincipal(user);
    }
}