
package com.supermarket.ui;

import com.supermarket.model.Product;
import com.supermarket.service.ProductService;
import com.supermarket.service.SalesService;
import com.supermarket.storage.FileStorage;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DashboardFrame extends JFrame {

    // ==================================
    // 1. SERVICE VA STORAGE
    // ==================================

    private final ProductService productService;
    private final FileStorage productStorage;
    private final SalesService salesService;

    // ==================================
    // 2. CARD LAYOUT
    // ==================================

    private final CardLayout cardLayout =
            new CardLayout();

    private final JPanel contentPanel =
            new JPanel(cardLayout);

    // ==================================
    // 3. CAC MAN HINH
    // ==================================

    private ProductPanel productPanel;
    private SalesPanel salesPanel;
    private InvoicePanel invoicePanel;

    // ==================================
    // 4. CAC LABEL THONG KE
    // ==================================

    private final JLabel lblTotalProducts =
            new JLabel("0");

    private final JLabel lblTotalQuantity =
            new JLabel("0");

    private final JLabel lblInventoryValue =
            new JLabel("0 VND");

    private final JLabel lblTotalInvoices =
            new JLabel("0");

    private final JLabel lblTotalRevenue =
            new JLabel("0 VND");

    // ==================================
    // 5. MAU SAC
    // ==================================

    private static final Color PRIMARY =
            new Color(30, 58, 95);

    private static final Color BACKGROUND =
            new Color(245, 247, 250);

    private static final Color SIDEBAR =
            new Color(232, 238, 246);

    private static final Color TEXT =
            new Color(40, 55, 75);

    // ==================================
    // 6. CONSTRUCTOR
    // ==================================

    public DashboardFrame(
            ProductService productService,
            FileStorage productStorage,
            SalesService salesService) {

        if (productService == null
                || productStorage == null
                || salesService == null) {

            throw new IllegalArgumentException(
                    "Du lieu khoi tao Dashboard khong hop le!");
        }

        this.productService = productService;
        this.productStorage = productStorage;
        this.salesService = salesService;

        setTitle(
                "MiniSupermarket - Quan ly ban hang");

        setSize(1200, 720);

        setMinimumSize(
                new Dimension(950, 580));

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        createHeader();
        createSidebar();
        createContent();
        createFooter();

        refreshDashboard();
    }

    // ==================================
    // 7. TAO HEADER
    // ==================================

    private void createHeader() {

        JPanel header =
                new JPanel(new BorderLayout());

        header.setBackground(PRIMARY);

        header.setBorder(
                new EmptyBorder(20, 25, 20, 25));

        JLabel title =
                new JLabel("MINI SUPERMARKET");

        title.setForeground(Color.WHITE);

        title.setFont(
                new Font("SansSerif", Font.BOLD, 24));

        JLabel subtitle =
                new JLabel("HE THONG QUAN LY BAN HANG");

        subtitle.setForeground(
                new Color(220, 230, 245));

        subtitle.setFont(
                new Font("SansSerif", Font.PLAIN, 13));

        header.add(
                title, BorderLayout.WEST);

        header.add(
                subtitle, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    // ==================================
    // 8. TAO MENU BEN TRAI
    // ==================================

    private void createSidebar() {

        JPanel sidebar = new JPanel();

        sidebar.setLayout(
                new BoxLayout(
                        sidebar, BoxLayout.Y_AXIS));

        sidebar.setBackground(SIDEBAR);

        sidebar.setPreferredSize(
                new Dimension(225, 0));

        sidebar.setBorder(
                new EmptyBorder(25, 12, 25, 12));

        // Tao cac nut
        JButton btnDashboard =
                createMenuButton("Tong quan");

        JButton btnProducts =
                createMenuButton("Quan ly san pham");

        JButton btnSales =
                createMenuButton("Ban hang");

        JButton btnInvoices =
                createMenuButton("Quan ly hoa don");

        // Them vao sidebar
        sidebar.add(btnDashboard);
        sidebar.add(Box.createVerticalStrut(12));

        sidebar.add(btnProducts);
        sidebar.add(Box.createVerticalStrut(12));

        sidebar.add(btnSales);
        sidebar.add(Box.createVerticalStrut(12));

        sidebar.add(btnInvoices);
        sidebar.add(Box.createVerticalGlue());

        // ==================================
        // SU KIEN CHUYEN MAN HINH
        // ==================================

        btnDashboard.addActionListener(e -> {

            refreshDashboard();

            cardLayout.show(
                    contentPanel, "dashboard");
        });

        btnProducts.addActionListener(e -> {

            productPanel.refreshTable();

            cardLayout.show(
                    contentPanel, "products");
        });

        btnSales.addActionListener(e -> {

            salesPanel.refreshProducts();

            cardLayout.show(
                    contentPanel, "sales");
        });

        btnInvoices.addActionListener(e -> {

            invoicePanel.refreshData();

            cardLayout.show(
                    contentPanel, "invoices");
        });

        add(sidebar, BorderLayout.WEST);
    }

    // ==================================
    // 9. TAO NUT MENU
    // ==================================

    private JButton createMenuButton(String title) {

        JButton button = new JButton(title);

        button.setFont(
                new Font("SansSerif", Font.BOLD, 14));

        button.setForeground(TEXT);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);

        button.setHorizontalAlignment(
                SwingConstants.LEFT);

        button.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE, 48));

        button.setPreferredSize(
                new Dimension(200, 48));

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(210, 220, 232)),
                        new EmptyBorder(10, 15, 10, 10)));

        return button;
    }

    // ==================================
    // 10. TAO CAC MAN HINH
    // ==================================

    private void createContent() {

        // Dashboard
        JPanel dashboardPanel =
                createDashboardPanel();

        // Quan ly san pham
        productPanel = new ProductPanel(
                productService,
                productStorage,
                this::refreshDashboard);

        // Ban hang
        salesPanel = new SalesPanel(
                productService,
                salesService,
                this::refreshAfterSale);

        // Quan ly hoa don
        invoicePanel =
                new InvoicePanel(salesService);

        // Them cac man hinh
        contentPanel.add(
                dashboardPanel, "dashboard");

        contentPanel.add(
                productPanel, "products");

        contentPanel.add(
                salesPanel, "sales");

        contentPanel.add(
                invoicePanel, "invoices");

        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(
                contentPanel, "dashboard");
    }

    // ==================================
    // 11. TAO DASHBOARD
    // ==================================

    private JPanel createDashboardPanel() {

        JPanel mainPanel =
                new JPanel(new BorderLayout(15, 15));

        mainPanel.setBackground(BACKGROUND);

        mainPanel.setBorder(
                new EmptyBorder(30, 30, 30, 30));

        JLabel title =
                new JLabel("TONG QUAN HE THONG");

        title.setFont(
                new Font("SansSerif", Font.BOLD, 22));

        title.setForeground(TEXT);

        mainPanel.add(
                title, BorderLayout.NORTH);

        JPanel statsPanel =
                new JPanel(new GridLayout(
                        2, 3, 18, 18));

        statsPanel.setBackground(BACKGROUND);

        statsPanel.add(
                createStatCard(
                        "SO LOAI SAN PHAM",
                        lblTotalProducts));

        statsPanel.add(
                createStatCard(
                        "TONG SO LUONG TON",
                        lblTotalQuantity));

        statsPanel.add(
                createStatCard(
                        "GIA TRI HANG TON",
                        lblInventoryValue));

        statsPanel.add(
                createStatCard(
                        "TONG SO HOA DON",
                        lblTotalInvoices));

        statsPanel.add(
                createStatCard(
                        "TONG DOANH THU",
                        lblTotalRevenue));

        mainPanel.add(
                statsPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    // ==================================
    // 12. TAO THE THONG KE
    // ==================================

    private JPanel createStatCard(
            String title, JLabel value) {

        JPanel card =
                new JPanel(new BorderLayout(10, 10));

        card.setBackground(Color.WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 235)),
                        new EmptyBorder(20, 15, 20, 15)));

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 14));

        titleLabel.setForeground(
                new Color(100, 110, 125));

        value.setFont(
                new Font("SansSerif", Font.BOLD, 25));

        value.setForeground(
                new Color(35, 105, 175));

        card.add(
                titleLabel, BorderLayout.NORTH);

        card.add(
                value, BorderLayout.CENTER);

        return card;
    }

    // ==================================
    // 13. CAP NHAT DASHBOARD
    // ==================================

    public void refreshDashboard() {

        // Tong so loai san pham
        lblTotalProducts.setText(
                String.valueOf(
                        productService.countProducts()));

        // Tong so luong ton
        int totalQuantity =
                productService.getAllProducts()
                        .stream()
                        .mapToInt(Product::getQuantity)
                        .sum();

        lblTotalQuantity.setText(
                String.valueOf(totalQuantity));

        // Gia tri ton kho
        double inventoryValue =
                productService.calculateInventoryValue();

        lblInventoryValue.setText(
                String.format(
                        "%,.0f VND",
                        inventoryValue));

        // Tong hoa don
        lblTotalInvoices.setText(
                String.valueOf(
                        salesService.countInvoices()));

        // Tong doanh thu
        double revenue =
                salesService.calculateTotalRevenue();

        lblTotalRevenue.setText(
                String.format(
                        "%,.0f VND", revenue));
    }

    // ==================================
    // 14. CAP NHAT SAU KHI BAN HANG
    // ==================================

    private void refreshAfterSale() {

        refreshDashboard();

        // Cap nhat ton kho tren giao dien
        productPanel.refreshTable();

        // Cap nhat lich su hoa don
        invoicePanel.refreshData();
    }

    // ==================================
    // 15. TAO FOOTER
    // ==================================

    private void createFooter() {

        JPanel footer =
                new JPanel(new BorderLayout());

        footer.setBackground(
                new Color(238, 242, 247));

        footer.setBorder(
                new EmptyBorder(10, 20, 10, 20));

        JLabel lblInfo = new JLabel(
                "MiniSupermarket | Java Swing | OOP | File I/O");

        lblInfo.setFont(
                new Font("SansSerif", Font.PLAIN, 12));

        lblInfo.setForeground(
                new Color(100, 110, 125));

        JLabel lblVersion =
                new JLabel("Version 1.0");

        footer.add(
                lblInfo, BorderLayout.WEST);

        footer.add(
                lblVersion, BorderLayout.EAST);

        add(footer, BorderLayout.SOUTH);
    }
}
