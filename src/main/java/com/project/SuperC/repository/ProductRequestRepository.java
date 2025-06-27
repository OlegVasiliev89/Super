package com.project.SuperC.repository;

import com.project.SuperC.models.PriceTrackingRequest;
import com.project.SuperC.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRequestRepository extends JpaRepository<PriceTrackingRequest, Long> {
    List<PriceTrackingRequest> findByUser(User user);
}