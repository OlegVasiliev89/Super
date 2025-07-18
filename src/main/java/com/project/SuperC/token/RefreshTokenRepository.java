/**
 * Repository interface for managing {@link RefreshToken} entities.
 * This interface extends {@link JpaRepository} to provide standard CRUD operations
 * and custom query methods for refresh tokens.
 */
package com.project.SuperC.token;

import com.project.SuperC.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    /**
     * Finds a refresh token by its unique token string.
     *
     * @param token The unique token string.
     * @return An {@link Optional} containing the {@link RefreshToken} if found, or empty if not.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Deletes all refresh tokens associated with a specific user.
     *
     * @param user The {@link User} entity whose refresh tokens are to be deleted.
     */
    void deleteByUser(User user);
}
