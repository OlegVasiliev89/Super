/**
 * Repository interface for managing {@link PasswordResetToken} entities.
 * This interface extends {@link JpaRepository} to provide standard CRUD operations
 * and custom query methods for password reset tokens.
 */
package com.project.SuperC.repository;

import com.project.SuperC.models.PasswordResetToken;
import com.project.SuperC.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    /**
     * Finds a password reset token by its unique token string.
     *
     * @param token The unique token string.
     * @return An {@link Optional} containing the {@link PasswordResetToken} if found, or empty if not.
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Finds a password reset token by the associated user.
     *
     * @param user The {@link User} entity associated with the token.
     * @return An {@link Optional} containing the {@link PasswordResetToken} if found, or empty if not.
     */
    Optional<PasswordResetToken> findByUser(User user);
}
