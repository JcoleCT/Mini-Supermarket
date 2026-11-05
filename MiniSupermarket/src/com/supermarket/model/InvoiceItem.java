
package com.supermarket.model;

import com.supermarket.exception.InsufficientStockException;
import com.supermarket.util.ValidationUtils;
import java.io.Serializable;

public class InvoiceItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String productId;
    private final String productName;
    private final double unitPrice;
    private final int quantity;

    public InvoiceItem(Product product, int quantity) {

        if (product == null) {
            throw new IllegalArgumentException(
                    "San pham khong duoc null!");
        }

        ValidationUtils.requirePositiveQuantity(quantity);

        if (quantity > product.getQuantity()) {
            throw new InsufficientStockException(
                    "So luong mua vuot ton kho!");
        }

        this.productId = product.getId();
        this.productName = product.getName();
        this.unitPrice = product.getPrice();
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }
}
