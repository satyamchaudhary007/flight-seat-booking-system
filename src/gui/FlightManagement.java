package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FlightManagement extends JFrame {

    private JTextField flightNumberField;
    private JTextField departureField;
    private JTextField arrivalField;
    private JTextField priceField;
    private JTextField searchField;

    private JComboBox<AirlineItem> airlineCombo;
    private JComboBox<AirportItem> sourceCombo;
    private JComboBox<AirportItem> destinationCombo;

    private JTable flightTable;
    private DefaultTableModel tableModel;

    private int selectedFlightId = -1;

    // Dark Theme Color System matching reference design
    private final Color bgDark = new Color(11, 17, 30);        // Deep Slate Dark Base
    private final Color cardBg = new Color(21, 30, 50);        // Dark Card Background
    private final Color borderDark = new Color(30, 41, 59);    // Subtle Dark Border
    private final Color fieldBg = new Color(15, 23, 42);       // Dark Field Background
    private final Color accentBlue = new Color(0, 112, 243);   // Modern Electric Blue
    private final Color textBright = new Color(248, 250, 252); // High Contrast White Text
    private final Color textMuted = new Color(148, 163, 184);  // Subtitle/Label Muted Gray

    public FlightManagement() {
        setTitle("SkySeat - Flight Management");
        setSize(1350, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        buildUI();
        loadAirlines();
        loadAirports();
        loadFlights("");
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            super.setVisible(true);

            SwingUtilities.invokeLater(() -> {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                toFront();
                requestFocus();
            });
        } else {
            super.setVisible(false);
        }
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(0, 15));
        main.setBackground(bgDark);
        setContentPane(main);

        // 1. Top Navigation Bar
        main.add(createTopNavbar(), BorderLayout.NORTH);

        // 2. Central Scrollable Dashboard View
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(bgDark);
        container.setBorder(new EmptyBorder(10, 30, 30, 30));

        // Page Header Banner
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(1920, 50));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Flight Management");
        title.setForeground(textBright);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subtitle = new JLabel("Add, edit, delete and search SkySeat flights");
        subtitle.setForeground(textMuted);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(subtitle);
        headerPanel.add(titleBox);

        container.add(headerPanel);
        container.add(Box.createVerticalStrut(15));
        container.add(createFormCard());
        container.add(Box.createVerticalStrut(15));
        container.add(createFilterCard());
        container.add(Box.createVerticalStrut(15));
        container.add(createTableCard());

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(bgDark);

        main.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createTopNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(fieldBg);
        navbar.setPreferredSize(new Dimension(1350, 55));
        navbar.setBorder(new EmptyBorder(0, 25, 0, 25));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        leftPanel.setOpaque(false);

        JLabel menuIcon = createVectorIconLabel("menu", textMuted, 20, 20);
        JLabel logoIcon = createVectorIconLabel("plane", accentBlue, 22, 22);
        JLabel logoText = new JLabel("SkySeat");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoText.setForeground(textBright);

        leftPanel.add(menuIcon);
        leftPanel.add(Box.createHorizontalStrut(8));
        leftPanel.add(logoIcon);
        leftPanel.add(logoText);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        rightPanel.setOpaque(false);

        JLabel userAvatar = createVectorIconLabel("user-round", textBright, 22, 22);
        JLabel settingsIcon = createVectorIconLabel("settings", textMuted, 20, 20);

        rightPanel.add(userAvatar);
        rightPanel.add(settingsIcon);

        navbar.add(leftPanel, BorderLayout.WEST);
        navbar.add(rightPanel, BorderLayout.EAST);
        return navbar;
    }

    private JPanel createFormCard() {
        RoundedCard card = new RoundedCard(16, cardBg, borderDark);
        card.setLayout(new BorderLayout(15, 15));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);

        flightNumberField = createStyledTextField("Flight Number");
        departureField = createStyledTextField("Departure (dd-MM-yyyy HH:mm)");
        arrivalField = createStyledTextField("Arrival (dd-MM-yyyy HH:mm)");
        priceField = createStyledTextField("Price");

        airlineCombo = createStyledComboBox();
        sourceCombo = createStyledComboBox();
        destinationCombo = createStyledComboBox();

        Dimension fieldSize = new Dimension(260, 36);
        flightNumberField.setPreferredSize(fieldSize);
        departureField.setPreferredSize(fieldSize);
        arrivalField.setPreferredSize(fieldSize);
        priceField.setPreferredSize(fieldSize);
        airlineCombo.setPreferredSize(fieldSize);
        sourceCombo.setPreferredSize(fieldSize);
        destinationCombo.setPreferredSize(fieldSize);

        // Row 1
        addFieldToGrid(formGrid, "Flight Number", flightNumberField, 0, 0);
        addFieldToGrid(formGrid, "Airline", airlineCombo, 1, 0);
        addFieldToGrid(formGrid, "Source", sourceCombo, 2, 0);
        addFieldToGrid(formGrid, "Destination", destinationCombo, 3, 0);

        // Action Buttons Row
        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actionButtons.setOpaque(false);

        JButton addButton = createPillButton("ADD FLIGHT", accentBlue, Color.WHITE, true);
        JButton updateButton = createPillButton("UPDATE", new Color(30, 41, 59), new Color(148, 163, 184), true);
        JButton deleteButton = createPillButton("DELETE", new Color(45, 20, 30), new Color(248, 113, 113), true);
        JButton clearButton = createPillButton("CLEAR", new Color(51, 65, 85), Color.WHITE, true);

        addButton.addActionListener(e -> addFlight());
        updateButton.addActionListener(e -> updateFlight());
        deleteButton.addActionListener(e -> deleteFlight());
        clearButton.addActionListener(e -> clearForm());

        actionButtons.add(addButton);
        actionButtons.add(updateButton);
        actionButtons.add(deleteButton);
        actionButtons.add(clearButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 5, 0, 5);
        formGrid.add(actionButtons, gbc);

        card.add(formGrid, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFilterCard() {
        RoundedCard card = new RoundedCard(16, cardBg, borderDark);
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 12));
        card.setBorder(new EmptyBorder(10, 15, 10, 15));

        searchField = createStyledTextField("Search...");
        searchField.setPreferredSize(new Dimension(300, 36));

        // Departure Date Filter Box
        JPanel depBox = createIconInputGroup("calendar-days", departureField, "Departure");
        JPanel arrBox = createIconInputGroup("calendar-days", arrivalField, "Arrival");

        // Airline Token Dummy Field
        JPanel tokenBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        tokenBox.setBackground(fieldBg);
        tokenBox.setBorder(BorderFactory.createLineBorder(borderDark, 1));
        tokenBox.setPreferredSize(new Dimension(160, 36));

        JLabel tokenTag = new JLabel("Airline ×");
        tokenTag.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tokenTag.setForeground(textMuted);
        tokenBox.add(tokenTag);

        // Price Range Slider Filter
        JPanel priceSliderPanel = new JPanel(new BorderLayout(5, 0));
        priceSliderPanel.setOpaque(false);
        JLabel priceLbl = new JLabel("Price Range");
        priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        priceLbl.setForeground(textMuted);

        JSlider priceSlider = new JSlider(1000, 10000, 5000);
        priceSlider.setOpaque(false);
        priceSlider.setUI(new CustomSliderUI(priceSlider));

        priceSliderPanel.add(priceLbl, BorderLayout.NORTH);
        priceSliderPanel.add(priceSlider, BorderLayout.CENTER);

        // Filter Action Buttons
        JButton searchBtn = createPillButton("Search", accentBlue, Color.WHITE, false);
        JButton resetBtn = createPillButton("Reset", new Color(51, 65, 85), textBright, false);

        searchBtn.addActionListener(e -> loadFlights(searchField.getText().trim()));
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            loadFlights("");
        });

        card.add(searchField);
        card.add(depBox);
        card.add(arrBox);
        card.add(tokenBox);
        card.add(priceSliderPanel);
        card.add(searchBtn);
        card.add(resetBtn);

        return card;
    }

    private JPanel createTableCard() {
        RoundedCard card = new RoundedCard(16, cardBg, borderDark);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(12, 12, 12, 12));

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "FLIGHT", "AIRLINE", "SOURCE", "DESTINATION", "DEPARTURE", "ARRIVAL", "PRICE"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        flightTable = new JTable(tableModel);
        flightTable.setBackground(cardBg);
        flightTable.setForeground(textBright);
        flightTable.setGridColor(new Color(30, 41, 59));
        flightTable.setRowHeight(42);
        flightTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        flightTable.setSelectionBackground(new Color(30, 58, 138));
        flightTable.setSelectionForeground(Color.WHITE);

        flightTable.getTableHeader().setBackground(new Color(15, 23, 42));
        flightTable.getTableHeader().setForeground(textMuted);
        flightTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        flightTable.getTableHeader().setPreferredSize(new Dimension(100, 36));

        // Custom Cell Renderer for Image Icons & Custom Column Colors
        flightTable.getColumnModel().getColumn(2).setCellRenderer(new AirlineCellRenderer());
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        flightTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedFlight();
        });

        JScrollPane scrollPane = new JScrollPane(flightTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(cardBg);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private void addFieldToGrid(JPanel panel, String label, JComponent component, int x, int y) {
        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = x; lc.gridy = y; lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(4, 6, 4, 6);

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(textMuted);

        panel.add(l, lc);

        GridBagConstraints fc = new GridBagConstraints();
        fc.gridx = x; fc.gridy = y + 1; fc.anchor = GridBagConstraints.WEST;
        fc.insets = new Insets(0, 6, 8, 6);

        panel.add(component, fc);
    }

    private JPanel createIconInputGroup(String iconName, JTextField field, String labelText) {
        JPanel container = new JPanel(new BorderLayout(5, 0));
        container.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(textMuted);

        JPanel inputGroup = new JPanel(new BorderLayout(6, 0));
        inputGroup.setBackground(fieldBg);
        inputGroup.setBorder(BorderFactory.createLineBorder(borderDark, 1));
        inputGroup.setPreferredSize(new Dimension(160, 36));

        JTextField input = new JTextField();
        input.setOpaque(false);
        input.setForeground(textBright);
        input.setCaretColor(textBright);
        input.setBorder(null);
        input.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel icon = createVectorIconLabel(iconName, textMuted, 16, 16);
        icon.setBorder(new EmptyBorder(0, 8, 0, 0));

        inputGroup.add(icon, BorderLayout.WEST);
        inputGroup.add(input, BorderLayout.CENTER);

        container.add(lbl, BorderLayout.NORTH);
        container.add(inputGroup, BorderLayout.CENTER);
        return container;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setBackground(fieldBg);
        tf.setForeground(textBright);
        tf.setCaretColor(textBright);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderDark, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        return tf;
    }

    private <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> combo = new JComboBox<>();
        combo.setBackground(fieldBg);
        combo.setForeground(textBright);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBorder(BorderFactory.createLineBorder(borderDark, 1));
        return combo;
    }

    private JButton createPillButton(String text, Color bg, Color fg, boolean fill) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setPreferredSize(new Dimension(110, 36));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Backend Logic Handlers (100% Retained)
    private void loadAirlines() {
        airlineCombo.removeAllItems();
        String sql = "SELECT AIRLINE_ID, AIRLINE_NAME, AIRLINE_CODE FROM AIRLINES ORDER BY AIRLINE_NAME";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                airlineCombo.addItem(new AirlineItem(rs.getInt("AIRLINE_ID"), rs.getString("AIRLINE_NAME"), rs.getString("AIRLINE_CODE")));
            }

        } catch (Exception ex) {
            showError("Unable to load airlines.", ex);
        }
    }

    private void loadAirports() {
        sourceCombo.removeAllItems();
        destinationCombo.removeAllItems();

        String sql = "SELECT AIRPORT_ID, AIRPORT_NAME, CITY, AIRPORT_CODE FROM AIRPORTS ORDER BY CITY, AIRPORT_NAME";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<AirportItem> airports = new ArrayList<>();
            while (rs.next()) {
                airports.add(new AirportItem(rs.getInt("AIRPORT_ID"), rs.getString("AIRPORT_NAME"), rs.getString("CITY"), rs.getString("AIRPORT_CODE")));
            }

            for (AirportItem airport : airports) {
                sourceCombo.addItem(airport);
                destinationCombo.addItem(airport);
            }

        } catch (Exception ex) {
            showError("Unable to load airports.", ex);
        }
    }

    private void loadFlights(String search) {
        tableModel.setRowCount(0);
        String sql = "SELECT f.FLIGHT_ID, f.FLIGHT_NUMBER, a.AIRLINE_NAME, sa.CITY AS SOURCE_CITY, da.CITY AS DEST_CITY, " +
                "f.DEPARTURE_TIME, f.ARRIVAL_TIME, f.PRICE FROM FLIGHTS f " +
                "JOIN AIRLINES a ON f.AIRLINE_ID = a.AIRLINE_ID " +
                "JOIN AIRPORTS sa ON f.SOURCE_AIRPORT_ID = sa.AIRPORT_ID " +
                "JOIN AIRPORTS da ON f.DESTINATION_AIRPORT_ID = da.AIRPORT_ID " +
                "WHERE UPPER(f.FLIGHT_NUMBER) LIKE UPPER(?) OR UPPER(a.AIRLINE_NAME) LIKE UPPER(?) " +
                "OR UPPER(sa.CITY) LIKE UPPER(?) OR UPPER(da.CITY) LIKE UPPER(?) ORDER BY f.DEPARTURE_TIME";

        String keyword = "%" + search + "%";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, keyword); ps.setString(2, keyword);
            ps.setString(3, keyword); ps.setString(4, keyword);

            try (ResultSet rs = ps.executeQuery()) {
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                while (rs.next()) {
                    Timestamp dep = rs.getTimestamp("DEPARTURE_TIME");
                    Timestamp arr = rs.getTimestamp("ARRIVAL_TIME");

                    String depStr = (dep != null) ? displayFormat.format(dep) : "N/A";
                    String arrStr = (arr != null) ? displayFormat.format(arr) : "N/A";

                    tableModel.addRow(new Object[]{
                            rs.getInt("FLIGHT_ID"), rs.getString("FLIGHT_NUMBER"), rs.getString("AIRLINE_NAME"),
                            rs.getString("SOURCE_CITY"), rs.getString("DEST_CITY"),
                            depStr, arrStr,
                            String.format("₹ %.2f", rs.getDouble("PRICE"))
                    });
                }
            }

        } catch (Exception ex) {
            showError("Unable to load flights.", ex);
        }
    }

    private void loadSelectedFlight() {
        int viewRow = flightTable.getSelectedRow();
        if (viewRow < 0) return;

        int row = flightTable.convertRowIndexToModel(viewRow);
        selectedFlightId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

        String sql = "SELECT f.FLIGHT_NUMBER, f.AIRLINE_ID, f.SOURCE_AIRPORT_ID, f.DESTINATION_AIRPORT_ID, " +
                "f.DEPARTURE_TIME, f.ARRIVAL_TIME, f.PRICE FROM FLIGHTS f WHERE f.FLIGHT_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, selectedFlightId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    flightNumberField.setText(rs.getString("FLIGHT_NUMBER"));
                    priceField.setText(String.valueOf(rs.getDouble("PRICE")));

                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    Timestamp dep = rs.getTimestamp("DEPARTURE_TIME");
                    Timestamp arr = rs.getTimestamp("ARRIVAL_TIME");

                    departureField.setText(dep != null ? format.format(dep) : "");
                    arrivalField.setText(arr != null ? format.format(arr) : "");

                    selectAirline(rs.getInt("AIRLINE_ID"));
                    selectAirport(sourceCombo, rs.getInt("SOURCE_AIRPORT_ID"));
                    selectAirport(destinationCombo, rs.getInt("DESTINATION_AIRPORT_ID"));
                }
            }

        } catch (Exception ex) {
            showError("Unable to load selected flight.", ex);
        }
    }

    private void addFlight() {
        if (!validateFields()) return;

        AirlineItem airline = (AirlineItem) airlineCombo.getSelectedItem();
        AirportItem source = (AirportItem) sourceCombo.getSelectedItem();
        AirportItem destination = (AirportItem) destinationCombo.getSelectedItem();

        if (airline == null || source == null || destination == null) {
            showMessage("Please select airline, source and destination.");
            return;
        }

        if (source.airportId == destination.airportId) {
            showMessage("Source and destination cannot be the same.");
            return;
        }

        try {
            Timestamp departure = parseTimestamp(departureField.getText().trim());
            Timestamp arrival = parseTimestamp(arrivalField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());

            if (!arrival.after(departure)) {
                showMessage("Arrival time must be after departure time.");
                return;
            }

            try (Connection con = DBConnection.getConnection()) {
                if (con == null) {
                    showMessage("Database connection failed.");
                    return;
                }

                con.setAutoCommit(false);
                int flightId = -1;

                String insert = "INSERT INTO FLIGHTS (AIRLINE_ID, FLIGHT_NUMBER, DEPARTURE_TIME, ARRIVAL_TIME, PRICE, SOURCE_AIRPORT_ID, DESTINATION_AIRPORT_ID) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, airline.airlineId);
                    ps.setString(2, flightNumberField.getText().trim());
                    ps.setTimestamp(3, departure); ps.setTimestamp(4, arrival);
                    ps.setDouble(5, price); ps.setInt(6, source.airportId); ps.setInt(7, destination.airportId);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            flightId = keys.getInt(1);
                        } else {
                            try (PreparedStatement idPs = con.prepareStatement("SELECT FLIGHT_ID FROM FLIGHTS WHERE FLIGHT_NUMBER = ? AND DEPARTURE_TIME = ? ORDER BY FLIGHT_ID DESC LIMIT 1")) {
                                idPs.setString(1, flightNumberField.getText().trim());
                                idPs.setTimestamp(2, departure);
                                try (ResultSet idRs = idPs.executeQuery()) {
                                    if (!idRs.next()) throw new SQLException("Unable to find newly created flight.");
                                    flightId = idRs.getInt(1);
                                }
                            }
                        }
                    }
                }

                createSeats(con, flightId);
                con.commit();

                showMessage("Flight added successfully.");
                clearForm();
                loadFlights(searchField.getText().trim());
            }

        } catch (NumberFormatException ex) {
            showMessage("Price must be a valid number.");
        } catch (IllegalArgumentException ex) {
            showMessage(ex.getMessage());
        } catch (Exception ex) {
            showError("Unable to add flight.", ex);
        }
    }

    private void updateFlight() {
        if (selectedFlightId == -1) {
            showMessage("Select a flight from the table first.");
            return;
        }

        if (!validateFields()) return;

        AirlineItem airline = (AirlineItem) airlineCombo.getSelectedItem();
        AirportItem source = (AirportItem) sourceCombo.getSelectedItem();
        AirportItem destination = (AirportItem) destinationCombo.getSelectedItem();

        if (airline == null || source == null || destination == null) {
            showMessage("Please select airline, source and destination.");
            return;
        }

        if (source.airportId == destination.airportId) {
            showMessage("Source and destination cannot be the same.");
            return;
        }

        try {
            Timestamp departure = parseTimestamp(departureField.getText().trim());
            Timestamp arrival = parseTimestamp(arrivalField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());

            if (!arrival.after(departure)) {
                showMessage("Arrival time must be after departure time.");
                return;
            }

            String sql = "UPDATE FLIGHTS SET AIRLINE_ID=?, FLIGHT_NUMBER=?, DEPARTURE_TIME=?, ARRIVAL_TIME=?, PRICE=?, " +
                    "SOURCE_AIRPORT_ID=?, DESTINATION_AIRPORT_ID=? WHERE FLIGHT_ID=?";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, airline.airlineId); ps.setString(2, flightNumberField.getText().trim());
                ps.setTimestamp(3, departure); ps.setTimestamp(4, arrival); ps.setDouble(5, price);
                ps.setInt(6, source.airportId); ps.setInt(7, destination.airportId); ps.setInt(8, selectedFlightId);

                int updated = ps.executeUpdate();
                if (updated == 1) {
                    showMessage("Flight updated successfully.");
                    clearForm();
                    loadFlights(searchField.getText().trim());
                } else showMessage("Flight was not updated.");
            }

        } catch (NumberFormatException ex) {
            showMessage("Price must be a valid number.");
        } catch (IllegalArgumentException ex) {
            showMessage(ex.getMessage());
        } catch (Exception ex) {
            showError("Unable to update flight.", ex);
        }
    }

    private void deleteFlight() {
        if (selectedFlightId == -1) {
            showMessage("Select a flight from the table first.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Delete Flight ID " + selectedFlightId + "?\n\nThe flight can only be deleted if it has no bookings.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (answer != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement bookingCheck = con.prepareStatement("SELECT COUNT(*) FROM BOOKINGS WHERE FLIGHT_ID=?")) {
                    bookingCheck.setInt(1, selectedFlightId);
                    try (ResultSet rs = bookingCheck.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) > 0) {
                            con.rollback();
                            showMessage("This flight has bookings and cannot be deleted.");
                            return;
                        }
                    }
                }

                try (PreparedStatement seatDelete = con.prepareStatement("DELETE FROM SEATS WHERE FLIGHT_ID=?")) {
                    seatDelete.setInt(1, selectedFlightId);
                    seatDelete.executeUpdate();
                }

                try (PreparedStatement flightDelete = con.prepareStatement("DELETE FROM FLIGHTS WHERE FLIGHT_ID=?")) {
                    flightDelete.setInt(1, selectedFlightId);
                    int deleted = flightDelete.executeUpdate();
                    if (deleted != 1) {
                        con.rollback();
                        showMessage("Flight was not deleted.");
                        return;
                    }
                }

                con.commit();
                showMessage("Flight deleted successfully.");
                clearForm();
                loadFlights(searchField.getText().trim());

            } catch (Exception ex) {
                con.rollback();
                throw ex;
            }

        } catch (Exception ex) {
            showError("Unable to delete flight.", ex);
        }
    }

    private void createSeats(Connection con, int flightId) throws SQLException {
        String insert = "INSERT INTO SEATS (FLIGHT_ID, SEAT_NUMBER, SEAT_CLASS, STATUS) VALUES (?, ?, ?, 'AVAILABLE')";
        try (PreparedStatement ps = con.prepareStatement(insert)) {
            for (int row = 1; row <= 13; row++) {
                for (char col = 'A'; col <= 'F'; col++) {
                    ps.setInt(1, flightId);
                    ps.setString(2, row + String.valueOf(col));
                    ps.setString(3, row <= 2 ? "BUSINESS" : "ECONOMY");
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }

    private boolean validateFields() {
        if (flightNumberField.getText().trim().isEmpty()) { showMessage("Enter flight number."); return false; }
        if (departureField.getText().trim().isEmpty()) { showMessage("Enter departure date and time."); return false; }
        if (arrivalField.getText().trim().isEmpty()) { showMessage("Enter arrival date and time."); return false; }
        if (priceField.getText().trim().isEmpty()) { showMessage("Enter flight price."); return false; }
        return true;
    }

    private Timestamp parseTimestamp(String value) throws IllegalArgumentException {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            format.setLenient(false);
            Date date = format.parse(value);
            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Date format must be dd-MM-yyyy HH:mm (e.g., 25-10-2026 14:30)");
        }
    }

    private void selectAirline(int airlineId) {
        for (int i = 0; i < airlineCombo.getItemCount(); i++) {
            AirlineItem item = airlineCombo.getItemAt(i);
            if (item.airlineId == airlineId) { airlineCombo.setSelectedIndex(i); return; }
        }
    }

    private void selectAirport(JComboBox<AirportItem> combo, int airportId) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            AirportItem item = combo.getItemAt(i);
            if (item.airportId == airportId) { combo.setSelectedIndex(i); return; }
        }
    }

    private void clearForm() {
        selectedFlightId = -1;
        flightTable.clearSelection();
        flightNumberField.setText(""); departureField.setText("");
        arrivalField.setText(""); priceField.setText("");
        if (airlineCombo.getItemCount() > 0) airlineCombo.setSelectedIndex(0);
        if (sourceCombo.getItemCount() > 0) sourceCombo.setSelectedIndex(0);
        if (destinationCombo.getItemCount() > 0) destinationCombo.setSelectedIndex(0);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "SkySeat", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(this, message + "\n\n" + ex.getMessage(), "SkySeat Error", JOptionPane.ERROR_MESSAGE);
    }

    // Dynamic Vector Graphics Generator
    private JLabel createVectorIconLabel(String iconName, Color strokeColor, int width, int height) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(width, height));
        label.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(strokeColor);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                double scaleX = width / 24.0;
                double scaleY = height / 24.0;
                g2.translate(x, y);
                g2.scale(scaleX, scaleY);

                switch (iconName) {
                    case "plane":
                        Path2D.Double plane = new Path2D.Double();
                        plane.moveTo(17.8, 19.2); plane.lineTo(16, 11); plane.lineTo(19.5, 7.5);
                        plane.curveTo(21, 6, 21.5, 4, 21, 3);
                        plane.curveTo(20, 2.5, 18, 3, 16.5, 4.5);
                        plane.lineTo(13, 8); plane.lineTo(4.8, 6.2);
                        plane.curveTo(4.3, 6.1, 3.9, 6.3, 3.7, 6.7);
                        plane.lineTo(3.4, 7.2);
                        plane.curveTo(3.2, 7.7, 3.3, 8.2, 3.7, 8.5);
                        plane.lineTo(9, 12); plane.lineTo(7, 15); plane.lineTo(4, 15);
                        plane.lineTo(3, 16); plane.lineTo(6, 18); plane.lineTo(8, 21);
                        plane.lineTo(9, 20); plane.lineTo(9, 17); plane.lineTo(12, 15);
                        plane.lineTo(15.5, 20.3);
                        g2.draw(plane);
                        break;

                    case "user-round":
                        g2.drawOval(7, 3, 10, 10);
                        g2.draw(new Path2D.Double() {{
                            moveTo(20, 21);
                            curveTo(20, 16.5, 16.4, 13, 12, 13);
                            curveTo(7.6, 13, 4, 16.5, 4, 21);
                        }});
                        break;

                    case "menu":
                        g2.drawLine(3, 6, 21, 6);
                        g2.drawLine(3, 12, 21, 12);
                        g2.drawLine(3, 18, 21, 18);
                        break;

                    case "settings":
                        g2.drawOval(9, 9, 6, 6);
                        g2.draw(new Path2D.Double() {{
                            moveTo(12, 2); lineTo(12, 4);
                            moveTo(12, 20); lineTo(12, 22);
                            moveTo(2, 12); lineTo(4, 12);
                            moveTo(20, 12); lineTo(22, 12);
                        }});
                        break;

                    case "calendar-days":
                        g2.drawRoundRect(3, 3, 18, 18, 4, 4);
                        g2.draw(new Path2D.Double() {{
                            moveTo(8, 2); lineTo(8, 5);
                            moveTo(16, 2); lineTo(16, 5);
                            moveTo(3, 9); lineTo(21, 9);
                        }});
                        break;
                }
                g2.dispose();
            }

            @Override public int getIconWidth() { return width; }
            @Override public int getIconHeight() { return height; }
        });
        return label;
    }

    // Custom Airline Table Cell Renderer with local image fallback
    private class AirlineCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String airlineName = value != null ? value.toString() : "";

            label.setText(airlineName);
            label.setForeground(textBright);
            label.setIconTextGap(10);

            // Path lookup for local image icons
            String fileName = airlineName.toLowerCase().replaceAll("\\s+", "") + ".png";
            File imgFile = new File("images/" + fileName);

            if (imgFile.exists()) {
                ImageIcon original = new ImageIcon(imgFile.getAbsolutePath());
                Image scaled = original.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaled));
            } else {
                JLabel planeIc = createVectorIconLabel("plane", accentBlue, 16, 16);
                label.setIcon(planeIc.getIcon());
            }

            return label;
        }
    }

    // Rounded Border Container Helper
    static class RoundedCard extends JPanel {
        private final int radius;
        private final Color bg;
        private final Color border;

        public RoundedCard(int radius, Color bg, Color border) {
            this.radius = radius;
            this.bg = bg;
            this.border = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (bg != null) {
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            }

            if (border != null) {
                g2.setColor(border);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Custom Slider UI for Price Range
    static class CustomSliderUI extends BasicSliderUI {
        public CustomSliderUI(JSlider b) { super(b); }
        @Override
        public void paintTrack(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(30, 41, 59));
            g2.fillRoundRect(trackRect.x, trackRect.y + trackRect.height/2 - 2, trackRect.width, 4, 4, 4);
            g2.setColor(new Color(0, 112, 243));
            g2.fillRoundRect(trackRect.x, trackRect.y + trackRect.height/2 - 2, thumbRect.x - trackRect.x, 4, 4, 4);
            g2.dispose();
        }
        @Override
        public void paintThumb(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillOval(thumbRect.x, thumbRect.y + thumbRect.height/2 - 6, 12, 12);
            g2.dispose();
        }
    }

    private static class AirlineItem {
        int airlineId; String name; String code;
        AirlineItem(int airlineId, String name, String code) { this.airlineId = airlineId; this.name = name; this.code = code; }
        @Override public String toString() { return name + " (" + code + ")"; }
    }

    private static class AirportItem {
        int airportId; String airportName; String city; String code;
        AirportItem(int airportId, String airportName, String city, String code) {
            this.airportId = airportId; this.airportName = airportName; this.city = city; this.code = code;
        }
        @Override public String toString() { return city + " (" + code + ")"; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlightManagement().setVisible(true));
    }
}