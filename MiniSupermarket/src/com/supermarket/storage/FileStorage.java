
package com.supermarket.storage;

import com.supermarket.model.Product;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileStorage {

    private final Path filePath;

    public FileStorage(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "Duong dan file khong hop le!"
            );
        }

        this.filePath = Path.of(fileName).toAbsolutePath();
    }

    // 1. LUU DANH SACH SAN PHAM
    public void saveProducts(List<Product> products)
            throws IOException {

        if (products == null) {
            throw new IllegalArgumentException(
                    "Danh sach khong duoc null!"
            );
        }

        Path folder = filePath.getParent();
        Files.createDirectories(folder);

        Path tempFile = Files.createTempFile(
                folder, "products-", ".tmp"
        );

        try {
            try (ObjectOutputStream output =
                         new ObjectOutputStream(
                                 Files.newOutputStream(tempFile))) {

                output.writeObject(
                        new ArrayList<>(products)
                );
            }

            try {
                Files.move(
                        tempFile,
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(
                        tempFile,
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    // 2. DOC DANH SACH SAN PHAM
    public List<Product> loadProducts()
            throws IOException, ClassNotFoundException {

        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(
                             Files.newInputStream(filePath))) {

            Object data = input.readObject();

            if (!(data instanceof List<?>)) {
                throw new IOException(
                        "File khong chua danh sach san pham!"
                );
            }

            List<Product> result = new ArrayList<>();
            Set<String> ids = new HashSet<>();

            for (Object item : (List<?>) data) {

                if (!(item instanceof Product)) {
                    throw new IOException(
                            "Du lieu trong file khong hop le!"
                    );
                }

                Product p = (Product) item;

                try {
                    // Tao lai de kiem tra validation
                    Product valid = new Product(
                            p.getId(),
                            p.getName(),
                            p.getPrice(),
                            p.getQuantity()
                    );

                    String id = valid.getId()
                            .toLowerCase(java.util.Locale.ROOT);

                    if (!ids.add(id)) {
                        throw new IOException(
                                "Trung ma san pham trong file!"
                        );
                    }

                    result.add(valid);

                } catch (IllegalArgumentException e) {
                    throw new IOException(
                            "Du lieu san pham bi loi!", e
                    );
                }
            }

            return result;
        }
    }

    // 3. LAY DUONG DAN FILE
    public Path getFilePath() {
        return filePath;
    }
}
