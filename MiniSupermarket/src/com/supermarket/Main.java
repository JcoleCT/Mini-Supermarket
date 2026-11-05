
package com.supermarket;

import com.supermarket.model.Product;
import com.supermarket.model.Invoice;

import com.supermarket.service.ProductService;
import com.supermarket.service.SalesService;

import com.supermarket.storage.FileStorage;
import com.supermarket.storage.InvoiceStorage;

import com.supermarket.ui.DashboardFrame;

import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        // Su dung giao dien cua he dieu hanh
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());

        } catch (Exception e) {
            System.out.println(
                    "Khong the thiet lap Look And Feel.");
        }

        SwingUtilities.invokeLater(() -> {

            // ==================================
            // 1. KHOI TAO PRODUCT SERVICE
            // ==================================

            ProductService productService =
                    new ProductService();

            // ==================================
            // 2. KHOI TAO FILE STORAGE
            // ==================================

            FileStorage productStorage =
                    new FileStorage(
                            "data/products.dat");

            InvoiceStorage invoiceStorage =
                    new InvoiceStorage();

            try {

                // ==================================
                // 3. DOC SAN PHAM TU FILE
                // ==================================

                List<Product> products =
                        productStorage.loadProducts();

                for (Product product : products) {
                    productService.addProduct(product);
                }

                System.out.println(
                        "Da tai "
                                + productService.countProducts()
                                + " san pham.");

                // ==================================
                // 4. DOC HOA DON TU FILE
                // ==================================

                List<Invoice> invoices =
                        invoiceStorage.loadInvoices();

                System.out.println(
                        "Da tai "
                                + invoices.size()
                                + " hoa don.");

                // ==================================
                // 5. KHOI TAO SALES SERVICE
                // ==================================

                SalesService salesService =
                        new SalesService(
                                productService,
                                productStorage,
                                invoiceStorage,
                                invoices);

                // ==================================
                // 6. TAO DASHBOARD
                // ==================================

                DashboardFrame dashboard =
                        new DashboardFrame(
                                productService,
                                productStorage,
                                salesService);

                // ==================================
                // 7. HIEN THI GIAO DIEN
                // ==================================

                dashboard.setVisible(true);

                System.out.println(
                        "MiniSupermarket da khoi dong!");

            } catch (IOException
                     | ClassNotFoundException e) {

                JOptionPane.showMessageDialog(
                        null,
                        "Khong the doc du lieu:\n"
                                + e.getMessage(),
                        "Loi File I/O",
                        JOptionPane.ERROR_MESSAGE);

            } catch (RuntimeException e) {

                JOptionPane.showMessageDialog(
                        null,
                        "Loi khi khoi dong:\n"
                                + e.getMessage(),
                        "Loi he thong",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
