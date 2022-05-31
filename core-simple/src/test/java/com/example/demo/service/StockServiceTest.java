package com.example.demo.service;


import com.example.demo.domain.StockEntity;
import com.example.demo.dto.out.State;
import com.example.demo.dto.out.Stock;
import com.example.demo.repository.StockRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class StockServiceTest {

    @InjectMocks
    private StockService stockService;
    @Mock
    private StockRepository stockRepository;

    @Test
    public void get_empty_stock() {
        Mockito.when(stockRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        Stock stock = stockService.get();

        assertThat(stock.getState()).isEqualTo(State.EMPTY);
        assertThat(stock.getShoes()).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void get_full_stock() {
        StockEntity stockEntity = mockStockEntityHikingShoesWithQuantity(StockService.MAX_CAPACITY);
        List<StockEntity> stock = List.of(stockEntity);
        Mockito.when(stockRepository.findAll()).thenReturn(stock);

        Stock res = stockService.get();

        assertThat(res.getState()).isEqualTo(State.FULL);
        assertThat(res.getShoes().size()).isEqualTo(stock.size());
    }

    @Test
    public void get_some_stock() {
        StockEntity stockEntityBlue = mockStockEntityHikingShoesWithQuantity(20);
        StockEntity stockEntityBlack = mockStockEntityOtherShoesWithQuantity(5);
        List<StockEntity> stock = List.of(stockEntityBlue, stockEntityBlack);
        Mockito.when(stockRepository.findAll()).thenReturn(stock);

        Stock res = stockService.get();

        assertThat(res.getState()).isEqualTo(State.SOME);
        assertThat(res.getShoes().size()).isEqualTo(stock.size());
    }


    private StockEntity mockStockEntityHikingShoesWithQuantity(int quantity) {
        StockEntity blueShoes = new StockEntity();
        blueShoes.setName("hiking shoes");
        blueShoes.setColor(StockEntity.Color.BLUE);
        blueShoes.setSize(BigInteger.valueOf(39l));
        blueShoes.setQuantity(quantity);
        return blueShoes;
    }

    private StockEntity mockStockEntityOtherShoesWithQuantity(int quantity) {
        StockEntity blueShoes = new StockEntity();
        blueShoes.setName("other shoes");
        blueShoes.setColor(StockEntity.Color.BLACK);
        blueShoes.setSize(BigInteger.valueOf(39l));
        blueShoes.setQuantity(quantity);
        return blueShoes;
    }
}