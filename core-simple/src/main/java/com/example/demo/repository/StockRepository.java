package com.example.demo.repository;

import com.example.demo.domain.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<StockEntity, Long> {

    @Query("SELECT SUM(s.quantity) FROM StockEntity s")
    Integer getStockCapacity();
}
