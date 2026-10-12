package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Profile extends JFrame {

    private final int userId;
    private final String userName;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;

    public Profile(int userId, String userName) {

        this.userId = userId;
        this.userName = userName;

        setTitle("SkySeat - My Profile");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        createHeader();
        createProfilePanel();
        loadProfile();
    }

    private void createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 55, 100));
        header.setPreferredSize(new Dimension(900, 70));

        JLabel logo = new JLabel("  SKYSEAT");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel title = new JLabel("My Profile    ");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 17));

        header.add(logo, BorderLayout.WEST);
        header.add(title, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void createProfilePanel() {

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createTitledBorder("Personal Information"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Name");
        JLabel emailLabel = new JLabel("Email");
        JLabel phoneLabel = new JLabel("Phone");

        nameField = new JTextField(25);
        emailField = new JTextField(25);
        phoneField = new JTextField(25);

        emailField.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        card.add(nameLabel, gbc);

        gbc.gridx = 1;
        card.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        card.add(emailLabel, gbc);

        gbc.gridx = 1;
        card.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        card.add(phoneLabel, gbc);

        gbc.gridx = 1;
        card.add(phoneField, gbc);

        JButton saveButton = new JButton("SAVE CHANGES");
        JButton bookingsButton = new JButton("MY BOOKINGS");
        JButton passwordButton = new JButton("CHANGE PASSWORD");
        JButton closeButton = new JButton("CLOSE");

        saveButton.addActionListener(e -> updateProfile());
        bookingsButton.addActionListener(e ->
                new MyBookings(userId, userName).setVisible(true)
        );
        passwordButton.addActionListener(e -> changePassword());
        closeButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        buttons.add(saveButton);
        buttons.add(bookingsButton);
        buttons.add(passwordButton);
        buttons.add(closeButton);

        main.add(card, BorderLayout.CENTER);
        main.add(buttons, BorderLayout.SOUTH);

        add(main, BorderLayout.CENTER);
    }

    private void loadProfile() {

        String sql =
                "SELECT NAME, EMAIL, PHONE FROM USERS WHERE USER_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, userId);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    nameField.setText(rs.getString("NAME"));
                    emailField.setText(rs.getString("EMAIL"));
                    phoneField.setText(
                            rs.getString("PHONE") == null
                                    ? ""
                                    : rs.getString("PHONE")
                    );
                }
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load profile.\n" + ex.getMessage(),
                    "Profile Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateProfile() {

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Name cannot be empty.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql =
                "UPDATE USERS SET NAME = ?, PHONE = ? WHERE USER_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, name);
            pst.setString(2, phone.isEmpty() ? null : phone);
            pst.setInt(3, userId);

            int updated = pst.executeUpdate();

            if (updated > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Profile updated successfully.",
                        "SkySeat",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to update profile.\n" + ex.getMessage(),
                    "Profile Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void changePassword() {

        JPasswordField oldPassword = new JPasswordField();
        JPasswordField newPassword = new JPasswordField();
        JPasswordField confirmPassword = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));

        panel.add(new JLabel("Current Password"));
        panel.add(oldPassword);

        panel.add(new JLabel("New Password"));
        panel.add(newPassword);

        panel.add(new JLabel("Confirm Password"));
        panel.add(confirmPassword);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Change Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String oldPass = new String(oldPassword.getPassword());
        String newPass = new String(newPassword.getPassword());
        String confirmPass = new String(confirmPassword.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Password fields cannot be empty.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (!newPass.equals(confirmPass)) {

            JOptionPane.showMessageDialog(
                    this,
                    "New password and confirm password do not match.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String checkSql =
                "SELECT COUNT(*) FROM USERS WHERE USER_ID = ? AND PASSWORD = ?";

        String updateSql =
                "UPDATE USERS SET PASSWORD = ? WHERE USER_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement check = con.prepareStatement(checkSql)) {

            check.setInt(1, userId);
            check.setString(2, oldPass);

            try (ResultSet rs = check.executeQuery()) {

                rs.next();

                if (rs.getInt(1) == 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Current password is incorrect.",
                            "Password Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    return;
                }
            }

            try (PreparedStatement update = con.prepareStatement(updateSql)) {

                update.setString(1, newPass);
                update.setInt(2, userId);

                update.executeUpdate();

                JOptionPane.showMessageDialog(
                        this,
                        "Password changed successfully.",
                        "SkySeat",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to change password.\n" + ex.getMessage(),
                    "Password Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new Profile(1, "Satyam").setVisible(true)
        );
    }
}
