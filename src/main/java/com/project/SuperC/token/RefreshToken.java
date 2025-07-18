/**
 * Represents a refresh token entity in the database.
 * Refresh tokens are used to obtain new access tokens after the current ones expire,
 * allowing users to maintain their session without re-authenticating frequently
 */
package com.project.SuperC.token;

import com.project.SuperC.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    /**
     * The unique identifier for the refresh token.
     * This field is the primary key and is auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique token string itself.
     * This field cannot be null and must be unique across all refresh tokens.
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * The user associated with this refresh token.
     * This establishes a many-to-one relationship with the {@link User} entity.
     * This field is mandatory and cannot be null.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The expiration date and time of the refresh token.
     * This field cannot be null.
     */
    @Column(nullable = false)
    private Instant expiryDate;
}
