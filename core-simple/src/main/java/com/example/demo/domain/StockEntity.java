package com.example.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "stock",
        uniqueConstraints = {
                @UniqueConstraint(name = "shoe", columnNames = {"name", "size", "color"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_generator")
    @SequenceGenerator(name = "stock_generator", sequenceName = "stock_id_seq", allocationSize = 1)
    private long id;

    private String name;

    private BigInteger size;

    private Color color;

    private Integer quantity;

    public enum Color {
        BLACK,
        BLUE,
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockEntity that = (StockEntity) o;

        if (!name.equals(that.name)) return false;
        if (!size.equals(that.size)) return false;
        return color == that.color;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + size.hashCode();
        result = 31 * result + color.hashCode();
        return result;
    }
}
