
package com.supermarket.ui;

import com.supermarket.model.Invoice;
import com.supermarket.model.InvoiceItem;
import com.supermarket.service.SalesService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class InvoicePanel extends JPanel {

    // ==================================
    // 1. SERVICE
    // ==================================

    private final SalesService salesService;

    // ==================================
    // 2. CAC THANH PHAN GIAO DIEN
    // ==================================

    private final JTextField txtSearch =
            new JTextField(18);

    private final JLabel lblTotalInvoices =
            new JLabel("0");

    private final JLabel lblTotalRevenue =
            new JLabel("0 VND");

    private final JLabel lblSelectedInvoice =
            new JLabel("Chua chon hoa don");

    // ==================================
    // 3. BANG DANH SACH HOA DON
    // ==================================

    private final DefaultTableModel invoiceModel =
            new DefaultTableModel(
                    new String[]{
                            "Ma hoa don",
                            "Ngay lap",
                            "Tong tien"
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
                    return String.class;
                }
            };

    private final JTable invoiceTable =
            new JTable(invoiceModel);

    // ==================================
    // 4. BANG CHI TIET HOA DON
    // ==================================

    private final DefaultTableModel detailModel =
            new DefaultTableModel(
                    new String[]{
                            "Ma san pham",
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

    private final JTable detailTable =
            new JTable(detailModel);

    // ==================================
    // 5. DINH DANG NGAY GIO
    // ==================================

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm:ss");

    // ==================================
    // 6. CONSTRUCTOR
    // ==================================

    public InvoicePanel(SalesService salesService) {

        if (salesService == null) {
            throw new IllegalArgumentException(
                    "SalesService khong duoc null!");
        }

        this.salesService = salesService;

        setLayout(new BorderLayout(10, 10));

        setBackground(new Color(245, 247, 250));

        setBorder(new EmptyBorder(
                15, 15, 15, 15));

        createHeader();
        createTables();

        refreshData();
    }

    // ==================================
    // 7. TAO HEADER
    // ==================================

    private void createHeader() {

        JPanel header =
                new JPanel(new BorderLayout(10, 10));

        JLabel title =
                new JLabel("QUAN LY HOA DON");

        title.setFont(
                new Font("SansSerif", Font.BOLD, 22));

        // Thong ke
        JPanel statistics =
                new JPanel(new GridLayout(1, 2, 15, 15));

        statistics.add(
                createStatCard(
                        "TONG SO HOA DON",
                        lblTotalInvoices));

        statistics.add(
                createStatCard(
                        "TONG DOANH THU",
                        lblTotalRevenue));

        // Tim kiem
        JPanel searchPanel =
                new JPanel(new FlowLayout(
                        FlowLayout.LEFT));

        JButton btnSearch =
                new JButton("Tim kiem");

        JButton btnShowAll =
                new JButton("Hien tat ca");

        JButton btnRefresh =
                new JButton("Lam moi");

        searchPanel.add(
                new JLabel("Ma hoa don:"));

        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnShowAll);
        searchPanel.add(btnRefresh);

        JPanel middle =
                new JPanel(new BorderLayout(5, 5));

        middle.add(
                statistics, BorderLayout.CENTER);

        middle.add(
                searchPanel, BorderLayout.SOUTH);

        header.add(title, BorderLayout.NORTH);
        header.add(middle, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // SU KIEN
        btnSearch.addActionListener(
                e -> searchInvoices());

        txtSearch.addActionListener(
                e -> searchInvoices());

        btnShowAll.addActionListener(e -> {
            txtSearch.setText("");
            refreshData();
        });

        btnRefresh.addActionListener(
                e -> refreshData());
    }

    // ==================================
    // 8. TAO THE THONG KE
    // ==================================

    private JPanel createStatCard(
            String title, JLabel valueLabel) {

        JPanel card =
                new JPanel(new BorderLayout(5, 5));

        card.setBackground(Color.WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 235)),
                        new EmptyBorder(
                                15, 20, 15, 20)));

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 14));

        valueLabel.setFont(
                new Font("SansSerif", Font.BOLD, 24));

        valueLabel.setForeground(
                new Color(35, 105, 175));

        card.add(
                titleLabel, BorderLayout.NORTH);

        card.add(
                valueLabel, BorderLayout.CENTER);

        return card;
    }

    // ==================================
    // 9. TAO BANG HOA DON
    // ==================================

    private void createTables() {

        invoiceTable.setRowHeight(28);

        invoiceTable.setAutoCreateRowSorter(true);

        invoiceTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        detailTable.setRowHeight(28);

        // Bang danh sach
        JPanel listPanel =
                new JPanel(new BorderLayout(5, 5));

        JLabel listTitle =
                new JLabel("DANH SACH HOA DON");

        listTitle.setFont(
                new Font("SansSerif", Font.BOLD, 15));

        listPanel.add(
                listTitle, BorderLayout.NORTH);

        listPanel.add(
                new JScrollPane(invoiceTable),
                BorderLayout.CENTER);

        // Bang chi tiet
        JPanel detailsPanel =
                new JPanel(new BorderLayout(5, 5));

        lblSelectedInvoice.setFont(
                new Font("SansSerif", Font.BOLD, 15));

        detailsPanel.add(
                lblSelectedInvoice,
                BorderLayout.NORTH);

        detailsPanel.add(
                new JScrollPane(detailTable),
                BorderLayout.CENTER);

        // Chia hai bang
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                listPanel,
                detailsPanel);

        splitPane.setResizeWeight(0.5);

        splitPane.setDividerLocation(260);

        add(splitPane, BorderLayout.CENTER);

        // Su kien chon hoa don
        invoiceTable.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        showSelectedInvoice();
                    }
                });
    }

    // ==================================
    // 10. CAP NHAT THONG KE VA DANH SACH
    // ==================================

    public void refreshData() {

        int totalInvoices =
                salesService.countInvoices();

        double totalRevenue =
                salesService.calculateTotalRevenue();

        lblTotalInvoices.setText(
                String.valueOf(totalInvoices));

        lblTotalRevenue.setText(
                String.format(
                        "%,.0f VND", totalRevenue));

        displayInvoices(
                salesService.getInvoices());
    }

    // ==================================
    // 11. HIEN THI DANH SACH HOA DON
    // ==================================

    private void displayInvoices(
            List<Invoice> invoices) {

        // Bo chon dong truoc khi cap nhat
        invoiceTable.clearSelection();

        invoiceModel.setRowCount(0);
        detailModel.setRowCount(0);

        lblSelectedInvoice.setText(
                "Chua chon hoa don");

        for (Invoice invoice : invoices) {

            invoiceModel.addRow(new Object[]{
                    invoice.getInvoiceId(),
                    invoice.getCreatedAt()
                            .format(dateFormatter),
                    invoice.calculateTotal()
            });
        }
    }

    // ==================================
    // 12. TIM KIEM HOA DON
    // ==================================

    private void searchInvoices() {

        String keyword = txtSearch.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        List<Invoice> results =
                salesService.getInvoices()
                        .stream()
                        .filter(invoice ->
                                invoice.getInvoiceId()
                                        .toLowerCase(Locale.ROOT)
                                        .contains(keyword))
                        .collect(Collectors.toList());

        displayInvoices(results);
    }

    // ==================================
    // 13. XEM CHI TIET HOA DON
    // ==================================

    private void showSelectedInvoice() {

        int viewRow =
                invoiceTable.getSelectedRow();

        detailModel.setRowCount(0);

        if (viewRow < 0) {
            lblSelectedInvoice.setText(
                    "Chua chon hoa don");
            return;
        }

        // Chuyen vi tri View sang Model
        int modelRow =
                invoiceTable.convertRowIndexToModel(
                        viewRow);

        String invoiceId =
                invoiceModel.getValueAt(
                        modelRow, 0).toString();

        try {

            Invoice invoice =
                    salesService.findInvoiceById(
                            invoiceId);

            lblSelectedInvoice.setText(
                    "CHI TIET HOA DON: "
                            + invoice.getInvoiceId()
                            + " | Tong tien: "
                            + String.format(
                            "%,.0f VND",
                            invoice.calculateTotal()));

            for (InvoiceItem item :
                    invoice.getItems()) {

                detailModel.addRow(new Object[]{
                        item.getProductId(),
                        item.getProductName(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getSubtotal()
                });
            }

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Loi hoa don",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
