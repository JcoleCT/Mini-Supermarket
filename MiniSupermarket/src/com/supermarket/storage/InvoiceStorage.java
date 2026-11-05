
package com.supermarket.storage;

import com.supermarket.model.Invoice;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceStorage {

    private final Path filePath =
            Path.of("data/invoices.dat").toAbsolutePath();

    public void saveInvoices(List<Invoice> invoices)
            throws IOException {

        Files.createDirectories(filePath.getParent());

        Path temp = Files.createTempFile(
                filePath.getParent(), "invoices-", ".tmp");

        try {
            try (ObjectOutputStream output =
                         new ObjectOutputStream(
                                 Files.newOutputStream(temp))) {

                output.writeObject(
                        new ArrayList<>(invoices));
            }

            try {
                Files.move(
                        temp,
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(
                        temp,
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    public List<Invoice> loadInvoices()
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
                        "File hoa don khong hop le!");
            }

            List<Invoice> result = new ArrayList<>();

            for (Object obj : (List<?>) data) {
                if (!(obj instanceof Invoice)) {
                    throw new IOException(
                            "Du lieu hoa don bi loi!");
                }
                result.add((Invoice) obj);
            }

            return result;
        }
    }
}
