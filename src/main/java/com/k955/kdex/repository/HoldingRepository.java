package com.k955.kdex.repository;

import com.k955.kdex.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, UUID> {

    boolean existsByUserIdAndCoinId(UUID userId, UUID coinId);

    Optional<Holding> findByUserIdAndCoinId(UUID userId, UUID coinId);

    List<Holding> findAllByUserId(UUID userId);

}
