package com.example.demo.domain;

import javax.persistence.Entity;
import java.util.Map;

@Entity
public class Stock {

    private static final int MAX_CAPACITY = 30;

    private Map<Shoe, Integer> stock;


}
