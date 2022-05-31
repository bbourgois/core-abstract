package com.example.demo.dto.in;

import lombok.Value;

import java.math.BigInteger;
import com.example.demo.dto.in.ShoeFilter.Color;


@Value
public class StockDetail {

    BigInteger size;
    String name;
    Color color;
    Integer quantity;


}
