package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SeatSelection extends JFrame {

    private final int userId;
    private final String userName;
    private final int flightId;
    private final String flightNumber;
    private final String airlineName;
    private final double flightPrice;

    private JPanel seatPanel;
    private JLabel selectedSeatLabel;
    private int selectedSeatId = -1;
    private String selectedSeatNumber = "";

    public SeatSelection(
            int userId,
            String userName,
            int flightId,
            String flightNumber,
            String airlineName,
            double flightPrice
    ) {

        this.userId = userId;
        this.userName = userName;
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airlineName = airlineName;
        this.flightPrice = flightPrice;

        setTitle("SkySeat - Select Seat");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        createHeader();
        createContent();

        loadSeats();
    }

    private void createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 55, 100));
        header.setPreferredSize(new Dimension(1100, 70));

        JLabel logo = new JLabel("  SKYSEAT");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel user = new JLabel(
                "Welcome, " + userName + "    "
        );
        user.setForeground(Color.WHITE);
        user.setFont(new Font("Arial", Font.BOLD, 15));

        header.add(logo, BorderLayout.WEST);
        header.add(user, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void createContent() {

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        30,
                        20,
                        30
                )
        );

        JPanel topPanel = new JPanel();
        topPanel.setLayout(
                new BoxLayout(
                        topPanel,
                        BoxLayout.Y_AXIS
                )
        );
        topPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Select Your Seat");
        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        JLabel flightInfo = new JLabel(
                airlineName
                        + "  |  Flight "
                        + flightNumber
                        + "  |  ₹"
                        + String.format(
                        "%.0f",
                        flightPrice
                )
        );

        flightInfo.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        16
                )
        );

        topPanel.add(title);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(flightInfo);
        topPanel.add(Box.createVerticalStrut(15));

        JPanel legend = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        15,
                        5
                )
        );

        legend.setBackground(Color.WHITE);

        legend.add(createLegend(
                "Available",
                new Color(235, 245, 255)
        ));

        legend.add(createLegend(
                "Selected",
                new Color(20, 55, 100)
        ));

        legend.add(createLegend(
                "Booked",
                new Color(190, 190, 190)
        ));

        topPanel.add(legend);

        mainPanel.add(
                topPanel,
                BorderLayout.NORTH
        );

        seatPanel = new JPanel();
        seatPanel.setBackground(
                new Color(248, 249, 250)
        );

        JScrollPane scrollPane =
                new JScrollPane(seatPanel);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 220, 220)
                )
        );

        mainPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        JPanel bottomPanel =
                new JPanel(
                        new BorderLayout()
                );

        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        0,
                        0,
                        0
                )
        );

        selectedSeatLabel =
                new JLabel(
                        "Selected Seat: None"
                );

        selectedSeatLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );

        JButton continueButton =
                new JButton(
                        "CONTINUE"
                );

        continueButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        continueButton.setFocusPainted(false);

        continueButton.addActionListener(e -> {

            if (selectedSeatId == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please select a seat first.",
                        "Seat Required",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            new PassengerDetails(
                    userId,
                    userName,
                    flightId,
                    flightNumber,
                    airlineName,
                    flightPrice,
                    selectedSeatId,
                    selectedSeatNumber
            ).setVisible(true);

            dispose();
        });

        bottomPanel.add(
                selectedSeatLabel,
                BorderLayout.WEST
        );

        bottomPanel.add(
                continueButton,
                BorderLayout.EAST
        );

        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        add(
                mainPanel,
                BorderLayout.CENTER
        );
    }

    private JPanel createLegend(
            String text,
            Color color
    ) {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                5,
                                2
                        )
                );

        panel.setBackground(Color.WHITE);

        JLabel box = new JLabel("   ");
        box.setOpaque(true);
        box.setBackground(color);
        box.setBorder(
                BorderFactory.createLineBorder(
                        new Color(160, 160, 160)
                )
        );

        JLabel label =
                new JLabel(text);

        panel.add(box);
        panel.add(label);

        return panel;
    }

    private void loadSeats() {

        String sql =
                "SELECT SEAT_ID, SEAT_NUMBER, SEAT_CLASS, STATUS " +
                        "FROM SEATS " +
                        "WHERE FLIGHT_ID = ? " +
                        "ORDER BY SEAT_ID";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {

            con = DBConnection.getConnection();

            if (con == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Database connection failed.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            pst = con.prepareStatement(sql);
            pst.setInt(1, flightId);

            rs = pst.executeQuery();

            seatPanel.removeAll();

            JPanel aircraftPanel =
                    new JPanel();

            aircraftPanel.setBackground(
                    new Color(248, 249, 250)
            );

            aircraftPanel.setBorder(
                    BorderFactory.createEmptyBorder(
                            25,
                            30,
                            25,
                            30
                    )
            );

            aircraftPanel.setLayout(
                    new BoxLayout(
                            aircraftPanel,
                            BoxLayout.Y_AXIS
                    )
            );

            JLabel cockpit =
                    new JLabel(
                            "        ✈  COCKPIT"
                    );

            cockpit.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            18
                    )
            );

            cockpit.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            aircraftPanel.add(cockpit);
            aircraftPanel.add(
                    Box.createVerticalStrut(20)
            );

            JPanel rowPanel = null;
            int column = 0;
            boolean hasSeats = false;

            while (rs.next()) {

                hasSeats = true;

                int seatId =
                        rs.getInt("SEAT_ID");

                String seatNumber =
                        rs.getString("SEAT_NUMBER");

                String seatClass =
                        rs.getString("SEAT_CLASS");

                String status =
                        rs.getString("STATUS");

                if (column == 0) {

                    rowPanel =
                            new JPanel(
                                    new FlowLayout(
                                            FlowLayout.CENTER,
                                            8,
                                            5
                                    )
                            );

                    rowPanel.setBackground(
                            new Color(248, 249, 250)
                    );

                    JLabel rowNumber =
                            new JLabel(
                                    getRowNumber(
                                            seatNumber
                                    ),
                                    SwingConstants.CENTER
                            );

                    rowNumber.setPreferredSize(
                            new Dimension(
                                    35,
                                    40
                            )
                    );

                    rowNumber.setFont(
                            new Font(
                                    "Arial",
                                    Font.BOLD,
                                    12
                            )
                    );

                    rowPanel.add(rowNumber);
                }

                JButton seatButton =
                        createSeatButton(
                                seatId,
                                seatNumber,
                                seatClass,
                                status
                        );

                rowPanel.add(seatButton);

                column++;

                if (column == 3) {

                    JLabel aisle =
                            new JLabel("    ");

                    aisle.setPreferredSize(
                            new Dimension(
                                    18,
                                    40
                            )
                    );

                    rowPanel.add(aisle);
                }

                if (column == 6) {

                    aircraftPanel.add(rowPanel);
                    column = 0;
                }
            }

            if (column != 0 && rowPanel != null) {
                aircraftPanel.add(rowPanel);
            }

            if (!hasSeats) {

                JLabel noSeats =
                        new JLabel(
                                "No seats available for this flight."
                        );

                noSeats.setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                18
                        )
                );

                aircraftPanel.add(noSeats);
            }

            seatPanel.setLayout(
                    new BorderLayout()
            );

            seatPanel.add(
                    aircraftPanel,
                    BorderLayout.CENTER
            );

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load seats.\n"
                            + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

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

        seatPanel.revalidate();
        seatPanel.repaint();
    }

    private JButton createSeatButton(
            int seatId,
            String seatNumber,
            String seatClass,
            String status
    ) {

        JButton button =
                new JButton(
                        "<html><center>"
                                + seatNumber
                                + "<br>"
                                + getSeatType(
                                seatNumber
                        )
                                + "</center></html>"
                );

        button.setPreferredSize(
                new Dimension(
                        72,
                        48
                )
        );

        button.setFocusPainted(false);
        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        11
                )
        );

        boolean booked =
                status != null
                        && status.equalsIgnoreCase(
                        "BOOKED"
                );

        if (booked) {

            button.setEnabled(false);
            button.setBackground(
                    new Color(
                            190,
                            190,
                            190
                    )
            );

        } else {

            button.setBackground(
                    new Color(
                            235,
                            245,
                            255
                    )
            );

            button.addActionListener(e -> {

                selectedSeatId = seatId;
                selectedSeatNumber = seatNumber;

                selectedSeatLabel.setText(
                        "Selected Seat: "
                                + seatNumber
                                + "  |  "
                                + seatClass
                );

                updateSelectedSeat();

                button.setBackground(
                        new Color(
                                20,
                                55,
                                100
                        )
                );

                button.setForeground(
                        Color.WHITE
                );
            });
        }

        return button;
    }

    private void updateSelectedSeat() {

        Component[] components =
                getAllComponents(
                        seatPanel
                );

        for (Component component : components) {

            if (component instanceof JButton) {

                JButton button =
                        (JButton) component;

                if (button.isEnabled()
                        && !button
                        .getText()
                        .contains(
                                selectedSeatNumber
                        )) {

                    button.setBackground(
                            new Color(
                                    235,
                                    245,
                                    255
                            )
                    );

                    button.setForeground(
                            Color.BLACK
                    );
                }
            }
        }
    }

    private Component[] getAllComponents(
            Container container
    ) {

        java.util.List<Component> list =
                new java.util.ArrayList<>();

        for (Component component :
                container.getComponents()) {

            list.add(component);

            if (component instanceof Container) {

                Component[] children =
                        getAllComponents(
                                (Container) component
                        );

                for (Component child :
                        children) {

                    list.add(child);
                }
            }
        }

        return list.toArray(
                new Component[0]
        );
    }

    private String getSeatType(
            String seatNumber
    ) {

        if (seatNumber == null
                || seatNumber.isEmpty()) {

            return "";
        }

        String letter =
                seatNumber.substring(
                        seatNumber.length() - 1
                ).toUpperCase();

        if (letter.equals("A")
                || letter.equals("F")) {

            return "Window";
        }

        if (letter.equals("B")
                || letter.equals("E")) {

            return "Middle";
        }

        return "Aisle";
    }

    private String getRowNumber(
            String seatNumber
    ) {

        if (seatNumber == null
                || seatNumber.isEmpty()) {

            return "";
        }

        String row =
                seatNumber.replaceAll(
                        "[^0-9]",
                        ""
                );

        return row;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new SeatSelection(
                    1,
                    "Satyam",
                    1,
                    "6E-123",
                    "IndiGo",
                    6500
            ).setVisible(true);

        });
    }
}
