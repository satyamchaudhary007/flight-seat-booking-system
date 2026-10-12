package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JLabel titleLabel;

    private JLabel usersCount;
    private JLabel passengersCount;
    private JLabel flightsCount;
    private JLabel bookingsCount;
    private JLabel paymentsCount;
    private JLabel airportsCount;
    private JLabel airlinesCount;
    private JLabel seatsCount;

    public AdminDashboard() {
        setTitle("SkySeat - Admin Dashboard");
        setSize(1250, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 55, 100));
        header.setPreferredSize(new Dimension(1250, 70));

        JLabel logo = new JLabel("  SKYSEAT ADMIN PANEL");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 24));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));

        logoutButton.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });

        header.add(logo, BorderLayout.WEST);
        header.add(logoutButton, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(30, 40, 55));
        sidebar.setPreferredSize(new Dimension(205, 680));
        sidebar.setLayout(new GridLayout(11, 1, 0, 5));

        JButton dashboardButton = createMenuButton("Dashboard");
        JButton usersButton = createMenuButton("Users");
        JButton passengersButton = createMenuButton("Passengers");
        JButton flightsButton = createMenuButton("Flights");
        JButton flightManagementButton = createMenuButton("Flight Management");
        JButton bookingsButton = createMenuButton("Bookings");
        JButton paymentsButton = createMenuButton("Payments");
        JButton airportsButton = createMenuButton("Airports");
        JButton airlinesButton = createMenuButton("Airlines");
        JButton seatsButton = createMenuButton("Seats");
        JButton exitButton = createMenuButton("Exit");

        sidebar.add(dashboardButton);
        sidebar.add(usersButton);
        sidebar.add(passengersButton);
        sidebar.add(flightsButton);
        sidebar.add(flightManagementButton);
        sidebar.add(bookingsButton);
        sidebar.add(paymentsButton);
        sidebar.add(airportsButton);
        sidebar.add(airlinesButton);
        sidebar.add(seatsButton);
        sidebar.add(exitButton);

        add(sidebar, BorderLayout.WEST);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(2, 4, 10, 10));

        usersCount = createCard(cardsPanel, "Users");
        passengersCount = createCard(cardsPanel, "Passengers");
        flightsCount = createCard(cardsPanel, "Flights");
        bookingsCount = createCard(cardsPanel, "Bookings");
        paymentsCount = createCard(cardsPanel, "Payments");
        airportsCount = createCard(cardsPanel, "Airports");
        airlinesCount = createCard(cardsPanel, "Airlines");
        seatsCount = createCard(cardsPanel, "Seats");

        model = new DefaultTableModel();
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1000, 400));

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));

        refreshButton.addActionListener(e -> {
            loadCounts();
            loadTable("USERS");
        });

        tablePanel.add(refreshButton, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(cardsPanel, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        dashboardButton.addActionListener(e -> {
            titleLabel.setText("Dashboard");
            loadCounts();
            loadTable("USERS");
        });

        usersButton.addActionListener(e -> {
            titleLabel.setText("Users");
            loadTable("USERS");
        });

        passengersButton.addActionListener(e -> {
            titleLabel.setText("Passengers");
            loadTable("PASSENGERS");
        });

        flightsButton.addActionListener(e -> {
            titleLabel.setText("Flights");
            loadTable("FLIGHTS");
        });

        flightManagementButton.addActionListener(e -> {
            new FlightManagement().setVisible(true);
        });

        bookingsButton.addActionListener(e -> {
            titleLabel.setText("Bookings");
            loadTable("BOOKINGS");
        });

        paymentsButton.addActionListener(e -> {
            titleLabel.setText("Payments");
            loadTable("PAYMENTS");
        });

        airportsButton.addActionListener(e -> {
            titleLabel.setText("Airports");
            loadTable("AIRPORTS");
        });

        airlinesButton.addActionListener(e -> {
            titleLabel.setText("Airlines");
            loadTable("AIRLINES");
        });

        seatsButton.addActionListener(e -> {
            titleLabel.setText("Seats");
            loadTable("SEATS");
        });

        exitButton.addActionListener(e -> System.exit(0));

        loadCounts();
        loadTable("USERS");
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(30, 40, 55));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 5));
        return button;
    }

    private JLabel createCard(JPanel panel, String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(235, 240, 250));
        card.setBorder(BorderFactory.createLineBorder(new Color(190, 200, 215)));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JLabel countLabel = new JLabel("0", SwingConstants.CENTER);
        countLabel.setFont(new Font("Arial", Font.BOLD, 25));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);

        panel.add(card);

        return countLabel;
    }

    private void loadCounts() {
        usersCount.setText(getCount("USERS"));
        passengersCount.setText(getCount("PASSENGERS"));
        flightsCount.setText(getCount("FLIGHTS"));
        bookingsCount.setText(getCount("BOOKINGS"));
        paymentsCount.setText(getCount("PAYMENTS"));
        airportsCount.setText(getCount("AIRPORTS"));
        airlinesCount.setText(getCount("AIRLINES"));
        seatsCount.setText(getCount("SEATS"));
    }

    private String getCount(String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "0";
    }

    private void loadTable(String tableName) {
        String sql = "SELECT * FROM " + tableName;

        model.setRowCount(0);
        model.setColumnCount(0);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            while (rs.next()) {
                Object[] row = new Object[columnCount];

                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }

                model.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database Error:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard().setVisible(true);
        });
    }
}
