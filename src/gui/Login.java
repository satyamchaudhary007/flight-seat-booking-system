
package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login extends JFrame {

    private final Color NAVY = new Color(8, 38, 76);
    private final Color BLUE = new Color(0, 91, 190);
    private final Color CYAN = new Color(40, 210, 255);
    private final Color WHITE = Color.WHITE;
    private final Color MUTED = new Color(225, 235, 248);
    private final Color PLACEHOLDER = new Color(205, 218, 237);

    private PlaceholderTextField emailField;
    private PlaceholderPasswordField passwordField;
    private boolean passwordVisible = false;

    public Login() {
        setTitle("SkySeat - Login");
        setSize(1000, 700);
        setMinimumSize(new Dimension(850, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BackgroundPanel background = new BackgroundPanel();
        background.setLayout(new BorderLayout());

        JPanel card = new GlassCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(24, 42, 22, 42));

        IconView planeIcon = new IconView("plane", 42);
        planeIcon.setForeground(CYAN);
        planeIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Welcome Back! Login to continue");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 17));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        emailField = new PlaceholderTextField(
                "Enter your email or Admin ID");
        styleField(emailField);

        passwordField = new PlaceholderPasswordField(
                "Enter your password");
        styleField(passwordField);

        JPanel emailRow = createInputRow("email", emailField);
        JPanel passwordRow = createInputRow("lock", passwordField);

        JButton eyeButton = new JButton();
        eyeButton.setToolTipText("Show password");
        eyeButton.setPreferredSize(new Dimension(35, 38));
        eyeButton.setBorderPainted(false);
        eyeButton.setContentAreaFilled(false);
        eyeButton.setFocusPainted(false);
        eyeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eyeButton.setLayout(new GridBagLayout());

        IconView eyeIcon = new IconView("eye", 22);
        eyeIcon.setForeground(WHITE);
        eyeButton.add(eyeIcon);

        passwordRow.add(eyeButton, BorderLayout.EAST);

        eyeButton.addActionListener(e -> {
            passwordVisible = !passwordVisible;
            passwordField.setPasswordVisible(passwordVisible);

            eyeButton.setToolTipText(
                    passwordVisible ? "Hide password" : "Show password");
        });

        JButton loginButton = createButton("LOGIN NOW   →");
        JButton registerButton = createLink("Register Now");
        JButton backButton = createLink("← Back");

        JLabel accountText = new JLabel("Don't have an account?");
        accountText.setForeground(MUTED);
        accountText.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel registerRow = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerRow.setOpaque(false);
        registerRow.add(accountText);
        registerRow.add(registerButton);
        registerRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(planeIcon);
        card.add(Box.createVerticalStrut(5));
        card.add(title);
        card.add(Box.createVerticalStrut(7));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(27));
        card.add(emailRow);
        card.add(Box.createVerticalStrut(20));
        card.add(passwordRow);
        card.add(Box.createVerticalStrut(28));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(17));
        card.add(registerRow);
        card.add(Box.createVerticalStrut(8));
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

        loginButton.addActionListener(e -> loginUser());

        passwordField.addActionListener(e -> loginUser());

        registerButton.addActionListener(e -> {
            new Register().setVisible(true);
            dispose();
        });

        backButton.addActionListener(e -> {
            new WelcomeFrame().setVisible(true);
            dispose();
        });

        setContentPane(background);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setForeground(WHITE);
        field.setCaretColor(WHITE);
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(8, 8, 8, 8));
        field.setPreferredSize(new Dimension(350, 42));
    }

    private class PlaceholderTextField extends JTextField {

        private final String placeholder;

        PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
            setForeground(WHITE);
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

    private class PlaceholderPasswordField extends JPasswordField {

        private final String placeholder;
        private boolean passwordVisible = false;

        PlaceholderPasswordField(String placeholder) {
            this.placeholder = placeholder;
            setForeground(WHITE);
            setEchoChar('•');
        }

        void setPasswordVisible(boolean visible) {
            passwordVisible = visible;
            setEchoChar(visible ? (char) 0 : '•');
            repaint();
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

    private JPanel createInputRow(String iconType, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(500, 48));
        row.setPreferredSize(new Dimension(410, 48));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.setBorder(BorderFactory.createMatteBorder(
                0, 0, 2, 0, new Color(225, 238, 255)));

        IconView icon = new IconView(iconType, 23);
        icon.setForeground(WHITE);
        icon.setPreferredSize(new Dimension(30, 38));

        JPanel iconHolder = new JPanel(new GridBagLayout());
        iconHolder.setOpaque(false);
        iconHolder.add(icon);

        row.add(iconHolder, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);

        return row;
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

        button.setFont(new Font("Arial", Font.BOLD, 17));
        button.setForeground(WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(500, 58));
        button.setPreferredSize(new Dimension(410, 58));

        return button;
    }

    private JButton createLink(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(CYAN);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
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
                g2.drawLine(x + s / 2, y + s / 2 + 2,
                        x + s - 3, y + 6);
            } else if ("lock".equals(type)) {
                g2.drawRoundRect(x + 4, y + s / 2 - 1,
                        s - 8, s / 2 - 2, 3, 3);
                g2.drawRoundRect(x + 7, y + 3, s - 14,
                        s / 2 + 3, 7, 7);
                g2.drawLine(x + s / 2, y + s / 2 + 4,
                        x + s / 2, y + s - 5);
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
            g2.drawRoundRect(
                    0, 0, getWidth() - 1, getHeight() - 1, 28, 28);

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
                URL resource = Login.class.getResource("/images/login.png");

                if (resource != null) {
                    backgroundImage = javax.imageio.ImageIO.read(resource);
                } else {
                    File file = new File("src/images/login.png");

                    if (file.exists()) {
                        backgroundImage = javax.imageio.ImageIO.read(file);
                    }
                }

                if (backgroundImage == null) {
                    System.err.println(
                            "Background not found: src/images/login.png");
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
                g2.drawImage(backgroundImage, 0, 0,
                        getWidth(), getHeight(), this);
            } else {
                g2.setPaint(new GradientPaint(
                        0, 0, NAVY, getWidth(), getHeight(),
                        new Color(20, 100, 170)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            g2.setColor(new Color(3, 24, 55, 95));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private void loginUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your email/admin ID and password.",
                    "Login Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ((email.equalsIgnoreCase("admin")
                || email.equalsIgnoreCase("admin@skyseat.com"))
                && password.equals("admin123")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Welcome Admin!",
                    "Admin Login",
                    JOptionPane.INFORMATION_MESSAGE);

            new AdminDashboard().setVisible(true);
            dispose();
            return;
        }

        String sql = "SELECT USER_ID, NAME FROM USERS "
                + "WHERE LOWER(TRIM(EMAIL)) = LOWER(TRIM(?)) "
                + "AND PASSWORD = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("USER_ID");
                    String userName = rs.getString("NAME");

                    JOptionPane.showMessageDialog(
                            this,
                            "Login Successful!",
                            "Welcome to SkySeat",
                            JOptionPane.INFORMATION_MESSAGE);

                    new UserDashboard(userId, userName).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Invalid email or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database Error:\n" + ex.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new Login().setVisible(true));
    }
}
