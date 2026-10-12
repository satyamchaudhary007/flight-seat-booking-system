package gui;

import javax.swing.*;
import java.awt.*;

public class Payment extends JFrame {

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

    private JComboBox<String> paymentMethodBox;

    public Payment(
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
            String passengerPhone
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

        setTitle("SkySeat - Payment");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        createHeader();
        createPaymentPage();
    }

    private void createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 55, 100));
        header.setPreferredSize(new Dimension(900, 70));

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

    private void createPaymentPage() {

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);
        main.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        40,
                        25,
                        40
                )
        );

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS
                )
        );
        titlePanel.setBackground(Color.WHITE);

        JLabel title =
                new JLabel("Payment");

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        JLabel subtitle =
                new JLabel(
                        "Complete your payment to confirm the booking."
                );

        subtitle.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        titlePanel.add(title);
        titlePanel.add(
                Box.createVerticalStrut(8)
        );
        titlePanel.add(subtitle);

        main.add(
                titlePanel,
                BorderLayout.NORTH
        );

        JPanel center =
                new JPanel(
                        new GridBagLayout()
                );

        center.setBackground(Color.WHITE);

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        8,
                        10,
                        8,
                        10
                );

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        addInfoRow(
                center,
                gbc,
                0,
                "Airline",
                airlineName
        );

        addInfoRow(
                center,
                gbc,
                1,
                "Flight",
                flightNumber
        );

        addInfoRow(
                center,
                gbc,
                2,
                "Passenger",
                passengerName
        );

        addInfoRow(
                center,
                gbc,
                3,
                "Seat",
                selectedSeatNumber
        );

        addInfoRow(
                center,
                gbc,
                4,
                "Amount",
                "₹" + String.format(
                        "%.2f",
                        flightPrice
                )
        );

        JLabel methodLabel =
                new JLabel(
                        "Payment Method"
                );

        methodLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        paymentMethodBox =
                new JComboBox<>(
                        new String[]{
                                "UPI",
                                "Credit Card",
                                "Debit Card",
                                "Net Banking"
                        }
                );

        paymentMethodBox.setPreferredSize(
                new Dimension(
                        350,
                        38
                )
        );

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.25;

        center.add(
                methodLabel,
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 0.75;

        center.add(
                paymentMethodBox,
                gbc
        );

        main.add(
                center,
                BorderLayout.CENTER
        );

        JPanel bottom =
                new JPanel(
                        new BorderLayout()
                );

        bottom.setBackground(Color.WHITE);

        JLabel secure =
                new JLabel(
                        "Secure payment"
                );

        secure.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        JButton payButton =
                new JButton(
                        "PAY ₹"
                                + String.format(
                                "%.2f",
                                flightPrice
                        )
                );

        payButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        payButton.setFocusPainted(false);

        payButton.addActionListener(
                e -> processPayment()
        );

        bottom.add(
                secure,
                BorderLayout.WEST
        );

        bottom.add(
                payButton,
                BorderLayout.EAST
        );

        main.add(
                bottom,
                BorderLayout.SOUTH
        );

        add(
                main,
                BorderLayout.CENTER
        );
    }

    private void addInfoRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String labelText,
            String valueText
    ) {

        JLabel label =
                new JLabel(labelText);

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        JLabel value =
                new JLabel(valueText);

        value.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.25;

        panel.add(
                label,
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 0.75;

        panel.add(
                value,
                gbc
        );
    }

    private void processPayment() {

        String paymentMethod =
                (String) paymentMethodBox
                        .getSelectedItem();

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        "Confirm payment of ₹"
                                + String.format(
                                "%.2f",
                                flightPrice
                        )
                                + " using "
                                + paymentMethod
                                + "?",
                        "Confirm Payment",
                        JOptionPane.YES_NO_OPTION
                );

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        new BookingConfirmation(
                userId,
                userName,
                flightId,
                flightNumber,
                airlineName,
                flightPrice,
                selectedSeatId,
                selectedSeatNumber,
                passengerName,
                passengerAge,
                passengerGender,
                passengerPhone,
                paymentMethod
        ).setVisible(true);

        dispose();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new Payment(
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
                    "9876543210"
            ).setVisible(true);

        });
    }
}

