package com.example.demo.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(
        name = "shoe",
        uniqueConstraints = {
                @UniqueConstraint(name = "model", columnNames = {"name", "size", "color"})
        }
)
@Getter
@Setter
public class ShoeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shoe_generator")
    @SequenceGenerator(name = "shoe_generator", sequenceName = "shoe_id_seq", allocationSize = 1)
    private long id;
    private String name;
    private BigInteger size;
    private Color color;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShoeEntity shoe = (ShoeEntity) o;

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


    public enum Color {

        BLACK,
        BLUE,
        ;

    }
}
