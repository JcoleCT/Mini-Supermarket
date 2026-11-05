
package com.supermarket.model;

import com.supermarket.exception.InsufficientStockException;
import com.supermarket.util.ValidationUtils;
import java.io.Serializable;

public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private String name;
    private double price;
    private int quantity;

    public Product(String id, String name,
                   double price, int quantity) {

        ValidationUtils.requireText(id, "Ma san pham");
        this.id = id.trim();

        setName(name);
        setPrice(price);
        setQuantity(quantity);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        ValidationUtils.requireText(name, "Ten san pham");
        this.name = name.trim();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        ValidationUtils.requirePositivePrice(price);
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        ValidationUtils.requireNonNegativeQuantity(quantity);
        this.quantity = quantity;
    }

    public void decreaseStock(int amount) {
        ValidationUtils.requirePositiveQuantity(amount);

        if (amount > quantity) {
            throw new InsufficientStockException(
                    "Khong du hang trong kho! "
                            + "Ton kho: " + quantity
                            + ", yeu cau: " + amount);
        }

        quantity -= amount;
    }

    @Override
    public String toString() {
        return id + " - " + name
                + " - Gia: " + price
                + " - So luong: " + quantity;
    }
}
