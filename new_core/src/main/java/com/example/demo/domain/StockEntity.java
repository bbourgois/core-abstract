package com.example.demo.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "stock")
@Getter
@Setter
public class StockEntity {

    private static final int MAX_CAPACITY = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_generator")
    @SequenceGenerator(name = "stock_generator", sequenceName = "stock_id_seq", allocationSize = 1)
    private long id;

    @ManyToOne
    private ShoeEntity shoe;


    private Integer quantity;


}
