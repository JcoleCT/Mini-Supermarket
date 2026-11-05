
package com.supermarket.ui;

import com.supermarket.model.Invoice;
import com.supermarket.model.InvoiceItem;
import com.supermarket.model.Product;
import com.supermarket.service.ProductService;
import com.supermarket.service.SalesService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class SalesPanel extends JPanel {

    // Service xu ly nghiep vu
    private final ProductService productService;
    private final SalesService salesService;
    private final Runnable onDataChanged;

    // Tim kiem va chon san pham
    private final JTextField txtSearch =
            new JTextField(22);

    private final JTextField txtQuantity =
            new JTextField("1", 6);

    private final JLabel lblSelected =
            new JLabel("Chua chon san pham");

    private final JLabel lblResults =
            new JLabel("0 san pham");

    // Tong tien gio hang
    private final JLabel lblTotal =
            new JLabel("0 VND");

    // Bang danh sach san pham
    private final DefaultTableModel productModel =
            new DefaultTableModel(
                    new String[]{
                            "Ma SP",
                            "Ten san pham",
                            "Don gia",
                            "Ton kho"
                    }, 0) {

                @Override
                public boolean isCellEditable(
                        int row, int column) {
                    return false;
                }

                @Override
                public Class<?> getColumnClass(
                        int column) {

                    if (column == 2) {
                        return Double.class;
                    }

                    if (column == 3) {
                        return Integer.class;
                    }

                    return String.class;
                }
            };

    private final JTable productTable =
            new JTable(productModel);

    // Bang gio hang
    private final DefaultTableModel cartModel =
            new DefaultTableModel(
                    new String[]{
                            "Ma SP",
                            "Ten san pham",
                            "Don gia",
                            "So luong",
                            "Thanh tien"
                    }, 0) {

                @Override
                public boolean isCellEditable(
                        int row, int column) {
                    return false;
                }
            };

    private final JTable cartTable =
            new JTable(cartModel);

    // CONSTRUCTOR
    public SalesPanel(
            ProductService productService,
            SalesService salesService,
            Runnable onDataChanged) {

        if (productService == null
                || salesService == null) {

            throw new IllegalArgumentException(
                    "Service khong duoc null!");
        }

        this.productService = productService;
        this.salesService = salesService;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(12, 12));

        setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15));

        createScreen();
        setupEvents();

        refreshProducts();
        refreshCart();
    }

    // 1. TAO GIAO DIEN BAN HANG
    private void createScreen() {

        JLabel title = new JLabel(
                "BAN HANG - TIM KIEM SAN PHAM");

        title.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        21));

        add(title, BorderLayout.NORTH);

        // Khu vuc tim kiem san pham
        JPanel productSection =
                new JPanel(new BorderLayout(8, 8));

        productSection.setBorder(
                BorderFactory.createTitledBorder(
                        "1. Tim va chon san pham"));

        JPanel searchRow =
                new JPanel(new FlowLayout(
                        FlowLayout.LEFT, 8, 4));

        searchRow.add(
                new JLabel("Ma / ten san pham:"));

        searchRow.add(txtSearch);

        JButton btnClearSearch =
                new JButton("Xoa tim kiem");

        searchRow.add(btnClearSearch);
        searchRow.add(lblResults);

        productSection.add(
                searchRow, BorderLayout.NORTH);

        // Bang ket qua tim kiem
        productTable.setRowHeight(29);

        productTable.setAutoCreateRowSorter(true);

        productTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        productTable.setFillsViewportHeight(true);

        productSection.add(
                new JScrollPane(productTable),
                BorderLayout.CENTER);

        // Khu vuc nhap so luong
        JPanel selectionRow =
                new JPanel(new FlowLayout(
                        FlowLayout.LEFT, 10, 5));

        lblSelected.setPreferredSize(
                new Dimension(230, 26));

        selectionRow.add(lblSelected);

        selectionRow.add(new JLabel("So luong:"));
        selectionRow.add(txtQuantity);

        JButton btnAdd =
                new JButton("Them vao gio");

        selectionRow.add(btnAdd);

        productSection.add(
                selectionRow, BorderLayout.SOUTH);

        // Khu vuc gio hang
        JPanel cartSection =
                new JPanel(new BorderLayout(8, 8));

        cartSection.setBorder(
                BorderFactory.createTitledBorder(
                        "2. Gio hang va thanh toan"));

        cartTable.setRowHeight(29);

        cartTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        cartTable.setFillsViewportHeight(true);

        cartSection.add(
                new JScrollPane(cartTable),
                BorderLayout.CENTER);

        // Tong tien
        JPanel cartFooter =
                new JPanel(new GridLayout(2, 1, 3, 3));

        JPanel totalRow =
                new JPanel(new FlowLayout(
                        FlowLayout.RIGHT, 8, 2));

        lblTotal.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        20));

        lblTotal.setForeground(
                new Color(25, 100, 65));

        totalRow.add(new JLabel("TONG TIEN:"));
        totalRow.add(lblTotal);

        // Cac nut gio hang
        JPanel actions =
                new JPanel(new FlowLayout(
                        FlowLayout.RIGHT, 8, 2));

        JButton btnRemove =
                new JButton("Xoa khoi gio");

        JButton btnClearCart =
                new JButton("Huy gio hang");

        JButton btnCheckout =
                new JButton("Thanh toan");

        actions.add(btnRemove);
        actions.add(btnClearCart);
        actions.add(btnCheckout);

        cartFooter.add(totalRow);
        cartFooter.add(actions);

        cartSection.add(
                cartFooter, BorderLayout.SOUTH);

        // Chia giao dien thanh hai khu vuc
        JSplitPane split =
                new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT,
                        productSection,
                        cartSection);

        split.setResizeWeight(0.52);
        split.setDividerSize(8);

        add(split, BorderLayout.CENTER);

        // Bat su kien cac nut
        btnClearSearch.addActionListener(
                e -> txtSearch.setText(""));

        btnAdd.addActionListener(
                e -> addToCart());

        btnRemove.addActionListener(
                e -> removeFromCart());

        btnClearCart.addActionListener(e -> {

            salesService.clearCart();
            refreshCart();
        });

        btnCheckout.addActionListener(
                e -> checkout());
    }

    // 2. BAT SU KIEN TIM KIEM VA CHON SAN PHAM
    private void setupEvents() {

        txtSearch.getDocument().addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        filterProducts();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        filterProducts();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        filterProducts();
                    }
                });

        productTable.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {

                        String id = getSelectedProductId();

                        if (id == null) {
                            lblSelected.setText(
                                    "Chua chon san pham");
                        } else {
                            lblSelected.setText(
                                    "Da chon: " + id);
                        }
                    }
                });
    }

    // 3. LAM MOI DANH SACH SAN PHAM
    public void refreshProducts() {
        filterProducts();
    }

    // 4. TIM KIEM SAN PHAM THEO MA HOAC TEN
    private void filterProducts() {

        String selectedId = getSelectedProductId();

        String keyword = txtSearch.getText();

        List<Product> products =
                productService.searchProducts(keyword);

        productTable.clearSelection();
        productModel.setRowCount(0);

        for (Product product : products) {

            productModel.addRow(new Object[]{
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getQuantity()
            });
        }

        lblResults.setText(
                products.size() + " san pham");

        lblSelected.setText(
                "Chua chon san pham");

        // Khoi phuc lua chon neu san pham van con
        if (selectedId != null) {

            for (int i = 0;
                 i < productModel.getRowCount();
                 i++) {

                String id = productModel
                        .getValueAt(i, 0).toString();

                if (id.equalsIgnoreCase(selectedId)) {

                    int viewRow =
                            productTable
                                    .convertRowIndexToView(i);

                    if (viewRow >= 0) {
                        productTable.setRowSelectionInterval(
                                viewRow, viewRow);
                    }

                    break;
                }
            }
        }
    }

    // 5. LAY MA SAN PHAM DUOC CHON
    private String getSelectedProductId() {

        int viewRow = productTable.getSelectedRow();

        if (viewRow < 0) {
            return null;
        }

        int modelRow =
                productTable.convertRowIndexToModel(
                        viewRow);

        return productModel.getValueAt(
                modelRow, 0).toString();
    }

    // 6. THEM SAN PHAM VAO GIO HANG
    private void addToCart() {

        try {

            String productId = getSelectedProductId();

            if (productId == null) {

                showError(
                        "Vui long chon san pham!");

                return;
            }

            int quantity = Integer.parseInt(
                    txtQuantity.getText().trim());

            salesService.addToCart(
                    productId, quantity);

            refreshCart();

            txtQuantity.setText("1");

        } catch (NumberFormatException e) {

            showError(
                    "So luong phai la so nguyen!");

        } catch (RuntimeException e) {

            showError(e.getMessage());
        }
    }

    // 7. HIEN THI GIO HANG
    private void refreshCart() {

        cartModel.setRowCount(0);

        try {

            for (InvoiceItem item :
                    salesService.getCartItems()) {

                cartModel.addRow(new Object[]{
                        item.getProductId(),
                        item.getProductName(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getSubtotal()
                });
            }

            double total =
                    salesService.calculateTotal();

            lblTotal.setText(
                    String.format(
                            "%,.0f VND", total));

        } catch (RuntimeException e) {

            lblTotal.setText(
                    "Can kiem tra gio hang");

            showError(
                    "Gio hang khong hop le: "
                            + e.getMessage());
        }
    }

    // 8. XOA SAN PHAM KHOI GIO
    private void removeFromCart() {

        int viewRow = cartTable.getSelectedRow();

        if (viewRow < 0) {

            showError(
                    "Vui long chon san pham trong gio!");

            return;
        }

        int modelRow =
                cartTable.convertRowIndexToModel(
                        viewRow);

        String productId =
                cartModel.getValueAt(
                        modelRow, 0).toString();

        try {

            salesService.removeFromCart(productId);

            refreshCart();

        } catch (RuntimeException e) {

            showError(e.getMessage());
        }
    }

    // 9. THANH TOAN
    private void checkout() {

        try {

            if (salesService.isCartEmpty()) {

                showError("Gio hang dang rong!");

                return;
            }

            double total =
                    salesService.calculateTotal();

            int confirm =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Tong thanh toan: "
                                    + String.format(
                                    "%,.0f VND", total)
                                    + "\nXac nhan thanh toan?",
                            "Xac nhan",
                            JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            Invoice invoice =
                    salesService.checkout();

            JOptionPane.showMessageDialog(
                    this,
                    "Thanh toan thanh cong!\n"
                            + "Ma hoa don: "
                            + invoice.getInvoiceId()
                            + "\nTong tien: "
                            + String.format(
                            "%,.0f VND",
                            invoice.calculateTotal()));

            refreshCart();
            refreshProducts();

            if (onDataChanged != null) {
                onDataChanged.run();
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Loi luu thanh toan: "
                            + e.getMessage()
                            + "\nCan kiem tra du lieu "
                            + "truoc khi tiep tuc.",
                    "Loi File I/O",
                    JOptionPane.ERROR_MESSAGE);

            java.awt.Window window =
                    SwingUtilities
                            .getWindowAncestor(this);

            if (window != null) {
                window.dispose();
            }

        } catch (RuntimeException e) {

            showError(e.getMessage());
        }
    }

    // 10. HIEN THI THONG BAO LOI
    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Loi",
                JOptionPane.ERROR_MESSAGE);
    }
}
