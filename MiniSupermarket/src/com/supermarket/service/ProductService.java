
package com.supermarket.service;

import com.supermarket.model.Product;
import com.supermarket.exception.InvalidDataException;
import com.supermarket.exception.ProductNotFoundException;
import com.supermarket.util.ValidationUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductService {

    // Danh sach san pham duoc luu trong ArrayList
    private final ArrayList<Product> products;

    // Constructor
    public ProductService() {
        products = new ArrayList<>();
    }

    // 1. THEM SAN PHAM
    public void addProduct(Product product) {

        if (product == null) {
            throw new InvalidDataException(
                    "San pham khong duoc null!");
        }

        // Kiem tra ma san pham trung
        if (findById(product.getId()).isPresent()) {
            throw new InvalidDataException(
                    "Ma san pham da ton tai: "
                            + product.getId());
        }

        products.add(product);
    }

    // 2. LAY DANH SACH SAN PHAM
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    // 3. TIM SAN PHAM THEO MA
    public Optional<Product> findById(String id) {

        if (id == null || id.isBlank()) {
            return Optional.empty();
        }

        String productId = id.trim();

        return products.stream()
                .filter(p -> p.getId()
                        .equalsIgnoreCase(productId))
                .findFirst();
    }

    // 4. TIM SAN PHAM - NEU KHONG CO THI BAO LOI
    public Product getProductOrThrow(String id) {

        return findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Khong tim thay san pham: "
                                        + id));
    }

    // 5. CAP NHAT SAN PHAM
    public void updateProduct(String id,
                              String newName,
                              double newPrice,
                              int newQuantity) {

        // Kiem tra san pham co ton tai khong
        Product product = getProductOrThrow(id);

        // Kiem tra tat ca du lieu truoc khi cap nhat
        ValidationUtils.requireText(
                newName, "Ten san pham");

        ValidationUtils.requirePositivePrice(
                newPrice);

        ValidationUtils.requireNonNegativeQuantity(
                newQuantity);

        // Cap nhat thong tin
        product.setName(newName);
        product.setPrice(newPrice);
        product.setQuantity(newQuantity);
    }

    // 6. XOA SAN PHAM
    public boolean deleteProduct(String id) {

        if (id == null || id.isBlank()) {
            return false;
        }

        String productId = id.trim();

        return products.removeIf(
                p -> p.getId()
                        .equalsIgnoreCase(productId));
    }

    // 7. TIM KIEM THEO TEN HOAC MA
    public List<Product> searchProducts(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return getAllProducts();
        }

        String key = keyword.trim()
                .toLowerCase(Locale.ROOT);

        return products.stream()
                .filter(p ->
                        p.getId()
                                .toLowerCase(Locale.ROOT)
                                .contains(key)
                                ||
                                p.getName()
                                        .toLowerCase(Locale.ROOT)
                                        .contains(key))
                .collect(Collectors.toList());
    }

    // 8. SAP XEP THEO GIA TANG DAN
    public List<Product> sortByPriceAscending() {

        return products.stream()
                .sorted(Comparator.comparingDouble(
                        p -> p.getPrice()))
                .collect(Collectors.toList());
    }

    // 9. SAP XEP THEO GIA GIAM DAN
    public List<Product> sortByPriceDescending() {

        return products.stream()
                .sorted(Comparator.comparingDouble(
                        Product::getPrice).reversed())
                .collect(Collectors.toList());
    }

    // 10. SAP XEP THEO TEN A-Z
    public List<Product> sortByName() {

        return products.stream()
                .sorted(Comparator.comparing(
                        Product::getName,
                        String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    // 11. DEM TONG SO SAN PHAM
    public int countProducts() {
        return products.size();
    }

    // 12. TINH TONG GIA TRI HANG TON KHO
    public double calculateInventoryValue() {

        return products.stream()
                .mapToDouble(p ->
                        p.getPrice() * p.getQuantity())
                .sum();
    }

    // 13. LAY DANH SACH SAN PHAM SAP HET HANG
    public List<Product> getLowStockProducts(int threshold) {

        ValidationUtils.requireNonNegativeQuantity(
                threshold);

        return products.stream()
                .filter(p -> p.getQuantity() <= threshold)
                .collect(Collectors.toList());
    }

    // 14. HIEN THI DANH SACH SAN PHAM
    public void displayProducts() {

        if (products.isEmpty()) {
            System.out.println(
                    "Danh sach san pham dang rong!");
            return;
        }

        products.forEach(System.out::println);
    }
}
