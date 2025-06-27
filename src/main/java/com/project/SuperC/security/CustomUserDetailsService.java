package com.project.SuperC.security;

import com.project.SuperC.models.User;
import com.project.SuperC.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * This service is responsible for loading user-specific data during authentication.
 * It fetches user details from the UserRepository and constructs a UserDetails object
 */
@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Locates the user based on the email.
     * @param email The email identifying the user whose data is required.
     * @return A fully populated user record (UserPrincipal instance).
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return UserPrincipal.create(user);
    }

    /**
     * Maps the roles from User entity to Spring Security's GrantedAuthority objects.
     * @param roles A set of Role entities associated with the user.
     * @return A collection of GrantedAuthority objects.
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(java.util.Set<com.project.SuperC.models.Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }
}