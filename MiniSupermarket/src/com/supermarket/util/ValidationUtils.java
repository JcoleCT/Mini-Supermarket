
package com.supermarket.util;

import com.supermarket.exception.InvalidDataException;

public final class ValidationUtils {

    private ValidationUtils() {
        // Khong cho tao doi tuong Utility
    }

    public static void requireText(
            String value, String fieldName) {

        if (value == null || value.isBlank()) {
            throw new InvalidDataException(
                    fieldName + " khong duoc de trong!");
        }
    }

    public static void requirePositivePrice(
            double price) {

        if (!Double.isFinite(price) || price <= 0) {
            throw new InvalidDataException(
                    "Gia san pham phai lon hon 0!");
        }
    }

    public static void requireNonNegativeQuantity(
            int quantity) {

        if (quantity < 0) {
            throw new InvalidDataException(
                    "So luong khong duoc am!");
        }
    }

    public static void requirePositiveQuantity(
            int quantity) {

        if (quantity <= 0) {
            throw new InvalidDataException(
                    "So luong mua phai lon hon 0!");
        }
    }
}
