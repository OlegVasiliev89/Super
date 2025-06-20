package com.project.SuperC.repository;

import com.project.SuperC.models.Request;
import com.project.SuperC.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Request entities.
 * Extends JpaRepository to provide standard CRUD (Create, Read, Update, Delete)
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    UserDetails findByEmail(String email);
}
