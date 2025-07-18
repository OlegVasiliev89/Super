/**
 * Custom implementation of Spring Security's {@link UserDetails} interface.
 * This class wraps a {@link User} entity and provides the necessary user information
 * (username, password, authorities, and account status) that Spring Security
 * requires for authentication and authorization.
 */
package com.project.SuperC.security;

import com.project.SuperC.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl() {
    }

    /**
     * Private constructor used by the build method to create an instance of UserDetailsImpl.
     *
     * @param id The unique identifier of the user.
     * @param email The email (username) of the user.
     * @param password The hashed password of the user.
     * @param authorities A collection of granted authorities (roles) for the user.
     */
    private UserDetailsImpl(Long id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Builds a UserDetailsImpl object from a {@link User} entity.
     * This static factory method converts the user's roles into Spring Security's
     * {@link GrantedAuthority} objects.
     *
     * @param user The {@link User} entity from which to build UserDetails.
     * @return A new instance of {@link UserDetailsImpl}.
     */
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }


    public Long getId() {
        return id;
    }

    /**
     * Returns the username used to authenticate the user. In this application,
     * the email address serves as the username.
     * @return The user's email address.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Returns the password used to authenticate the user.
     * @return The user's hashed password.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the authorities granted to the user.
     * @return A collection of {@link GrantedAuthority} objects.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Indicates whether the user's account has expired.
     * This implementation always returns true, meaning accounts do not expire.
     * @return true if the user's account is valid, false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     * This implementation always returns true, meaning accounts are not locked.
     * @return true if the user is not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Compares this UserDetailsImpl object with another object for equality.
     * Two UserDetailsImpl objects are considered equal if their IDs are equal.
     *
     * @param o The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl that = (UserDetailsImpl) o;
        return id != null && id.equals(that.id);
    }

    /**
     * Returns a hash code value for the object.
     * The hash code is based on the user's ID.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
