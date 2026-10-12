package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FlightResults extends JFrame {

    private final int userId;
    private final String userName;
    private final String fromCity;
    private final String toCity;
    private final String travelDate;
    private final int passengerCount;

    private JPanel resultsPanel;

    public FlightResults(
            int userId,
            String userName,
            String fromCity,
            String toCity,
            String travelDate,
            int passengerCount
    ) {

        this.userId = userId;
        this.userName = userName;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.travelDate = travelDate;
        this.passengerCount = passengerCount;

        setTitle("SkySeat - Available Flights");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        createHeader();
        createSearchSummary();
        createResults();

        loadFlights();
    }

    private void createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 55, 100));
        header.setPreferredSize(new Dimension(1200, 70));

        JLabel logo = new JLabel("  SKYSEAT");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel user = new JLabel("Welcome, " + userName + "    ");
        user.setForeground(Color.WHITE);
        user.setFont(new Font("Arial", Font.BOLD, 15));

        header.add(logo, BorderLayout.WEST);
        header.add(user, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void createSearchSummary() {

        JPanel summary = new JPanel(new BorderLayout());

        summary.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        20,
                        10,
                        20
                )
        );

        JLabel route = new JLabel(
                fromCity + "  →  " + toCity
        );

        route.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        JLabel details = new JLabel(
                "Date: " + travelDate +
                        "    |    Passengers: " +
                        passengerCount
        );

        details.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        summary.add(route, BorderLayout.NORTH);
        summary.add(details, BorderLayout.SOUTH);

        add(summary, BorderLayout.NORTH);
    }

    private void createResults() {

        resultsPanel = new JPanel();

        resultsPanel.setLayout(
                new BoxLayout(
                        resultsPanel,
                        BoxLayout.Y_AXIS
                )
        );

        resultsPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        20,
                        20,
                        20
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(resultsPanel);

        scrollPane.setBorder(null);

        add(
                scrollPane,
                BorderLayout.CENTER
        );
    }

    private void loadFlights() {

        String sql =
                "SELECT " +
                        "f.FLIGHT_ID, " +
                        "f.FLIGHT_NUMBER, " +
                        "f.DEPARTURE_TIME, " +
                        "f.ARRIVAL_TIME, " +
                        "f.PRICE, " +
                        "a.AIRLINE_NAME, " +
                        "a.AIRLINE_CODE, " +
                        "src.CITY AS SOURCE_CITY, " +
                        "src.AIRPORT_CODE AS SOURCE_CODE, " +
                        "dest.CITY AS DESTINATION_CITY, " +
                        "dest.AIRPORT_CODE AS DESTINATION_CODE " +
                        "FROM FLIGHTS f " +
                        "JOIN AIRLINES a " +
                        "ON f.AIRLINE_ID = a.AIRLINE_ID " +
                        "JOIN AIRPORTS src " +
                        "ON f.SOURCE_AIRPORT_ID = src.AIRPORT_ID " +
                        "JOIN AIRPORTS dest " +
                        "ON f.DESTINATION_AIRPORT_ID = dest.AIRPORT_ID " +
                        "WHERE UPPER(src.CITY) = UPPER(?) " +
                        "AND UPPER(dest.CITY) = UPPER(?) " +
                        "AND TRUNC(f.DEPARTURE_TIME) = " +
                        "TO_DATE(?, 'DD-MM-YYYY') " +
                        "ORDER BY f.DEPARTURE_TIME";

        Connection con = null;
        CallableStatement call = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        int flightCount = 0;

        try {

            con = DBConnection.getConnection();

            if (con == null) {

                showNoFlights(
                        "Database connection failed."
                );

                return;
            }

            call = con.prepareCall(
                    "{call ENSURE_DAILY_FLIGHTS(?, ?, ?)}"
            );

            call.setString(
                    1,
                    fromCity
            );

            call.setString(
                    2,
                    toCity
            );

            String[] parts =
                    travelDate.split("-");

            if (parts.length != 3) {

                showNoFlights(
                        "Invalid travel date."
                );

                return;
            }

            LocalDate localDate;

            try {

                localDate = LocalDate.of(
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[0])
                );

            } catch (Exception ex) {

                showNoFlights(
                        "Invalid travel date."
                );

                return;
            }

            java.sql.Date sqlDate =
                    java.sql.Date.valueOf(localDate);

            call.setDate(
                    3,
                    sqlDate
            );

            call.execute();

            call.close();
            call = null;

            pst = con.prepareStatement(sql);

            pst.setString(
                    1,
                    fromCity
            );

            pst.setString(
                    2,
                    toCity
            );

            pst.setString(
                    3,
                    travelDate
            );

            rs = pst.executeQuery();

            while (rs.next()) {

                flightCount++;

                int flightId =
                        rs.getInt(
                                "FLIGHT_ID"
                        );

                String flightNumber =
                        rs.getString(
                                "FLIGHT_NUMBER"
                        );

                String airlineName =
                        rs.getString(
                                "AIRLINE_NAME"
                        );

                String airlineCode =
                        rs.getString(
                                "AIRLINE_CODE"
                        );

                String sourceCity =
                        rs.getString(
                                "SOURCE_CITY"
                        );

                String sourceCode =
                        rs.getString(
                                "SOURCE_CODE"
                        );

                String destinationCity =
                        rs.getString(
                                "DESTINATION_CITY"
                        );

                String destinationCode =
                        rs.getString(
                                "DESTINATION_CODE"
                        );

                String departure =
                        formatTime(
                                rs.getTimestamp(
                                        "DEPARTURE_TIME"
                                )
                        );

                String arrival =
                        formatTime(
                                rs.getTimestamp(
                                        "ARRIVAL_TIME"
                                )
                        );

                double basePrice =
                        rs.getDouble(
                                "PRICE"
                        );

                double finalPrice =
                        calculateDynamicPrice(
                                basePrice
                        );

                addFlightCard(
                        flightId,
                        airlineName,
                        airlineCode,
                        flightNumber,
                        sourceCity,
                        sourceCode,
                        destinationCity,
                        destinationCode,
                        departure,
                        arrival,
                        basePrice,
                        finalPrice
                );
            }

            if (flightCount == 0) {

                showNoFlights(
                        "No flights available for this route and date."
                );
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            showNoFlights(
                    "Unable to load flights.\n" +
                            ex.getMessage()
            );

        } finally {

            try {

                if (call != null) {
                    call.close();
                }

            } catch (Exception ignored) {
            }

            try {

                if (rs != null) {
                    rs.close();
                }

            } catch (Exception ignored) {
            }

            try {

                if (pst != null) {
                    pst.close();
                }

            } catch (Exception ignored) {
            }

            try {

                if (con != null) {
                    con.close();
                }

            } catch (Exception ignored) {
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void addFlightCard(
            int flightId,
            String airlineName,
            String airlineCode,
            String flightNumber,
            String sourceCity,
            String sourceCode,
            String destinationCity,
            String destinationCode,
            String departure,
            String arrival,
            double basePrice,
            double finalPrice
    ) {

        JPanel card =
                new JPanel(
                        new BorderLayout()
                );

        card.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        155
                )
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        210,
                                        210,
                                        210
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                15,
                                18,
                                15,
                                18
                        )
                )
        );

        card.setBackground(Color.WHITE);

        JPanel left =
                new JPanel(
                        new GridLayout(
                                3,
                                1
                        )
                );

        left.setBackground(Color.WHITE);

        JLabel airline =
                new JLabel(
                        airlineName +
                                " (" +
                                airlineCode +
                                ")"
                );

        airline.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        17
                )
        );

        JLabel flight =
                new JLabel(
                        "Flight " +
                                flightNumber
                );

        flight.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        JLabel route =
                new JLabel(
                        sourceCity +
                                " (" +
                                sourceCode +
                                ")" +
                                "  →  " +
                                destinationCity +
                                " (" +
                                destinationCode +
                                ")"
                );

        route.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        left.add(airline);
        left.add(flight);
        left.add(route);

        JPanel middle =
                new JPanel(
                        new GridLayout(
                                2,
                                1
                        )
                );

        middle.setBackground(Color.WHITE);

        JLabel timing =
                new JLabel(
                        departure +
                                "  →  " +
                                arrival,
                        SwingConstants.CENTER
                );

        timing.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        17
                )
        );

        JLabel classLabel =
                new JLabel(
                        "Economy",
                        SwingConstants.CENTER
                );

        classLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        middle.add(timing);
        middle.add(classLabel);

        JPanel right =
                new JPanel(
                        new GridLayout(
                                3,
                                1
                        )
                );

        right.setBackground(Color.WHITE);

        JLabel priceLabel =
                new JLabel(
                        "₹" +
                                String.format(
                                        "%.0f",
                                        finalPrice
                                ),
                        SwingConstants.CENTER
                );

        priceLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        JLabel baseLabel =
                new JLabel(
                        "Base ₹" +
                                String.format(
                                        "%.0f",
                                        basePrice
                                ),
                        SwingConstants.CENTER
                );

        baseLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );

        JButton selectButton =
                new JButton(
                        "SELECT FLIGHT"
                );

        selectButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        selectButton.setFocusPainted(false);

        selectButton.addActionListener(e -> {

            try {

                new SeatSelection(
                        userId,
                        userName,
                        flightId,
                        flightNumber,
                        airlineName,
                        finalPrice
                ).setVisible(true);

                dispose();

            } catch (Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to open Seat Selection.\n" +
                                ex.getMessage(),
                        "Seat Selection Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        right.add(priceLabel);
        right.add(baseLabel);
        right.add(selectButton);

        card.add(
                left,
                BorderLayout.WEST
        );

        card.add(
                middle,
                BorderLayout.CENTER
        );

        card.add(
                right,
                BorderLayout.EAST
        );

        resultsPanel.add(card);

        resultsPanel.add(
                Box.createVerticalStrut(
                        12
                )
        );
    }

    private double calculateDynamicPrice(
            double basePrice
    ) {

        try {

            String[] parts =
                    travelDate.split("-");

            int day =
                    Integer.parseInt(
                            parts[0]
                    );

            int month =
                    Integer.parseInt(
                            parts[1]
                    );

            int year =
                    Integer.parseInt(
                            parts[2]
                    );

            LocalDate travel =
                    LocalDate.of(
                            year,
                            month,
                            day
                    );

            LocalDate today =
                    LocalDate.now();

            long days =
                    ChronoUnit.DAYS.between(
                            today,
                            travel
                    );

            if (days >= 60) {
                return basePrice;
            }

            if (days >= 30) {
                return basePrice * 1.10;
            }

            if (days >= 15) {
                return basePrice * 1.20;
            }

            if (days >= 7) {
                return basePrice * 1.40;
            }

            if (days >= 3) {
                return basePrice * 1.60;
            }

            if (days >= 1) {
                return basePrice * 1.80;
            }

            return basePrice * 2.00;

        } catch (Exception e) {

            return basePrice;
        }
    }

    private String formatTime(
            java.sql.Timestamp timestamp
    ) {

        if (timestamp == null) {
            return "--";
        }

        java.time.LocalTime time =
                timestamp
                        .toLocalDateTime()
                        .toLocalTime();

        return time.format(
                java.time.format.DateTimeFormatter
                        .ofPattern("hh:mm a")
        );
    }

    private void showNoFlights(
            String message
    ) {

        resultsPanel.removeAll();

        JLabel label =
                new JLabel(
                        message,
                        SwingConstants.CENTER
                );

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        JPanel panel =
                new JPanel(
                        new GridBagLayout()
                );

        panel.setPreferredSize(
                new Dimension(
                        900,
                        250
                )
        );

        panel.add(label);

        resultsPanel.add(panel);

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new FlightResults(
                    1,
                    "Satyam",
                    "Patna",
                    "Delhi",
                    "10-10-2026",
                    2
            ).setVisible(true);

        });
    }
}