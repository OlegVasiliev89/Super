/**
 * Repository interface for managing {@link User} entities.
 * This interface extends {@link JpaRepository} to provide standard CRUD operations
 * and a custom query method for retrieving users by their email.
 */
package com.project.SuperC.repository;

import com.project.SuperC.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by their email address.
     *
     * @param email The email address of the user.
     * @return An {@link Optional} containing the {@link User} if found, or empty if not.
     */
    Optional<User> findByEmail(String email);

}
