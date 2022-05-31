package com.example.demo.repository;

import com.example.demo.domain.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<StockEntity, Long> {


}
