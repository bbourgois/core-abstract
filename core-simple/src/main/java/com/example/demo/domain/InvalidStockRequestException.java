package com.example.demo.domain;

public class InvalidStockRequestException extends RuntimeException{
    public InvalidStockRequestException(String message) {
        super(message);
    }
}
