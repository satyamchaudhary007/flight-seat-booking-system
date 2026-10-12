package gui;

import javax.swing.*;
import java.awt.*;

public class PassengerDetails extends JFrame {

    private final int userId;
    private final String userName;
    private final int flightId;
    private final String flightNumber;
    private final String airlineName;
    private final double flightPrice;
    private final int selectedSeatId;
    private final String selectedSeatNumber;

    private JTextField fullNameField;
    private JTextField ageField;
    private JTextField phoneField;
    private JComboBox<String> genderBox;

    public PassengerDetails(
            int userId,
            String userName,
            int flightId,
            String flightNumber,
            String airlineName,
            double flightPrice,
            int selectedSeatId,
            String selectedSeatNumber
    ) {

        this.userId = userId;
        this.userName = userName;
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airlineName = airlineName;
        this.flightPrice = flightPrice;
        this.selectedSeatId = selectedSeatId;
        this.selectedSeatNumber = selectedSeatNumber;

        setTitle("SkySeat - Passenger Details");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        createHeader();
        createForm();
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

    private void createForm() {

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
                new JLabel("Passenger Details");

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        JLabel info =
                new JLabel(
                        airlineName
                                + " | Flight "
                                + flightNumber
                                + " | Seat "
                                + selectedSeatNumber
                                + " | ₹"
                                + String.format(
                                "%.0f",
                                flightPrice
                        )
                );

        info.setFont(
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
        titlePanel.add(info);

        main.add(
                titlePanel,
                BorderLayout.NORTH
        );

        JPanel form =
                new JPanel(
                        new GridBagLayout()
                );

        form.setBackground(Color.WHITE);

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        10,
                        10,
                        10,
                        10
                );

        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel =
                new JLabel("Full Name");

        fullNameField =
                new JTextField();

        JLabel ageLabel =
                new JLabel("Age");

        ageField =
                new JTextField();

        JLabel genderLabel =
                new JLabel("Gender");

        genderBox =
                new JComboBox<>(
                        new String[]{
                                "Male",
                                "Female",
                                "Other"
                        }
                );

        JLabel phoneLabel =
                new JLabel("Phone Number");

        phoneField =
                new JTextField();

        addFormRow(
                form,
                gbc,
                0,
                nameLabel,
                fullNameField
        );

        addFormRow(
                form,
                gbc,
                1,
                ageLabel,
                ageField
        );

        addFormRow(
                form,
                gbc,
                2,
                genderLabel,
                genderBox
        );

        addFormRow(
                form,
                gbc,
                3,
                phoneLabel,
                phoneField
        );

        main.add(
                form,
                BorderLayout.CENTER
        );

        JPanel bottom =
                new JPanel(
                        new BorderLayout()
                );

        bottom.setBackground(Color.WHITE);

        JLabel required =
                new JLabel(
                        "Please enter passenger details carefully."
                );

        required.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        JButton continueButton =
                new JButton(
                        "CONTINUE TO PAYMENT"
                );

        continueButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        continueButton.setFocusPainted(false);

        continueButton.addActionListener(
                e -> continueToPayment()
        );

        bottom.add(
                required,
                BorderLayout.WEST
        );

        bottom.add(
                continueButton,
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

    private void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            JLabel label,
            Component field
    ) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;

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
        gbc.weightx = 0.8;

        if (field instanceof JTextField) {

            field.setPreferredSize(
                    new Dimension(
                            400,
                            38
                    )
            );
        } else {

            field.setPreferredSize(
                    new Dimension(
                            400,
                            38
                    )
            );
        }

        panel.add(
                field,
                gbc
        );
    }

    private void continueToPayment() {

        String fullName =
                fullNameField.getText().trim();

        String ageText =
                ageField.getText().trim();

        String phone =
                phoneField.getText().trim();

        String gender =
                (String) genderBox.getSelectedItem();

        if (fullName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter passenger name.",
                    "Required",
                    JOptionPane.WARNING_MESSAGE
            );

            fullNameField.requestFocus();
            return;
        }

        if (ageText.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter passenger age.",
                    "Required",
                    JOptionPane.WARNING_MESSAGE
            );

            ageField.requestFocus();
            return;
        }

        int age;

        try {

            age = Integer.parseInt(ageText);

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Age must be a valid number.",
                    "Invalid Age",
                    JOptionPane.WARNING_MESSAGE
            );

            ageField.requestFocus();
            return;
        }

        if (age < 1 || age > 120) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid age between 1 and 120.",
                    "Invalid Age",
                    JOptionPane.WARNING_MESSAGE
            );

            ageField.requestFocus();
            return;
        }

        if (phone.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter phone number.",
                    "Required",
                    JOptionPane.WARNING_MESSAGE
            );

            phoneField.requestFocus();
            return;
        }

        if (!phone.matches("\\d{10}")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Phone number must contain exactly 10 digits.",
                    "Invalid Phone",
                    JOptionPane.WARNING_MESSAGE
            );

            phoneField.requestFocus();
            return;
        }

        new Payment(
                userId,
                userName,
                flightId,
                flightNumber,
                airlineName,
                flightPrice,
                selectedSeatId,
                selectedSeatNumber,
                fullName,
                age,
                gender,
                phone
        ).setVisible(true);

        dispose();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new PassengerDetails(
                    1,
                    "Satyam",
                    1,
                    "6E-123",
                    "IndiGo",
                    6500,
                    1,
                    "1A"
            ).setVisible(true);

        });
    }
}
