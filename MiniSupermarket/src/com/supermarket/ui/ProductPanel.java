
package com.supermarket.ui;

import com.supermarket.model.Product;
import com.supermarket.service.ProductService;
import com.supermarket.storage.FileStorage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class ProductPanel extends JPanel {

    private final ProductService service;
    private final FileStorage storage;
    private final Runnable onDataChanged;

    private final JTextField txtId = new JTextField();
    private final JTextField txtName = new JTextField();
    private final JTextField txtPrice = new JTextField();
    private final JTextField txtQuantity = new JTextField();
    private final JTextField txtSearch = new JTextField(18);

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new String[]{
                            "Ma SP", "Ten san pham",
                            "Gia", "So luong"
                    }, 0) {
                @Override
                public boolean isCellEditable(
                        int row, int column) {
                    return false;
                }

                @Override
                public Class<?> getColumnClass(int column) {
                    if (column == 2) {
                        return Double.class;
                    }
                    if (column == 3) {
                        return Integer.class;
                    }
                    return String.class;
                }
            };

    private final JTable table = new JTable(tableModel);

    public ProductPanel(ProductService service,
                        FileStorage storage,
                        Runnable onDataChanged) {

        this.service = service;
        this.storage = storage;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        createForm();
        createTable();
        createButtons();

        refreshTable();
    }

    // 1. TAO FORM NHAP SAN PHAM
    private void createForm() {

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));

        form.add(new JLabel("Ma san pham:"));
        form.add(txtId);

        form.add(new JLabel("Ten san pham:"));
        form.add(txtName);

        form.add(new JLabel("Gia ban:"));
        form.add(txtPrice);

        form.add(new JLabel("So luong:"));
        form.add(txtQuantity);

        JPanel top = new JPanel(new BorderLayout(8, 8));

        JLabel title = new JLabel("QUAN LY SAN PHAM");
        title.setFont(
                new java.awt.Font(
                        "SansSerif",
                        java.awt.Font.BOLD, 20));

        JPanel searchPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(new JLabel("Tim kiem:"));
        searchPanel.add(txtSearch);

        JButton btnSearch = new JButton("Tim");
        JButton btnReset = new JButton("Hien tat ca");

        searchPanel.add(btnSearch);
        searchPanel.add(btnReset);

        top.add(title, BorderLayout.NORTH);
        top.add(form, BorderLayout.CENTER);
        top.add(searchPanel, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        btnSearch.addActionListener(e -> searchProducts());
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            refreshTable();
        });

        txtSearch.addActionListener(e -> searchProducts());
    }

    // 2. TAO BANG DANH SACH
    private void createTable() {

        table.setRowHeight(28);
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        table.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()
                    && table.getSelectedRow() != -1) {

                int viewRow = table.getSelectedRow();

                int modelRow = table.convertRowIndexToModel(
                        viewRow);

                txtId.setText(
                        tableModel.getValueAt(
                                modelRow, 0).toString());

                txtName.setText(
                        tableModel.getValueAt(
                                modelRow, 1).toString());

                txtPrice.setText(
                        tableModel.getValueAt(
                                modelRow, 2).toString());

                txtQuantity.setText(
                        tableModel.getValueAt(
                                modelRow, 3).toString());
            }
        });
    }

    // 3. TAO CAC NUT CHUC NANG
    private void createButtons() {

        JPanel buttons =
                new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnAdd = new JButton("Them");
        JButton btnUpdate = new JButton("Sua");
        JButton btnDelete = new JButton("Xoa");
        JButton btnClear = new JButton("Lam moi");

        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        add(buttons, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearForm());
    }

    // 4. THEM SAN PHAM
    private void addProduct() {

        try {
            Product p = new Product(
                    txtId.getText(),
                    txtName.getText(),
                    Double.parseDouble(txtPrice.getText().trim()),
                    Integer.parseInt(txtQuantity.getText().trim()));

            service.addProduct(p);

            saveAndRefresh();

            JOptionPane.showMessageDialog(
                    this, "Them san pham thanh cong!");

            clearForm();

        } catch (NumberFormatException e) {
            showError("Gia va so luong phai la so hop le!");

        } catch (IOException e) {
            showSaveError(e);

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    // 5. CAP NHAT SAN PHAM
    private void updateProduct() {

        try {
            String id = txtId.getText();

            String name = txtName.getText();

            double price =
                    Double.parseDouble(txtPrice.getText().trim());

            int quantity =
                    Integer.parseInt(txtQuantity.getText().trim());

            service.updateProduct(
                    id, name, price, quantity);

            saveAndRefresh();

            JOptionPane.showMessageDialog(
                    this, "Cap nhat thanh cong!");

            clearForm();

        } catch (NumberFormatException e) {
            showError("Gia va so luong phai la so hop le!");

        } catch (IOException e) {
            showSaveError(e);

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    // 6. XOA SAN PHAM
    private void deleteProduct() {

        String id = txtId.getText().trim();

        if (id.isEmpty()) {
            showError("Vui long chon san pham can xoa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Ban co chac muon xoa " + id + "?",
                "Xac nhan xoa",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean deleted = service.deleteProduct(id);

            if (!deleted) {
                showError("Khong tim thay san pham!");
                return;
            }

            saveAndRefresh();

            JOptionPane.showMessageDialog(
                    this, "Xoa san pham thanh cong!");

            clearForm();

        } catch (IOException e) {
            showSaveError(e);
        }
    }

    // 7. TIM KIEM SAN PHAM
    private void searchProducts() {

        List<Product> results =
                service.searchProducts(txtSearch.getText());

        displayProducts(results);
    }

    // 8. HIEN THI DU LIEU LEN JTABLE
    private void displayProducts(List<Product> products) {

        tableModel.setRowCount(0);

        for (Product p : products) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    p.getQuantity()
            });
        }
    }

    // 9. TAI LAI DANH SACH
    public void refreshTable() {
        displayProducts(service.getAllProducts());
    }

    // 10. LUU FILE VA CAP NHAT GIAO DIEN
    private void saveAndRefresh() throws IOException {

        storage.saveProducts(service.getAllProducts());

        refreshTable();

        if (onDataChanged != null) {
            onDataChanged.run();
        }
    }

    // 11. XOA NOI DUNG FORM
    private void clearForm() {

        txtId.setText("");
        txtName.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");

        table.clearSelection();

        txtId.requestFocusInWindow();
    }

    // 12. HIEN THI LOI
    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Loi",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showSaveError(IOException e) {

        JOptionPane.showMessageDialog(
                this,
                "Khong the luu du lieu: " + e.getMessage()
                        + "\nDu lieu RAM co the khac du lieu trong file."
                        + "\nUng dung se dong de tranh thao tac tiep.",
                "Loi luu file",
                JOptionPane.ERROR_MESSAGE);

        java.awt.Window window =
                SwingUtilities.getWindowAncestor(this);

        if (window != null) {
            window.dispose();
        }
    }
}



