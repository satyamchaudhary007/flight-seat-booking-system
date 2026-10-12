package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
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

    private final Color dark = new Color(15, 23, 42);
    private final Color blue = new Color(37, 99, 235);
    private final Color green = new Color(22, 163, 74);
    private final Color red = new Color(220, 38, 38);
    private final Color light = new Color(248, 250, 252);

    public FlightManagement() {
        setTitle("SkySeat - Flight Management");
        setSize(1250, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildUI();
        loadAirlines();
        loadAirports();
        loadFlights("");
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBackground(light);
        main.setBorder(new EmptyBorder(18, 18, 18, 18));
        setContentPane(main);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(dark);
        header.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel title = new JLabel("Flight Management");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 25));

        JLabel subtitle = new JLabel("Add, edit, delete and search SkySeat flights");
        subtitle.setForeground(new Color(203, 213, 225));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(subtitle);

        header.add(titleBox, BorderLayout.WEST);
        main.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(15, 15));
        center.setOpaque(false);

        center.add(createFormPanel(), BorderLayout.NORTH);
        center.add(createTablePanel(), BorderLayout.CENTER);

        main.add(center, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel outer = new JPanel(new BorderLayout(12, 12));
        outer.setBackground(Color.WHITE);
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        flightNumberField = new JTextField();
        departureField = new JTextField();
        arrivalField = new JTextField();
        priceField = new JTextField();

        airlineCombo = new JComboBox<>();
        sourceCombo = new JComboBox<>();
        destinationCombo = new JComboBox<>();

        Dimension fieldSize = new Dimension(205, 34);
        flightNumberField.setPreferredSize(fieldSize);
        departureField.setPreferredSize(fieldSize);
        arrivalField.setPreferredSize(fieldSize);
        priceField.setPreferredSize(fieldSize);
        airlineCombo.setPreferredSize(fieldSize);
        sourceCombo.setPreferredSize(fieldSize);
        destinationCombo.setPreferredSize(fieldSize);

        addField(form, "Flight Number", flightNumberField, 0, 0);
        addField(form, "Airline", airlineCombo, 1, 0);
        addField(form, "Source", sourceCombo, 2, 0);
        addField(form, "Destination", destinationCombo, 3, 0);

        addField(form, "Departure (dd-MM-yyyy HH:mm)", departureField, 0, 1);
        addField(form, "Arrival (dd-MM-yyyy HH:mm)", arrivalField, 1, 1);
        addField(form, "Price", priceField, 2, 1);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        buttons.setOpaque(false);

        JButton addButton = createButton("ADD FLIGHT", green);
        JButton updateButton = createButton("UPDATE", blue);
        JButton deleteButton = createButton("DELETE", red);
        JButton clearButton = createButton("CLEAR", new Color(71, 85, 105));

        addButton.addActionListener(e -> addFlight());
        updateButton.addActionListener(e -> updateFlight());
        deleteButton.addActionListener(e -> deleteFlight());
        clearButton.addActionListener(e -> clearForm());

        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0;
        bc.gridy = 2;
        bc.gridwidth = 4;
        bc.anchor = GridBagConstraints.WEST;
        bc.insets = new Insets(8, 5, 0, 5);
        form.add(buttons, bc);

        outer.add(form, BorderLayout.CENTER);

        return outer;
    }

    private void addField(JPanel panel, String label, JComponent component, int x, int y) {
        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = x;
        lc.gridy = y;
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(5, 5, 2, 8);

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(51, 65, 85));

        panel.add(l, lc);

        GridBagConstraints fc = new GridBagConstraints();
        fc.gridx = x;
        fc.gridy = y + 1;
        fc.anchor = GridBagConstraints.WEST;
        fc.insets = new Insets(0, 5, 8, 8);

        panel.add(component, fc);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 34));

        JButton searchButton = createButton("SEARCH", blue);
        JButton refreshButton = createButton("REFRESH", new Color(71, 85, 105));

        searchButton.addActionListener(e -> loadFlights(searchField.getText().trim()));
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadFlights("");
        });

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        left.add(searchLabel);
        left.add(searchField);
        left.add(searchButton);
        left.add(refreshButton);

        searchPanel.add(left, BorderLayout.WEST);

        panel.add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{
                        "ID", "Flight", "Airline", "Source", "Destination",
                        "Departure", "Arrival", "Price"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        flightTable = new JTable(tableModel);
        flightTable.setRowHeight(30);
        flightTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        flightTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightTable.setAutoCreateRowSorter(true);

        flightTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedFlight();
            }
        });

        JScrollPane scrollPane = new JScrollPane(flightTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(125, 34));
        return button;
    }

    private void loadAirlines() {
        airlineCombo.removeAllItems();

        String sql = "SELECT AIRLINE_ID, AIRLINE_NAME, AIRLINE_CODE FROM AIRLINES ORDER BY AIRLINE_NAME";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                airlineCombo.addItem(new AirlineItem(
                        rs.getInt("AIRLINE_ID"),
                        rs.getString("AIRLINE_NAME"),
                        rs.getString("AIRLINE_CODE")
                ));
            }

        } catch (Exception ex) {
            showError("Unable to load airlines.", ex);
        }
    }

    private void loadAirports() {
        sourceCombo.removeAllItems();
        destinationCombo.removeAllItems();

        String sql = "SELECT AIRPORT_ID, AIRPORT_NAME, CITY, AIRPORT_CODE " +
                "FROM AIRPORTS ORDER BY CITY, AIRPORT_NAME";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<AirportItem> airports = new ArrayList<>();

            while (rs.next()) {
                airports.add(new AirportItem(
                        rs.getInt("AIRPORT_ID"),
                        rs.getString("AIRPORT_NAME"),
                        rs.getString("CITY"),
                        rs.getString("AIRPORT_CODE")
                ));
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

        String sql =
                "SELECT f.FLIGHT_ID, f.FLIGHT_NUMBER, a.AIRLINE_NAME, " +
                        "sa.CITY AS SOURCE_CITY, da.CITY AS DEST_CITY, " +
                        "f.DEPARTURE_TIME, f.ARRIVAL_TIME, f.PRICE " +
                        "FROM FLIGHTS f " +
                        "JOIN AIRLINES a ON f.AIRLINE_ID = a.AIRLINE_ID " +
                        "JOIN AIRPORTS sa ON f.SOURCE_AIRPORT_ID = sa.AIRPORT_ID " +
                        "JOIN AIRPORTS da ON f.DESTINATION_AIRPORT_ID = da.AIRPORT_ID " +
                        "WHERE UPPER(f.FLIGHT_NUMBER) LIKE UPPER(?) " +
                        "OR UPPER(a.AIRLINE_NAME) LIKE UPPER(?) " +
                        "OR UPPER(sa.CITY) LIKE UPPER(?) " +
                        "OR UPPER(da.CITY) LIKE UPPER(?) " +
                        "ORDER BY f.DEPARTURE_TIME";

        String keyword = "%" + search + "%";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, keyword);
            ps.setString(2, keyword);
            ps.setString(3, keyword);
            ps.setString(4, keyword);

            try (ResultSet rs = ps.executeQuery()) {
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("FLIGHT_ID"),
                            rs.getString("FLIGHT_NUMBER"),
                            rs.getString("AIRLINE_NAME"),
                            rs.getString("SOURCE_CITY"),
                            rs.getString("DEST_CITY"),
                            displayFormat.format(rs.getTimestamp("DEPARTURE_TIME")),
                            displayFormat.format(rs.getTimestamp("ARRIVAL_TIME")),
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

        if (viewRow < 0) {
            return;
        }

        int row = flightTable.convertRowIndexToModel(viewRow);
        selectedFlightId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

        String sql =
                "SELECT f.FLIGHT_NUMBER, f.AIRLINE_ID, f.SOURCE_AIRPORT_ID, " +
                        "f.DESTINATION_AIRPORT_ID, f.DEPARTURE_TIME, f.ARRIVAL_TIME, f.PRICE " +
                        "FROM FLIGHTS f WHERE f.FLIGHT_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, selectedFlightId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    flightNumberField.setText(rs.getString("FLIGHT_NUMBER"));
                    priceField.setText(String.valueOf(rs.getDouble("PRICE")));

                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    departureField.setText(format.format(rs.getTimestamp("DEPARTURE_TIME")));
                    arrivalField.setText(format.format(rs.getTimestamp("ARRIVAL_TIME")));

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
        if (!validateFields()) {
            return;
        }

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
                con.setAutoCommit(false);

                int flightId;

                String insert =
                        "INSERT INTO FLIGHTS " +
                                "(FLIGHT_ID, AIRLINE_ID, FLIGHT_NUMBER, DEPARTURE_TIME, ARRIVAL_TIME, PRICE, SOURCE_AIRPORT_ID, DESTINATION_AIRPORT_ID) " +
                                "VALUES (FLIGHTS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement ps = con.prepareStatement(insert, new String[]{"FLIGHT_ID"})) {
                    ps.setInt(1, airline.airlineId);
                    ps.setString(2, flightNumberField.getText().trim());
                    ps.setTimestamp(3, departure);
                    ps.setTimestamp(4, arrival);
                    ps.setDouble(5, price);
                    ps.setInt(6, source.airportId);
                    ps.setInt(7, destination.airportId);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            flightId = keys.getInt(1);
                        } else {
                            try (PreparedStatement idPs = con.prepareStatement(
                                    "SELECT FLIGHT_ID FROM FLIGHTS WHERE FLIGHT_NUMBER = ? AND DEPARTURE_TIME = ? ORDER BY FLIGHT_ID DESC")) {
                                idPs.setString(1, flightNumberField.getText().trim());
                                idPs.setTimestamp(2, departure);
                                try (ResultSet idRs = idPs.executeQuery()) {
                                    if (!idRs.next()) {
                                        throw new SQLException("Unable to find newly created flight.");
                                    }
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

            } catch (Exception ex) {
                throw ex;
            }

        } catch (NumberFormatException ex) {
            showMessage("Price must be a valid number.");
        } catch (Exception ex) {
            showError("Unable to add flight.", ex);
        }
    }

    private void updateFlight() {
        if (selectedFlightId == -1) {
            showMessage("Select a flight from the table first.");
            return;
        }

        if (!validateFields()) {
            return;
        }

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

            String sql =
                    "UPDATE FLIGHTS SET AIRLINE_ID=?, FLIGHT_NUMBER=?, " +
                            "DEPARTURE_TIME=?, ARRIVAL_TIME=?, PRICE=?, " +
                            "SOURCE_AIRPORT_ID=?, DESTINATION_AIRPORT_ID=? " +
                            "WHERE FLIGHT_ID=?";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, airline.airlineId);
                ps.setString(2, flightNumberField.getText().trim());
                ps.setTimestamp(3, departure);
                ps.setTimestamp(4, arrival);
                ps.setDouble(5, price);
                ps.setInt(6, source.airportId);
                ps.setInt(7, destination.airportId);
                ps.setInt(8, selectedFlightId);

                int updated = ps.executeUpdate();

                if (updated == 1) {
                    showMessage("Flight updated successfully.");
                    clearForm();
                    loadFlights(searchField.getText().trim());
                } else {
                    showMessage("Flight was not updated.");
                }
            }

        } catch (NumberFormatException ex) {
            showMessage("Price must be a valid number.");
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
                "Delete Flight ID " + selectedFlightId + "?\n\n" +
                        "The flight can only be deleted if it has no bookings.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement bookingCheck = con.prepareStatement(
                        "SELECT COUNT(*) FROM BOOKINGS WHERE FLIGHT_ID=?")) {

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

                try (PreparedStatement seatDelete = con.prepareStatement(
                        "DELETE FROM SEATS WHERE FLIGHT_ID=?")) {
                    seatDelete.setInt(1, selectedFlightId);
                    seatDelete.executeUpdate();
                }

                try (PreparedStatement flightDelete = con.prepareStatement(
                        "DELETE FROM FLIGHTS WHERE FLIGHT_ID=?")) {
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
        String insert =
                "INSERT INTO SEATS " +
                        "(SEAT_ID, FLIGHT_ID, SEAT_NUMBER, SEAT_CLASS, STATUS) " +
                        "VALUES (SEATS_SEQ.NEXTVAL, ?, ?, ?, 'AVAILABLE')";

        try (PreparedStatement ps = con.prepareStatement(insert)) {
            for (int row = 1; row <= 5; row++) {
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
        if (flightNumberField.getText().trim().isEmpty()) {
            showMessage("Enter flight number.");
            return false;
        }

        if (departureField.getText().trim().isEmpty()) {
            showMessage("Enter departure date and time.");
            return false;
        }

        if (arrivalField.getText().trim().isEmpty()) {
            showMessage("Enter arrival date and time.");
            return false;
        }

        if (priceField.getText().trim().isEmpty()) {
            showMessage("Enter flight price.");
            return false;
        }

        return true;
    }

    private Timestamp parseTimestamp(String value) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        format.setLenient(false);
        Date date = format.parse(value);
        return new Timestamp(date.getTime());
    }

    private void selectAirline(int airlineId) {
        for (int i = 0; i < airlineCombo.getItemCount(); i++) {
            AirlineItem item = airlineCombo.getItemAt(i);
            if (item.airlineId == airlineId) {
                airlineCombo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectAirport(JComboBox<AirportItem> combo, int airportId) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            AirportItem item = combo.getItemAt(i);
            if (item.airportId == airportId) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void clearForm() {
        selectedFlightId = -1;
        flightTable.clearSelection();

        flightNumberField.setText("");
        departureField.setText("");
        arrivalField.setText("");
        priceField.setText("");

        if (airlineCombo.getItemCount() > 0) {
            airlineCombo.setSelectedIndex(0);
        }

        if (sourceCombo.getItemCount() > 0) {
            sourceCombo.setSelectedIndex(0);
        }

        if (destinationCombo.getItemCount() > 0) {
            destinationCombo.setSelectedIndex(0);
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "SkySeat",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                message + "\n\n" + ex.getMessage(),
                "SkySeat Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private static class AirlineItem {
        int airlineId;
        String name;
        String code;

        AirlineItem(int airlineId, String name, String code) {
            this.airlineId = airlineId;
            this.name = name;
            this.code = code;
        }

        @Override
        public String toString() {
            return name + " (" + code + ")";
        }
    }

    private static class AirportItem {
        int airportId;
        String airportName;
        String city;
        String code;

        AirportItem(int airportId, String airportName, String city, String code) {
            this.airportId = airportId;
            this.airportName = airportName;
            this.city = city;
            this.code = code;
        }

        @Override
        public String toString() {
            return city + " (" + code + ")";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlightManagement().setVisible(true));
    }
}
