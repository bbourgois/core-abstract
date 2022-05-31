package com.example.demo.core;

import com.example.demo.dto.in.StockDetail;
import com.example.demo.dto.out.Stock;

public interface StockCore {

  Stock get();

  void update(StockDetail stockDetail);


}
