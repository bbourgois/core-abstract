package com.example.demo.dto.out;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class Stock {

    private State state;

    Map<Shoe, Integer> stock;

}
