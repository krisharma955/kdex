package com.k955.kdex.repository;

import com.k955.kdex.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, UUID> {

    List<PriceHistory> findByCoinSymbolOrderByRecordedAtAsc(String symbol);

}
