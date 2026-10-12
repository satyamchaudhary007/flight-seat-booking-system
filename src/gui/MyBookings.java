package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class MyBookings extends JFrame {

    private final int userId;
    private final String userName;

    private JTable bookingTable;
    private DefaultTableModel model;

    public MyBookings(int userId, String userName) {

        this.userId = userId;
        this.userName = userName;

        setTitle("SkySeat - My Bookings");
        setSize(1200, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        createHeader();
        createTable();
        createButtons();

        loadBookings();
    }

    private void createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 55, 100));
        header.setPreferredSize(new Dimension(1200, 70));

        JLabel logo = new JLabel("  SKYSEAT");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel title = new JLabel("My Bookings    ");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 17));

        header.add(logo, BorderLayout.WEST);
        header.add(title, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void createTable() {

        String[] columns = {
                "Booking ID",
                "PNR",
                "Flight",
                "Route",
                "Travel Date",
                "Seat",
                "Passenger",
                "Amount",
                "Status"
        };

        model = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        bookingTable = new JTable(model);

        bookingTable.setRowHeight(30);
        bookingTable.setFont(new Font("Arial", Font.PLAIN, 13));
        bookingTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        bookingTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane =
                new JScrollPane(bookingTable);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void createButtons() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                12,
                                12
                        )
                );

        JButton viewButton =
                new JButton("VIEW TICKET");

        JButton cancelButton =
                new JButton("CANCEL TICKET");

        JButton refreshButton =
                new JButton("REFRESH");

        JButton closeButton =
                new JButton("CLOSE");

        viewButton.addActionListener(
                e -> viewTicket()
        );

        cancelButton.addActionListener(
                e -> cancelTicket()
        );

        refreshButton.addActionListener(
                e -> loadBookings()
        );

        closeButton.addActionListener(
                e -> dispose()
        );

        panel.add(viewButton);
        panel.add(cancelButton);
        panel.add(refreshButton);
        panel.add(closeButton);

        add(panel, BorderLayout.SOUTH);
    }

    private void loadBookings() {

        model.setRowCount(0);

        String sql =
                "SELECT " +
                        "b.BOOKING_ID, " +
                        "b.PNR, " +
                        "f.FLIGHT_NUMBER, " +
                        "src.CITY AS SOURCE_CITY, " +
                        "dest.CITY AS DESTINATION_CITY, " +
                        "f.DEPARTURE_TIME, " +
                        "s.SEAT_NUMBER, " +
                        "p.FULL_NAME, " +
                        "pay.AMOUNT, " +
                        "b.STATUS " +
                        "FROM BOOKINGS b " +
                        "JOIN FLIGHTS f " +
                        "ON b.FLIGHT_ID = f.FLIGHT_ID " +
                        "JOIN AIRPORTS src " +
                        "ON f.SOURCE_AIRPORT_ID = src.AIRPORT_ID " +
                        "JOIN AIRPORTS dest " +
                        "ON f.DESTINATION_AIRPORT_ID = dest.AIRPORT_ID " +
                        "JOIN SEATS s " +
                        "ON b.SEAT_ID = s.SEAT_ID " +
                        "JOIN PASSENGERS p " +
                        "ON b.PASSENGER_ID = p.PASSENGER_ID " +
                        "LEFT JOIN PAYMENTS pay " +
                        "ON b.BOOKING_ID = pay.BOOKING_ID " +
                        "WHERE b.USER_ID = ? " +
                        "ORDER BY b.BOOKING_DATE DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {

                    Timestamp departure =
                            rs.getTimestamp(
                                    "DEPARTURE_TIME"
                            );

                    String date =
                            departure == null
                                    ? ""
                                    : departure.toLocalDateTime()
                                    .toLocalDate()
                                    .toString();

                    model.addRow(
                            new Object[]{
                                    rs.getInt("BOOKING_ID"),
                                    rs.getString("PNR"),
                                    rs.getString("FLIGHT_NUMBER"),
                                    rs.getString("SOURCE_CITY")
                                            + " → "
                                            + rs.getString(
                                            "DESTINATION_CITY"
                                    ),
                                    date,
                                    rs.getString("SEAT_NUMBER"),
                                    rs.getString("FULL_NAME"),
                                    rs.getObject("AMOUNT"),
                                    rs.getString("STATUS")
                            }
                    );
                }
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load bookings.\n"
                            + ex.getMessage(),
                    "Booking Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Integer getSelectedBookingId() {

        int row =
                bookingTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a booking first.",
                    "My Bookings",
                    JOptionPane.WARNING_MESSAGE
            );

            return null;
        }

        return (Integer) model.getValueAt(
                row,
                0
        );
    }

    private void viewTicket() {

        Integer bookingId =
                getSelectedBookingId();

        if (bookingId == null) {
            return;
        }

        String sql =
                "SELECT " +
                        "b.BOOKING_ID, b.PNR, b.STATUS, " +
                        "b.BOOKING_DATE, " +
                        "f.FLIGHT_NUMBER, " +
                        "f.DEPARTURE_TIME, " +
                        "f.ARRIVAL_TIME, " +
                        "a.AIRLINE_NAME, " +
                        "src.CITY AS SOURCE_CITY, " +
                        "src.AIRPORT_CODE AS SOURCE_CODE, " +
                        "dest.CITY AS DESTINATION_CITY, " +
                        "dest.AIRPORT_CODE AS DESTINATION_CODE, " +
                        "s.SEAT_NUMBER, " +
                        "s.SEAT_CLASS, " +
                        "p.FULL_NAME, p.AGE, p.GENDER, p.PHONE, " +
                        "pay.AMOUNT, pay.PAYMENT_METHOD, " +
                        "pay.PAYMENT_STATUS " +
                        "FROM BOOKINGS b " +
                        "JOIN FLIGHTS f " +
                        "ON b.FLIGHT_ID = f.FLIGHT_ID " +
                        "JOIN AIRLINES a " +
                        "ON f.AIRLINE_ID = a.AIRLINE_ID " +
                        "JOIN AIRPORTS src " +
                        "ON f.SOURCE_AIRPORT_ID = src.AIRPORT_ID " +
                        "JOIN AIRPORTS dest " +
                        "ON f.DESTINATION_AIRPORT_ID = dest.AIRPORT_ID " +
                        "JOIN SEATS s " +
                        "ON b.SEAT_ID = s.SEAT_ID " +
                        "JOIN PASSENGERS p " +
                        "ON b.PASSENGER_ID = p.PASSENGER_ID " +
                        "LEFT JOIN PAYMENTS pay " +
                        "ON b.BOOKING_ID = pay.BOOKING_ID " +
                        "WHERE b.BOOKING_ID = ? " +
                        "AND b.USER_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, bookingId);
            pst.setInt(2, userId);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    String ticket =
                            "SKYSEAT\n"
                                    + "====================================\n"
                                    + "PNR              : "
                                    + rs.getString("PNR") + "\n"
                                    + "Status            : "
                                    + rs.getString("STATUS") + "\n"
                                    + "Airline           : "
                                    + rs.getString("AIRLINE_NAME") + "\n"
                                    + "Flight            : "
                                    + rs.getString("FLIGHT_NUMBER") + "\n"
                                    + "Route             : "
                                    + rs.getString("SOURCE_CITY")
                                    + " ("
                                    + rs.getString("SOURCE_CODE")
                                    + ") → "
                                    + rs.getString("DESTINATION_CITY")
                                    + " ("
                                    + rs.getString("DESTINATION_CODE")
                                    + ")\n"
                                    + "Departure         : "
                                    + rs.getTimestamp("DEPARTURE_TIME")
                                    + "\n"
                                    + "Arrival           : "
                                    + rs.getTimestamp("ARRIVAL_TIME")
                                    + "\n"
                                    + "Passenger         : "
                                    + rs.getString("FULL_NAME")
                                    + "\n"
                                    + "Age / Gender      : "
                                    + rs.getInt("AGE")
                                    + " / "
                                    + rs.getString("GENDER")
                                    + "\n"
                                    + "Phone             : "
                                    + rs.getString("PHONE")
                                    + "\n"
                                    + "Seat              : "
                                    + rs.getString("SEAT_NUMBER")
                                    + "\n"
                                    + "Class             : "
                                    + rs.getString("SEAT_CLASS")
                                    + "\n"
                                    + "Amount            : ₹"
                                    + rs.getObject("AMOUNT")
                                    + "\n"
                                    + "Payment Method    : "
                                    + rs.getString("PAYMENT_METHOD")
                                    + "\n"
                                    + "Payment Status    : "
                                    + rs.getString("PAYMENT_STATUS")
                                    + "\n"
                                    + "====================================";

                    JTextArea area =
                            new JTextArea(ticket);

                    area.setFont(
                            new Font(
                                    "Monospaced",
                                    Font.PLAIN,
                                    14
                            )
                    );

                    area.setEditable(false);

                    JScrollPane scroll =
                            new JScrollPane(area);

                    scroll.setPreferredSize(
                            new Dimension(
                                    650,
                                    500
                            )
                    );

                    JOptionPane.showMessageDialog(
                            this,
                            scroll,
                            "SkySeat - Ticket",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load ticket.\n"
                            + ex.getMessage(),
                    "Ticket Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void cancelTicket() {

        Integer bookingId =
                getSelectedBookingId();

        if (bookingId == null) {
            return;
        }

        int row =
                bookingTable.getSelectedRow();

        String status =
                String.valueOf(
                        model.getValueAt(
                                row,
                                8
                        )
                );

        if (!"CONFIRMED".equalsIgnoreCase(status)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Only confirmed bookings can be cancelled.",
                    "Cancellation",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String pnr =
                String.valueOf(
                        model.getValueAt(
                                row,
                                1
                        )
                );

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Cancel ticket with PNR " + pnr + "?",
                        "Confirm Cancellation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String bookingSql =
                "UPDATE BOOKINGS " +
                        "SET STATUS = 'CANCELLED' " +
                        "WHERE BOOKING_ID = ? " +
                        "AND USER_ID = ? " +
                        "AND STATUS = 'CONFIRMED'";

        String seatSql =
                "UPDATE SEATS " +
                        "SET STATUS = 'AVAILABLE' " +
                        "WHERE SEAT_ID = (" +
                        "SELECT SEAT_ID FROM BOOKINGS " +
                        "WHERE BOOKING_ID = ?" +
                        ")";

        String paymentSql =
                "UPDATE PAYMENTS " +
                        "SET PAYMENT_STATUS = 'REFUND_PENDING' " +
                        "WHERE BOOKING_ID = ? " +
                        "AND PAYMENT_STATUS = 'SUCCESS'";

        Connection con = null;

        try {

            con = DBConnection.getConnection();

            if (con == null) {
                throw new Exception("Database connection failed.");
            }

            con.setAutoCommit(false);

            int updatedBooking;

            try (PreparedStatement pst =
                         con.prepareStatement(bookingSql)) {

                pst.setInt(1, bookingId);
                pst.setInt(2, userId);

                updatedBooking =
                        pst.executeUpdate();
            }

            if (updatedBooking == 0) {

                con.rollback();

                JOptionPane.showMessageDialog(
                        this,
                        "Booking could not be cancelled.",
                        "Cancellation",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            try (PreparedStatement pst =
                         con.prepareStatement(seatSql)) {

                pst.setInt(1, bookingId);
                pst.executeUpdate();
            }

            try (PreparedStatement pst =
                         con.prepareStatement(paymentSql)) {

                pst.setInt(1, bookingId);
                pst.executeUpdate();
            }

            con.commit();

            JOptionPane.showMessageDialog(
                    this,
                    "Ticket cancelled successfully.\n"
                            + "PNR: " + pnr
                            + "\nPayment status: REFUND_PENDING",
                    "SkySeat",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadBookings();

        } catch (Exception ex) {

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ignored) {
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Cancellation failed.\n"
                            + ex.getMessage(),
                    "Cancellation Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new MyBookings(1, "Satyam").setVisible(true)
        );
    }
}
