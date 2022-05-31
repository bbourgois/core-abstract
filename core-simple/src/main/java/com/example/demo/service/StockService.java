package com.example.demo.service;

import com.example.demo.core.AbstractStockCore;
import com.example.demo.core.Implementation;
import com.example.demo.domain.InvalidStockRequestException;
import com.example.demo.domain.StockEntity;
import com.example.demo.dto.in.ShoeFilter;
import com.example.demo.dto.in.StockDetail;
import com.example.demo.dto.out.State;
import com.example.demo.dto.out.Stock;
import com.example.demo.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Implementation(version = 1)
public class StockService extends AbstractStockCore {

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
    @Transactional
    public void update(StockDetail stockDetail) {
        if (stockDetail.getQuantity() > 0) {
            addInStock(stockDetail);
        } else {
            removeFromStock(stockDetail);
        }

    }

    private void removeFromStock(StockDetail stockDetail) {
        StockEntity toUpdate = checkIfShoeBoxIsInStock(stockDetail);
        Integer newQuantity = checkQuantityInStock(stockDetail, toUpdate);

        if (newQuantity == 0) {
            stockRepository.delete(toUpdate);
        } else {
            toUpdate.setQuantity(newQuantity);
            stockRepository.save(toUpdate);
        }
    }

    private Integer checkQuantityInStock(StockDetail stockDetail, StockEntity toUpdate) {
        int newQuantity = toUpdate.getQuantity() + stockDetail.getQuantity();
        if (newQuantity < 0) {
            throw new InvalidStockRequestException("There are not enough shoes in stock");
        }
        return newQuantity;
    }

    private StockEntity checkIfShoeBoxIsInStock(StockDetail stockDetail) {
        StockEntity stockEntity = findInStock(stockDetail);
        if (stockEntity == null) {
            throw new InvalidStockRequestException("Shoes are not in stock");
        }
        return stockEntity;
    }

    private void addInStock(StockDetail stockDetail) {
        Integer quantity = stockDetail.getQuantity();
        checkMaxCapacity(quantity);

        StockEntity toUpdate = findInStock(stockDetail);
        if (toUpdate != null) {
            toUpdate.setQuantity(toUpdate.getQuantity() + quantity);
            stockRepository.save(toUpdate);
        } else {
            StockEntity toInsert = new StockEntity();
            toInsert.setName(stockDetail.getName());
            toInsert.setSize(stockDetail.getSize());
            toInsert.setColor(StockEntity.Color.valueOf(stockDetail.getColor().toString()));
            toInsert.setQuantity(quantity);
            stockRepository.save(toInsert);
        }
    }

    private StockEntity findInStock(StockDetail stockDetail) {
        List<StockEntity> alreadyInStock = stockRepository.findByNameAndSizeAndColor(stockDetail.getName(), stockDetail.getSize(),
                StockEntity.Color.valueOf(stockDetail.getColor().toString()));
        return alreadyInStock.size() > 0 ? alreadyInStock.get(0) : null;
    }

    private void checkMaxCapacity(Integer quantity) {
        Integer stockCapacity = stockRepository.getStockCapacity();
        int newStockCapacity = stockCapacity == null ? quantity : stockCapacity + quantity;

        if (newStockCapacity > MAX_CAPACITY) {
            throw new InvalidStockRequestException("Max capacity");
        }
    }


}