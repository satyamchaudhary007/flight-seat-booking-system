package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.UUID;

public class BookingConfirmation extends JFrame {

    private final int userId;
    private final String userName;
    private final int flightId;
    private final String flightNumber;
    private final String airlineName;
    private final double flightPrice;
    private final int selectedSeatId;
    private final String selectedSeatNumber;
    private final String passengerName;
    private final int passengerAge;
    private final String passengerGender;
    private final String passengerPhone;
    private final String paymentMethod;

    private String pnr;

    public BookingConfirmation(
            int userId,
            String userName,
            int flightId,
            String flightNumber,
            String airlineName,
            double flightPrice,
            int selectedSeatId,
            String selectedSeatNumber,
            String passengerName,
            int passengerAge,
            String passengerGender,
            String passengerPhone,
            String paymentMethod
    ) {

        this.userId = userId;
        this.userName = userName;
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airlineName = airlineName;
        this.flightPrice = flightPrice;
        this.selectedSeatId = selectedSeatId;
        this.selectedSeatNumber = selectedSeatNumber;
        this.passengerName = passengerName;
        this.passengerAge = passengerAge;
        this.passengerGender = passengerGender;
        this.passengerPhone = passengerPhone;
        this.paymentMethod = paymentMethod;

        setTitle("SkySeat - Booking Confirmation");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        createHeader();
        createLoadingScreen();

        SwingUtilities.invokeLater(
                this::createBooking
        );
    }

    private void createHeader() {

        JPanel header =
                new JPanel(new BorderLayout());

        header.setBackground(
                new Color(20, 55, 100)
        );

        header.setPreferredSize(
                new Dimension(900, 70)
        );

        JLabel logo =
                new JLabel("  SKYSEAT");

        logo.setForeground(Color.WHITE);

        logo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        26
                )
        );

        JLabel user =
                new JLabel(
                        "Welcome, "
                                + userName
                                + "    "
                );

        user.setForeground(Color.WHITE);

        user.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        header.add(
                logo,
                BorderLayout.WEST
        );

        header.add(
                user,
                BorderLayout.EAST
        );

        add(
                header,
                BorderLayout.NORTH
        );
    }

    private void createLoadingScreen() {

        JPanel panel =
                new JPanel(
                        new GridBagLayout()
                );

        panel.setBackground(Color.WHITE);

        JLabel label =
                new JLabel(
                        "Processing your booking..."
                );

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        20
                )
        );

        panel.add(label);

        add(
                panel,
                BorderLayout.CENTER
        );
    }

    private void createBooking() {

        Connection con = null;
        PreparedStatement passengerStatement = null;
        PreparedStatement bookingStatement = null;
        PreparedStatement paymentStatement = null;
        PreparedStatement seatStatement = null;

        ResultSet passengerKeys = null;
        ResultSet bookingKeys = null;
        ResultSet paymentKeys = null;

        try {

            con = DBConnection.getConnection();

            if (con == null) {

                showError(
                        "Database connection failed."
                );

                return;
            }

            con.setAutoCommit(false);

            String passengerSql =
                    "INSERT INTO PASSENGERS " +
                            "(PASSENGER_ID, USER_ID, FULL_NAME, AGE, GENDER, PHONE) " +
                            "VALUES " +
                            "(PASSENGERS_SEQ.NEXTVAL, ?, ?, ?, ?, ?)";

            passengerStatement =
                    con.prepareStatement(
                            passengerSql,
                            new String[]{"PASSENGER_ID"}
                    );

            passengerStatement.setInt(
                    1,
                    userId
            );

            passengerStatement.setString(
                    2,
                    passengerName
            );

            passengerStatement.setInt(
                    3,
                    passengerAge
            );

            passengerStatement.setString(
                    4,
                    passengerGender
            );

            passengerStatement.setString(
                    5,
                    passengerPhone
            );

            passengerStatement.executeUpdate();

            passengerKeys =
                    passengerStatement.getGeneratedKeys();

            int passengerId;

            if (passengerKeys != null
                    && passengerKeys.next()) {

                passengerId =
                        passengerKeys.getInt(1);

            } else {

                passengerId =
                        getLatestPassengerId(
                                con
                        );
            }

            pnr = generatePNR();

            String bookingSql =
                    "INSERT INTO BOOKINGS " +
                            "(BOOKING_ID, USER_ID, FLIGHT_ID, SEAT_ID, " +
                            "PASSENGER_ID, PNR, BOOKING_DATE, STATUS) " +
                            "VALUES " +
                            "(BOOKINGS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, SYSTIMESTAMP, ?)";

            bookingStatement =
                    con.prepareStatement(
                            bookingSql,
                            new String[]{"BOOKING_ID"}
                    );

            bookingStatement.setInt(
                    1,
                    userId
            );

            bookingStatement.setInt(
                    2,
                    flightId
            );

            bookingStatement.setInt(
                    3,
                    selectedSeatId
            );

            bookingStatement.setInt(
                    4,
                    passengerId
            );

            bookingStatement.setString(
                    5,
                    pnr
            );

            bookingStatement.setString(
                    6,
                    "CONFIRMED"
            );

            bookingStatement.executeUpdate();

            bookingKeys =
                    bookingStatement.getGeneratedKeys();

            int bookingId;

            if (bookingKeys != null
                    && bookingKeys.next()) {

                bookingId =
                        bookingKeys.getInt(1);

            } else {

                bookingId =
                        getLatestBookingId(
                                con
                        );
            }

            String paymentSql =
                    "INSERT INTO PAYMENTS " +
                            "(PAYMENT_ID, BOOKING_ID, AMOUNT, PAYMENT_METHOD, " +
                            "PAYMENT_STATUS, PAYMENT_DATE) " +
                            "VALUES " +
                            "(PAYMENTS_SEQ.NEXTVAL, ?, ?, ?, ?, SYSTIMESTAMP)";

            paymentStatement =
                    con.prepareStatement(
                            paymentSql,
                            new String[]{"PAYMENT_ID"}
                    );

            paymentStatement.setInt(
                    1,
                    bookingId
            );

            paymentStatement.setDouble(
                    2,
                    flightPrice
            );

            paymentStatement.setString(
                    3,
                    paymentMethod
            );

            paymentStatement.setString(
                    4,
                    "SUCCESS"
            );

            paymentStatement.executeUpdate();

            String seatSql =
                    "UPDATE SEATS " +
                            "SET STATUS = 'BOOKED' " +
                            "WHERE SEAT_ID = ? " +
                            "AND FLIGHT_ID = ? " +
                            "AND UPPER(STATUS) <> 'BOOKED'";

            seatStatement =
                    con.prepareStatement(
                            seatSql
                    );

            seatStatement.setInt(
                    1,
                    selectedSeatId
            );

            seatStatement.setInt(
                    2,
                    flightId
            );

            int updated =
                    seatStatement.executeUpdate();

            if (updated == 0) {

                con.rollback();

                showError(
                        "This seat is no longer available."
                );

                return;
            }

            con.commit();

            showConfirmation();

        } catch (Exception ex) {

            ex.printStackTrace();

            try {

                if (con != null) {
                    con.rollback();
                }

            } catch (Exception ignored) {
            }

            showError(
                    "Booking failed.\n"
                            + ex.getMessage()
            );

        } finally {

            try {
                if (passengerKeys != null) {
                    passengerKeys.close();
                }
            } catch (Exception ignored) {
            }

            try {
                if (bookingKeys != null) {
                    bookingKeys.close();
                }
            } catch (Exception ignored) {
            }

            try {
                if (paymentKeys != null) {
                    paymentKeys.close();
                }
            } catch (Exception ignored) {
            }

            try {
                if (passengerStatement != null) {
                    passengerStatement.close();
                }
            } catch (Exception ignored) {
            }

            try {
                if (bookingStatement != null) {
                    bookingStatement.close();
                }
            } catch (Exception ignored) {
            }

            try {
                if (paymentStatement != null) {
                    paymentStatement.close();
                }
            } catch (Exception ignored) {
            }

            try {
                if (seatStatement != null) {
                    seatStatement.close();
                }
            } catch (Exception ignored) {
            }

            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private int getLatestPassengerId(
            Connection con
    ) throws Exception {

        String sql =
                "SELECT PASSENGER_ID " +
                        "FROM (" +
                        "SELECT PASSENGER_ID " +
                        "FROM PASSENGERS " +
                        "WHERE USER_ID = ? " +
                        "ORDER BY PASSENGER_ID DESC" +
                        ") " +
                        "WHERE ROWNUM = 1";

        try (
                PreparedStatement pst =
                        con.prepareStatement(sql)
        ) {

            pst.setInt(
                    1,
                    userId
            );

            try (
                    ResultSet rs =
                            pst.executeQuery()
            ) {

                if (rs.next()) {
                    return rs.getInt(
                            "PASSENGER_ID"
                    );
                }
            }
        }

        throw new Exception(
                "Unable to find passenger ID."
        );
    }

    private int getLatestBookingId(
            Connection con
    ) throws Exception {

        String sql =
                "SELECT BOOKING_ID " +
                        "FROM (" +
                        "SELECT BOOKING_ID " +
                        "FROM BOOKINGS " +
                        "WHERE USER_ID = ? " +
                        "ORDER BY BOOKING_ID DESC" +
                        ") " +
                        "WHERE ROWNUM = 1";

        try (
                PreparedStatement pst =
                        con.prepareStatement(sql)
        ) {

            pst.setInt(
                    1,
                    userId
            );

            try (
                    ResultSet rs =
                            pst.executeQuery()
            ) {

                if (rs.next()) {
                    return rs.getInt(
                            "BOOKING_ID"
                    );
                }
            }
        }

        throw new Exception(
                "Unable to find booking ID."
        );
    }

    private String generatePNR() {

        return "SKY"
                + UUID.randomUUID()
                .toString()
                .replace(
                        "-",
                        ""
                )
                .substring(
                        0,
                        7
                )
                .toUpperCase();
    }

    private void showConfirmation() {

        getContentPane().removeAll();

        JPanel main =
                new JPanel(
                        new BorderLayout()
                );

        main.setBackground(Color.WHITE);

        JPanel top =
                new JPanel();

        top.setLayout(
                new BoxLayout(
                        top,
                        BoxLayout.Y_AXIS
                )
        );

        top.setBackground(Color.WHITE);
        top.setBorder(
                BorderFactory.createEmptyBorder(
                        30,
                        30,
                        15,
                        30
                )
        );

        JLabel success =
                new JLabel(
                        "BOOKING CONFIRMED"
                );

        success.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        success.setForeground(
                new Color(
                        25,
                        130,
                        70
                )
        );

        success.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        30
                )
        );

        JLabel pnrLabel =
                new JLabel(
                        "PNR: " + pnr
                );

        pnrLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        pnrLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        top.add(success);
        top.add(
                Box.createVerticalStrut(12)
        );
        top.add(pnrLabel);

        main.add(
                top,
                BorderLayout.NORTH
        );

        JPanel details =
                new JPanel(
                        new GridBagLayout()
                );

        details.setBackground(Color.WHITE);
        details.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        220,
                                        220,
                                        220
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                20,
                                30,
                                20,
                                30
                        )
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        8,
                        12,
                        8,
                        12
                );

        gbc.anchor =
                GridBagConstraints.WEST;

        addDetail(
                details,
                gbc,
                0,
                "Passenger",
                passengerName
        );

        addDetail(
                details,
                gbc,
                1,
                "Airline",
                airlineName
        );

        addDetail(
                details,
                gbc,
                2,
                "Flight",
                flightNumber
        );

        addDetail(
                details,
                gbc,
                3,
                "Seat",
                selectedSeatNumber
        );

        addDetail(
                details,
                gbc,
                4,
                "Amount",
                "₹"
                        + String.format(
                        "%.2f",
                        flightPrice
                )
        );

        addDetail(
                details,
                gbc,
                5,
                "Payment",
                paymentMethod
        );

        addDetail(
                details,
                gbc,
                6,
                "Status",
                "CONFIRMED"
        );

        main.add(
                details,
                BorderLayout.CENTER
        );

        JPanel bottom =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                15,
                                15
                        )
                );

        bottom.setBackground(Color.WHITE);

        JButton ticketButton =
                new JButton(
                        "VIEW TICKET"
                );

        ticketButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        ticketButton.setFocusPainted(false);

        ticketButton.addActionListener(
                e -> showTicket()
        );

        JButton closeButton =
                new JButton(
                        "CLOSE"
                );

        closeButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        closeButton.setFocusPainted(false);

        closeButton.addActionListener(
                e -> dispose()
        );

        bottom.add(ticketButton);
        bottom.add(closeButton);

        main.add(
                bottom,
                BorderLayout.SOUTH
        );

        add(
                main,
                BorderLayout.CENTER
        );

        revalidate();
        repaint();
    }

    private void addDetail(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String labelText,
            String valueText
    ) {

        gbc.gridx = 0;
        gbc.gridy = row;

        JLabel label =
                new JLabel(
                        labelText
                                + ":"
                );

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        panel.add(
                label,
                gbc
        );

        gbc.gridx = 1;

        JLabel value =
                new JLabel(
                        valueText
                );

        value.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        panel.add(
                value,
                gbc
        );
    }

    private void showTicket() {

        JOptionPane.showMessageDialog(
                this,
                "SKYSEAT TICKET\n\n"
                        + "PNR: "
                        + pnr
                        + "\nPassenger: "
                        + passengerName
                        + "\nFlight: "
                        + flightNumber
                        + "\nAirline: "
                        + airlineName
                        + "\nSeat: "
                        + selectedSeatNumber
                        + "\nAmount: ₹"
                        + String.format(
                        "%.2f",
                        flightPrice
                ),
                "Ticket",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showError(
            String message
    ) {

        getContentPane().removeAll();

        JPanel panel =
                new JPanel(
                        new GridBagLayout()
                );

        panel.setBackground(Color.WHITE);

        JLabel label =
                new JLabel(
                        "<html><center>"
                                + message.replace(
                                "\n",
                                "<br>"
                        )
                                + "</center></html>"
                );

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        panel.add(label);

        add(
                panel,
                BorderLayout.CENTER
        );

        revalidate();
        repaint();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new BookingConfirmation(
                    1,
                    "Satyam",
                    1,
                    "6E-123",
                    "IndiGo",
                    6500,
                    1,
                    "1A",
                    "Satyam Kumar",
                    20,
                    "Male",
                    "9876543210",
                    "UPI"
            ).setVisible(true);

        });
    }
}
