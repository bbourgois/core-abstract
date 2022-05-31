package com.example.demo.service;

import com.example.demo.core.AbstractStockCore;
import com.example.demo.core.Implementation;
import com.example.demo.domain.StockEntity;
import com.example.demo.dto.in.ShoeFilter;
import com.example.demo.dto.in.StockDetail;
import com.example.demo.dto.out.State;
import com.example.demo.dto.out.Stock;
import com.example.demo.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Implementation(version = 1)
public class StockService extends AbstractStockCore  {

    public static final int MAX_CAPACITY = 30;

    private final StockRepository stockRepository;

    @Autowired
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public Stock get() {
        List<StockEntity> allStock = stockRepository.findAll();
        List<StockDetail> shoes = allStock.stream()
                .map(s -> StockDetail.builder()
                        .name(s.getName())
                        .color(ShoeFilter.Color.valueOf(s.getColor().toString()))
                        .size(s.getSize())
                        .quantity(s.getQuantity())
                        .build())
                .collect(Collectors.toList());

        int nbShoesInStock = allStock
                .stream()
                .map(StockEntity::getQuantity)
                .reduce(0, Integer::sum);

        return Stock.builder()
                .state(getState(nbShoesInStock))
                .shoes(shoes).build();
    }

    private State getState(int nbShoesInStock) {
        return switch (nbShoesInStock) {
            case 0 -> State.EMPTY;
            case MAX_CAPACITY -> State.FULL;
            default -> State.SOME;
        };
    }

    @Override
    public void update(StockDetail stockDetail) {
        StockEntity toSave = new StockEntity();
        toSave.setName(stockDetail.getName());
        toSave.setSize(stockDetail.getSize());
        toSave.setColor(StockEntity.Color.valueOf(stockDetail.getColor().toString()));
        toSave.setQuantity(stockDetail.getQuantity());
        stockRepository.save(toSave);
    }

}
