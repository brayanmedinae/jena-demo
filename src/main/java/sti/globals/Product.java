package sti.globals;

import lombok.*;

@Getter
@Setter
@ToString
public class Product {
    private int id;
    private String category;
    private double price;
    private double discount;

    public Product(int id, String category, double price) {
        this.id = id;
        this.category = category;
        this.price = price;
    }
}
