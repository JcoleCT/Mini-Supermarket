
package com.supermarket.service;

import com.supermarket.model.Product;
import com.supermarket.model.Invoice;
import com.supermarket.model.InvoiceItem;

import com.supermarket.storage.FileStorage;
import com.supermarket.storage.InvoiceStorage;

import com.supermarket.exception.InvalidDataException;
import com.supermarket.exception.InsufficientStockException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SalesService {

    // =====================================
    // 1. CAC SERVICE VA STORAGE
    // =====================================

    private final ProductService productService;
    private final FileStorage productStorage;
    private final InvoiceStorage invoiceStorage;

    // Gio hang: Ma san pham -> So luong mua
    private final Map<String, Integer> cart;

    // Danh sach hoa don da thanh toan
    private final List<Invoice> invoices;

    // =====================================
    // 2. CONSTRUCTOR
    // =====================================

    public SalesService(
            ProductService productService,
            FileStorage productStorage,
            InvoiceStorage invoiceStorage,
            List<Invoice> loadedInvoices) {

        if (productService == null
                || productStorage == null
                || invoiceStorage == null
                || loadedInvoices == null) {

            throw new IllegalArgumentException(
                    "Du lieu khoi tao SalesService khong hop le!");
        }

        this.productService = productService;
        this.productStorage = productStorage;
        this.invoiceStorage = invoiceStorage;

        this.cart = new LinkedHashMap<>();
        this.invoices = new ArrayList<>(loadedInvoices);
    }

    // =====================================
    // 3. THEM SAN PHAM VAO GIO HANG
    // =====================================

    public void addToCart(String productId, int quantity) {

        if (quantity <= 0) {
            throw new InvalidDataException(
                    "So luong mua phai lon hon 0!");
        }

        Product product =
                productService.getProductOrThrow(productId);

        String id = product.getId();

        int currentQuantity =
                cart.getOrDefault(id, 0);

        // Dung long de tranh tran so nguyen
        long totalQuantity =
                (long) currentQuantity + quantity;

        if (totalQuantity > product.getQuantity()) {

            throw new InsufficientStockException(
                    "San pham " + product.getName()
                            + " khong du ton kho!"
                            + " Hien co: " + product.getQuantity()
                            + ", yeu cau: " + totalQuantity);
        }

        cart.put(id, (int) totalQuantity);
    }

    // =====================================
    // 4. XOA MOT SAN PHAM KHOI GIO
    // =====================================

    public void removeFromCart(String productId) {
        if (productId == null || productId.isBlank()) {
            return;
        }

        String key = cart.keySet().stream()
                .filter(id -> id.equalsIgnoreCase(
                        productId.trim()))
                .findFirst()
                .orElse(null);

        if (key != null) {
            cart.remove(key);
        }
    }

    // =====================================
    // 5. HUY TOAN BO GIO HANG
    // =====================================

    public void clearCart() {
        cart.clear();
    }

    // =====================================
    // 6. KIEM TRA GIO HANG RONG
    // =====================================

    public boolean isCartEmpty() {
        return cart.isEmpty();
    }

    // =====================================
    // 7. DEM SO LOAI SAN PHAM TRONG GIO
    // =====================================

    public int countCartProducts() {
        return cart.size();
    }

    // =====================================
    // 8. TINH TONG SO LUONG TRONG GIO
    // =====================================

    public int getTotalCartQuantity() {

        return cart.values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    // =====================================
    // 9. LAY DANH SACH CHI TIET GIO HANG
    // =====================================

    public List<InvoiceItem> getCartItems() {

        List<InvoiceItem> items = new ArrayList<>();

        for (Map.Entry<String, Integer> entry
                : cart.entrySet()) {

            Product product =
                    productService.getProductOrThrow(
                            entry.getKey());

            int quantity = entry.getValue();

            // Kiem tra lai ton kho hien tai
            if (quantity > product.getQuantity()) {

                throw new InsufficientStockException(
                        "San pham " + product.getName()
                                + " khong du ton kho!"
                                + " Hien co: " + product.getQuantity()
                                + ", yeu cau: " + quantity);
            }

            InvoiceItem item =
                    new InvoiceItem(product, quantity);

            items.add(item);
        }

        return items;
    }

    // =====================================
    // 10. TINH TONG TIEN GIO HANG
    // =====================================

    public double calculateTotal() {

        return getCartItems()
                .stream()
                .mapToDouble(InvoiceItem::getSubtotal)
                .sum();
    }

    // =====================================
    // 11. TAO MA HOA DON KHONG TRUNG
    // =====================================

    private String generateInvoiceId() {

        Set<String> existingIds = new HashSet<>();

        for (Invoice invoice : invoices) {
            existingIds.add(invoice.getInvoiceId());
        }

        String id;

        do {
            id = "HD-"
                    + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();

        } while (existingIds.contains(id));

        return id;
    }

    // =====================================
    // 12. THANH TOAN
    // =====================================

    public Invoice checkout() throws IOException {

        // Buoc 1: Kiem tra gio hang
        if (cart.isEmpty()) {
            throw new InvalidDataException(
                    "Gio hang dang rong, khong the thanh toan!");
        }

        // Buoc 2: Kiem tra ton kho va tao chi tiet
        List<InvoiceItem> items = getCartItems();

        // Buoc 3: Tao hoa don
        Invoice invoice =
                new Invoice(generateInvoiceId());

        for (InvoiceItem item : items) {
            invoice.addItem(item);
        }

        // Buoc 4: Luu so luong ton kho cu
        Map<Product, Integer> oldStock =
                new LinkedHashMap<>();

        for (InvoiceItem item : items) {

            Product product =
                    productService.getProductOrThrow(
                            item     .getProductId());

            oldStock.put(
                    product,
                    product.getQuantity());
        }

        // Buoc 5: Tru ton kho trong RAM
        try {
            for (InvoiceItem item : items) {

                Product product =
                        productService.getProductOrThrow(
                                item.getProductId());

                product.decreaseStock(
                        item.getQuantity());
            }

        } catch (RuntimeException e) {

            // Khoi phuc ton kho neu tru kho that bai
            restoreStock(oldStock);
            throw e;
        }

        // Buoc 6: Luu hoa don truoc
        // Chua dua hoa don vao danh sach chinh
        List<Invoice> newInvoices =
                new ArrayList<>(invoices);

        newInvoices.add(invoice);

        try {
            invoiceStorage.saveInvoices(newInvoices);

        } catch (IOException e) {

            restoreStock(oldStock);
            throw e;
        }

        // Buoc 7: Luu ton kho sau khi tru
        try {
            productStorage.saveProducts(
                    productService.getAllProducts());

        } catch (IOException e) {

            restoreStock(oldStock);

            // Co gang phuc hoi file hoa don cu
            try {
                invoiceStorage.saveInvoices(invoices);

            } catch (IOException rollbackError) {
                e.addSuppressed(rollbackError);
            }

            throw e;
        }

        // Buoc 8: Xac nhan thanh toan thanh cong
        invoices.add(invoice);

        // Xoa gio hang sau khi thanh toan
        cart.clear();

        return invoice;
    }

    // =====================================
    // 13. KHOI PHUC TON KHO TRONG RAM
    // =====================================

    private void restoreStock(
            Map<Product, Integer> oldStock) {

        for (Map.Entry<Product, Integer> entry
                : oldStock.entrySet()) {

            Product product = entry.getKey();
            int quantity = entry.getValue();

            product.setQuantity(quantity);
        }
    }

    // =====================================
    // 14. LAY DANH SACH HOA DON
    // =====================================

    public List<Invoice> getInvoices() {
        return new ArrayList<>(invoices);
    }

    // =====================================
    // 15. DEM SO HOA DON
    // =====================================

    public int countInvoices() {
        return invoices.size();
    }

    // =====================================
    // 16. TINH TONG DOANH THU
    // =====================================

    public double calculateTotalRevenue() {

        return invoices.stream()
                .mapToDouble(Invoice::calculateTotal)
                .sum();
    }

    // =====================================
    // 17. TIM HOA DON THEO MA
    // =====================================

    public Invoice findInvoiceById(String invoiceId) {

        if (invoiceId == null || invoiceId.isBlank()) {
            throw new InvalidDataException(
                    "Ma hoa don khong duoc rong!");
        }

        return invoices.stream()
                .filter(invoice ->
                        invoice.getInvoiceId()
                                .equalsIgnoreCase(
                                        invoiceId.trim()))
                .findFirst()
                .orElseThrow(() ->
                        new InvalidDataException(
                                "Khong tim thay hoa don: "
                                        + invoiceId));
    }
}
