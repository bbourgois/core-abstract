package com.example.demo.dto.in;

import lombok.Builder;
import lombok.Value;

import java.math.BigInteger;

import com.example.demo.dto.in.ShoeFilter.Color;

import javax.validation.constraints.*;


@Value
@Builder
public class StockDetail {
    @Positive(message = "Size is positive")
    BigInteger size;
    @NotBlank(message = "Name is mandatory")
    String name;
    @NotNull(message = "Color is mandatory")
    Color color;
    @NotNull(message = "Quantity is mandatory")
    @Max(value = 30)
    @Min(value = -30)
    Integer quantity;


}
