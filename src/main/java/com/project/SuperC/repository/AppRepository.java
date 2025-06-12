package com.project.SuperC.repository;

import com.project.SuperC.models.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Request entities.
 * Extends JpaRepository to provide standard CRUD (Create, Read, Update, Delete)
 */
@Repository
public interface AppRepository extends JpaRepository<Request, Integer> {
}