package com.project.SuperC.repository;

import com.project.SuperC.models.Request;
// lombok.Data is not needed here as it's an interface, not a data class.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Request} entities.
 * Extends JpaRepository to provide standard CRUD (Create, Read, Update, Delete)
 * operations and custom query capabilities for the Request entity.
 * Spring Data JPA automatically provides the implementation for this interface.
 */
@Repository // Marks this interface as a Spring Data JPA repository.
public interface AppRepository extends JpaRepository<Request, Integer> {
    // JpaRepository provides methods like save(), findAll(), findById(), deleteById() out of the box.
    // No custom methods are currently needed, but could be added here (e.g., findByEmail(String email)).
}