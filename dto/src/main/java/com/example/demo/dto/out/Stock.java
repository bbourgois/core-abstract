package com.example.demo.dto.out;

import com.example.demo.dto.in.StockDetail;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Stock {

    State state;
    List<StockDetail> shoes;

}
