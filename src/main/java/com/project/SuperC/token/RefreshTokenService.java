/**
 * Service class for managing refresh tokens.
 * This service handles the creation, retrieval, validation, and deletion of refresh tokens,
 * which are used to obtain new access tokens without requiring users to re-authenticate.
 */
package com.project.SuperC.token;

import com.project.SuperC.repository.UserRepository;
import com.project.SuperC.models.User;
import com.project.SuperC.token.RefreshToken;
import com.project.SuperC.token.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    /**
     * Constructs a RefreshTokenService with the necessary repositories.
     * Spring automatically injects these dependencies.
     *
     * @param refreshTokenRepository The repository for managing refresh token entities.
     * @param userRepository The repository for accessing user data.
     */
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new refresh token for a specified user.
     * Any existing refresh tokens for the user will be deleted before creating a new one.
     * The token is a randomly generated UUID, and its expiry date is calculated based on
     * the configured expiration time.
     *
     * @param userId The ID of the user for whom to create the refresh token.
     * @return The newly created and saved {@link RefreshToken} entity.
     * @throws RuntimeException if the user with the specified ID is not found.
     */
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        refreshTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(refreshTokenExpirationMs);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(expiryDate)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Finds a refresh token by its token string.
     *
     * @param token The unique token string of the refresh token.
     * @return An {@link Optional} containing the {@link RefreshToken} if found, or empty if not.
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Verifies if a given refresh token has expired.
     * If the token has expired, it is deleted from the repository, and a runtime exception is thrown.
     *
     * @param token The {@link RefreshToken} to verify.
     * @return The verified {@link RefreshToken} if it has not expired.
     * @throws RuntimeException if the refresh token has expired.
     */
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new sign-in request");
        }
        return token;
    }

    /**
     * Deletes all refresh tokens associated with a specific user ID.
     * This method first attempts to find the user by ID and then deletes their tokens.
     *
     * @param userId The ID of the user whose refresh tokens are to be deleted.
     */
    @Transactional
    public void deleteByUserId(Long userId) {
        userRepository.findById(userId).ifPresent(refreshTokenRepository::deleteByUser);
    }

    /**
     * Deletes a specific refresh token from the repository.
     *
     * @param refreshToken The {@link RefreshToken} entity to delete.
     */
    @Transactional
    public void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}
