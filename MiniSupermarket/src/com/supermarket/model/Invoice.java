
package com.supermarket.model;

import com.supermarket.interfaces.Payable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Invoice implements Payable, Serializable {

    private static final long serialVersionUID = 1L;

    private final String invoiceId;
    private final LocalDateTime createdAt;
    private final List<InvoiceItem> items;

    public Invoice(String invoiceId) {

        if (invoiceId == null || invoiceId.isBlank()) {
            throw new IllegalArgumentException(
                    "Ma hoa don khong duoc rong!");
        }

        this.invoiceId = invoiceId.trim();
        this.createdAt = LocalDateTime.now();
        this.items = new ArrayList<>();
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void addItem(InvoiceItem item) {

        if (item == null) {
            throw new IllegalArgumentException(
                    "Chi tiet hoa don khong hop le!");
        }

        items.add(item);
    }

    public List<InvoiceItem> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    public double calculateTotal() {
        double total = 0;

        for (InvoiceItem item : items) {
            total += item.getSubtotal();
        }

        return total;
    }
}
