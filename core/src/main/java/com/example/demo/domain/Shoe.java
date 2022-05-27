package com.example.demo.domain;

import com.example.demo.dto.in.ShoeFilter;

import javax.persistence.Entity;
import java.math.BigInteger;

@Entity
public class Shoe {

    private String name;
    private BigInteger size;
    private ShoeFilter.Color color;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shoe shoe = (Shoe) o;

        if (!name.equals(shoe.name)) return false;
        if (!size.equals(shoe.size)) return false;
        return color == shoe.color;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + size.hashCode();
        result = 31 * result + color.hashCode();
        return result;
    }
}
