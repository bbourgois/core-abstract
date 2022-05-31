package com.example.demo.controller;

import com.example.demo.domain.InvalidStockRequestException;
import com.example.demo.dto.in.StockDetail;
import com.example.demo.dto.out.Stock;
import com.example.demo.facade.StockFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockFacade stockFacade;

    @GetMapping
    public ResponseEntity<Stock> stock(@RequestHeader Integer version){
        return ResponseEntity.ok(stockFacade.get(version).get());
    }

    @PatchMapping
    public void update(@RequestHeader Integer version, @RequestBody StockDetail stockDetail){
        try {
            stockFacade.get(version).update(stockDetail);
        } catch (InvalidStockRequestException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

}
