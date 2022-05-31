package com.example.demo.repository;

import com.example.demo.domain.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface StockRepository extends JpaRepository<StockEntity, Long> {

    @Query("SELECT SUM(s.quantity) FROM StockEntity s")
    Integer getStockCapacity();

    List<StockEntity> findByNameAndSizeAndColor(String name, BigInteger size, StockEntity.Color color);
}
