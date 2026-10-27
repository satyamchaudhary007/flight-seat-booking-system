package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Register extends JFrame {

    private final Color NAVY = new Color(8, 38, 76);
    private final Color BLUE = new Color(0, 91, 190);
    private final Color CYAN = new Color(40, 210, 255);
    private final Color WHITE = Color.WHITE;
    private final Color MUTED = new Color(225, 235, 248);
    private final Color PLACEHOLDER = new Color(205, 218, 237);

    private PlaceholderField nameField;
    private PlaceholderField emailField;
    private PlaceholderField phoneField;
    private PlaceholderPassword passwordField;
    private PlaceholderPassword confirmPasswordField;

    public Register() {
        setTitle("SkySeat - Create Account");
        setSize(1000, 790);
        setMinimumSize(new Dimension(850, 720));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Window ko full screen (maximized) set karne ke liye
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        BackgroundPanel background = new BackgroundPanel();
        background.setLayout(new BorderLayout());

        JPanel card = new GlassCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 42, 18, 42));

        IconView planeIcon = new IconView("plane", 34);
        planeIcon.setForeground(CYAN);
        planeIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Join SkySeat and book your flights");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField = new PlaceholderField("Enter your full name");
        emailField = new PlaceholderField("Enter your email address");
        phoneField = new PlaceholderField("Enter your phone number");

        passwordField = new PlaceholderPassword("Create your password");
        confirmPasswordField =
                new PlaceholderPassword("Confirm your password");

        JPanel nameRow = createInputRow("user", nameField, null);
        JPanel emailRow = createInputRow("email", emailField, null);
        JPanel phoneRow = createInputRow("phone", phoneField, null);

        JButton passwordEye = createEyeButton(passwordField);
        JButton confirmEye = createEyeButton(confirmPasswordField);

        JPanel passwordRow =
                createInputRow("lock", passwordField, passwordEye);

        JPanel confirmPasswordRow =
                createInputRow("lock", confirmPasswordField, confirmEye);

        JButton registerButton = createButton("CREATE ACCOUNT   →");
        JButton loginButton = createLink("Login Now");
        JButton backButton = createLink("← Back to Welcome");

        JLabel accountText = new JLabel("Already have an account?");
        accountText.setForeground(MUTED);
        accountText.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel loginRow = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginRow.setOpaque(false);
        loginRow.add(accountText);
        loginRow.add(loginButton);
        loginRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(planeIcon);
        card.add(Box.createVerticalStrut(3));
        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(19));

        card.add(nameRow);
        card.add(Box.createVerticalStrut(12));

        card.add(emailRow);
        card.add(Box.createVerticalStrut(12));

        card.add(phoneRow);
        card.add(Box.createVerticalStrut(12));

        card.add(passwordRow);
        card.add(Box.createVerticalStrut(12));

        card.add(confirmPasswordRow);
        card.add(Box.createVerticalStrut(20));

        card.add(registerButton);
        card.add(Box.createVerticalStrut(12));
        card.add(loginRow);
        card.add(Box.createVerticalStrut(3));
        card.add(backButton);

        JPanel cardHolder = new JPanel(new GridBagLayout());
        cardHolder.setOpaque(false);
        cardHolder.setBorder(new EmptyBorder(12, 20, 12, 20));
        cardHolder.add(card);

        background.add(cardHolder, BorderLayout.CENTER);

        JLabel footer = new JLabel(
                "© 2026 SkySeat | Flight Seat Booking System",
                SwingConstants.CENTER);
        footer.setForeground(MUTED);
        footer.setFont(new Font("Arial", Font.PLAIN, 11));
        footer.setBorder(new EmptyBorder(0, 0, 12, 0));

        background.add(footer, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> register());
        confirmPasswordField.addActionListener(e -> register());

        loginButton.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });

        backButton.addActionListener(e -> {
            new WelcomeFrame().setVisible(true);
            dispose();
        });

        setContentPane(background);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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

    private JPanel createInputRow(
            String iconType, JTextField field, JButton eyeButton) {

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(500, 43));
        row.setPreferredSize(new Dimension(410, 43));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        row.setBorder(BorderFactory.createMatteBorder(
                0, 0, 2, 0, new Color(225, 238, 255)));

        IconView icon = new IconView(iconType, 22);
        icon.setForeground(WHITE);
        icon.setPreferredSize(new Dimension(30, 35));

        JPanel iconHolder = new JPanel(new GridBagLayout());
        iconHolder.setOpaque(false);
        iconHolder.add(icon);

        row.add(iconHolder, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);

        if (eyeButton != null) {
            row.add(eyeButton, BorderLayout.EAST);
        }

        return row;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setForeground(WHITE);
        field.setCaretColor(WHITE);
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(6, 5, 6, 5));
        field.setPreferredSize(new Dimension(350, 38));
    }

    private JButton createEyeButton(PlaceholderPassword field) {
        JButton button = new JButton();
        button.setToolTipText("Show password");
        button.setPreferredSize(new Dimension(35, 35));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setLayout(new GridBagLayout());

        IconView eye = new IconView("eye", 21);
        eye.setForeground(WHITE);
        button.add(eye);

        button.addActionListener(e -> {
            field.togglePasswordVisibility();
            button.setToolTipText(
                    field.isPasswordVisible()
                            ? "Hide password" : "Show password");
        });

        return button;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getModel().isRollover()
                        ? new Color(20, 125, 225) : BLUE);

                g2.fillRoundRect(
                        0, 0, getWidth(), getHeight(), 45, 45);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setForeground(WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(500, 48));
        button.setPreferredSize(new Dimension(410, 48));

        return button;
    }

    private JButton createLink(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(CYAN);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private class PlaceholderField extends JTextField {
        private final String placeholder;

        PlaceholderField(String placeholder) {
            this.placeholder = placeholder;
            styleField(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (getText().isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                g2.setColor(PLACEHOLDER);
                g2.setFont(getFont());

                Insets insets = getInsets();
                FontMetrics fm = g2.getFontMetrics();

                g2.drawString(
                        placeholder,
                        insets.left,
                        (getHeight() - fm.getHeight()) / 2
                                + fm.getAscent());

                g2.dispose();
            }
        }
    }

    private class PlaceholderPassword extends JPasswordField {
        private final String placeholder;
        private boolean visible = false;

        PlaceholderPassword(String placeholder) {
            this.placeholder = placeholder;
            styleField(this);
            setEchoChar('•');
        }

        void togglePasswordVisibility() {
            visible = !visible;
            setEchoChar(visible ? (char) 0 : '•');
            repaint();
        }

        boolean isPasswordVisible() {
            return visible;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (getPassword().length == 0) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                g2.setColor(PLACEHOLDER);
                g2.setFont(getFont());

                Insets insets = getInsets();
                FontMetrics fm = g2.getFontMetrics();

                g2.drawString(
                        placeholder,
                        insets.left,
                        (getHeight() - fm.getHeight()) / 2
                                + fm.getAscent());

                g2.dispose();
            }
        }
    }

    private class IconView extends JComponent {
        private final String type;
        private final int iconSize;

        IconView(String type, int iconSize) {
            this.type = type;
            this.iconSize = iconSize;
            setPreferredSize(new Dimension(iconSize + 8, iconSize + 8));
            setMinimumSize(getPreferredSize());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getForeground());
            g2.setStroke(new BasicStroke(
                    1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int x = (getWidth() - iconSize) / 2;
            int y = (getHeight() - iconSize) / 2;
            int s = iconSize;

            if ("plane".equals(type)) {
                g2.drawLine(x + 3, y + s - 5, x + s - 3, y + 3);
                g2.drawLine(x + s - 3, y + 3, x + s - 7, y + s - 2);
                g2.drawLine(x + 3, y + s - 5, x + s - 7, y + s - 2);
                g2.drawLine(x + s / 2, y + s / 2, x + 4, y + s / 2 - 4);
                g2.drawLine(x + s / 2, y + s / 2, x + s / 2 - 4, y + s - 2);
            } else if ("email".equals(type)) {
                g2.drawRoundRect(x + 2, y + 5, s - 4, s - 10, 3, 3);
                g2.drawLine(x + 3, y + 6, x + s / 2, y + s / 2 + 2);
                g2.drawLine(x + s / 2, y + s / 2 + 2, x + s - 3, y + 6);
            } else if ("user".equals(type)) {
                g2.drawOval(x + s / 3, y + 2, s / 3, s / 3);
                g2.drawArc(x + 3, y + s / 2, s - 6, s / 2, 0, 180);
            } else if ("phone".equals(type)) {
                g2.drawRoundRect(x + 5, y + 2, s - 10, s - 4, 4, 4);
                g2.drawLine(x + 9, y + s - 5, x + s - 9, y + s - 5);
            } else if ("lock".equals(type)) {
                g2.drawRoundRect(x + 4, y + s / 2 - 1, s - 8, s / 2 - 2, 3, 3);
                g2.drawRoundRect(x + 7, y + 3, s - 14, s / 2 + 3, 7, 7);
                g2.drawLine(x + s / 2, y + s / 2 + 4, x + s / 2, y + s - 5);
            } else if ("eye".equals(type)) {
                g2.drawOval(x + 2, y + s / 4, s - 4, s / 2);
                g2.drawOval(x + s / 2 - 3, y + s / 2 - 3, 6, 6);
            }

            g2.dispose();
        }
    }

    private class GlassCard extends JPanel {
        GlassCard() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(7, 36, 76, 190));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);

            g2.setColor(new Color(180, 220, 255, 160));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 28, 28);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class BackgroundPanel extends JPanel {
        private BufferedImage backgroundImage;

        BackgroundPanel() {
            loadBackground();
        }

        private void loadBackground() {
            try {
                URL resource = Register.class.getResource("/images/register.png");

                if (resource != null) {
                    backgroundImage = javax.imageio.ImageIO.read(resource);
                } else {
                    File file = new File("src/images/register.png");

                    if (file.exists()) {
                        backgroundImage = javax.imageio.ImageIO.read(file);
                    }
                }

                if (backgroundImage == null) {
                    System.err.println("Background not found: src/images/register.png");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            if (backgroundImage != null) {
                g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g2.setPaint(new GradientPaint(
                        0, 0, NAVY, getWidth(), getHeight(), new Color(20, 100, 170)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            g2.setColor(new Color(3, 24, 55, 95));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private void register() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields.",
                    "Registration",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Password and Confirm Password do not match.",
                    "Registration",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection con = DBConnection.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database Connection Failed!\nPlease check your MySQL DB service or DBConnection.java credentials.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String sql = "INSERT INTO USERS "
                + "(NAME, EMAIL, PHONE, PASSWORD, CREATED_AT) "
                + "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, phone);
            pst.setString(4, password);

            int rows = pst.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Registration successful!",
                        "SkySeat",
                        JOptionPane.INFORMATION_MESSAGE);

                new Login().setVisible(true);
                dispose();
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(
                        this,
                        "Email already registered.",
                        "Registration",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Database Error:\n" + e.getMessage(),
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } finally {
            try {
                if (con != null) con.close();
            } catch (Exception ignored) {}
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Register register = new Register();
            register.setVisible(true);
        });
    }
}