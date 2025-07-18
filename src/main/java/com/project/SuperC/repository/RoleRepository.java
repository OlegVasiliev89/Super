/**
 * Repository interface for managing {@link Role} entities.
 * This interface extends {@link JpaRepository} to provide standard CRUD operations
 * and a custom query method for retrieving roles by their name.
 */
package com.project.SuperC.repository;

import com.project.SuperC.models.Role;
import com.project.SuperC.models.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Finds a role by its name.
     *
     * @param name The {@link RoleName} enumeration value representing the name of the role.
     * @return An {@link Optional} containing the {@link Role} if found, or empty if not.
     */
    Optional<Role> findByName(RoleName name);
}
