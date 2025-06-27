package com.project.SuperC.repository;

import com.project.SuperC.models.PriceTrackingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceTrackingRequestRepository extends JpaRepository<PriceTrackingRequest, Long> {

    List<PriceTrackingRequest> findByUser_Id(Long userId);

    Optional<PriceTrackingRequest> findByIdAndUser_Id(Long id, Long userId);
}