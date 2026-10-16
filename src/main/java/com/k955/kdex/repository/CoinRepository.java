package com.k955.kdex.repository;

import com.k955.kdex.entity.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CoinRepository extends JpaRepository<Coin, UUID> {
}
