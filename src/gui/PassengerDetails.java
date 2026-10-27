package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

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

    private final Color NAVY_DARK = new Color(6, 22, 48);
    private final Color NAVY_BLUE = new Color(0, 68, 148);
    private final Color PRIMARY_BLUE = new Color(0, 102, 230);
    private final Color PRIMARY_BLUE_HOVER = new Color(0, 85, 200);
    private final Color CYAN_ACCENT = new Color(40, 210, 255);
    private final Color BG_LIGHT = new Color(228, 238, 250);
    private final Color CARD_BG = Color.WHITE;
    private final Color TEXT_DARK = new Color(20, 35, 60);
    private final Color TEXT_MUTED = new Color(120, 135, 155);
    private final Color BORDER_COLOR = new Color(205, 222, 242);
    private final Color FIELD_BG = new Color(250, 252, 255);
    private final Color ICON_BG = new Color(235, 244, 255);
    private final Color INFO_STRIP_BG = new Color(236, 244, 255);

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
        setSize(1280, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLayout(new BorderLayout());

        createHeader();
        createMainContent();
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

    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, NAVY_DARK, getWidth(), 0, new Color(10, 38, 78));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(1280, 75));
        header.setBorder(new EmptyBorder(0, 35, 0, 35));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 16));
        logoPanel.setOpaque(false);

        JLabel logoIcon = new JLabel(new ExactVectorIcon("logo_plane", 34, Color.WHITE));

        JPanel textStack = new JPanel(new GridLayout(2, 1, 0, 2));
        textStack.setOpaque(false);
        JLabel logoText = new JLabel("SKYSEAT");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 23));
        logoText.setForeground(Color.WHITE);

        JLabel logoSub = new JLabel("FLIGHT SEAT BOOKING SYSTEM");
        logoSub.setFont(new Font("Segoe UI", Font.BOLD, 9));
        logoSub.setForeground(CYAN_ACCENT);

        textStack.add(logoText);
        textStack.add(logoSub);

        logoPanel.add(logoIcon);
        logoPanel.add(textStack);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 22));
        userPanel.setOpaque(false);

        JLabel userAvatar = new JLabel(new ExactVectorIcon("user_circle", 24, Color.WHITE));
        JLabel userLabel = new JLabel("Welcome, " + userName + "  v");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);

        userPanel.add(userAvatar);
        userPanel.add(userLabel);

        header.add(logoPanel, BorderLayout.WEST);
        header.add(userPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void createMainContent() {
        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        backgroundPanel.setBackground(BG_LIGHT);
        backgroundPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Beech ka layout yahan BADA (Expanded) kar diya hai
        RoundedPanel card = new RoundedPanel(24, CARD_BG, BORDER_COLOR);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(1180, 640));

        LeftBannerPanel leftBanner = new LeftBannerPanel();
        leftBanner.setPreferredSize(new Dimension(360, 640));

        JPanel rightFormPanel = new JPanel(new BorderLayout(0, 15));
        rightFormPanel.setOpaque(false);
        rightFormPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        titlePanel.setOpaque(false);

        JLabel headerIconCircle = createIconCircle("users_group", PRIMARY_BLUE, Color.WHITE, 46);

        JPanel titleTextStack = new JPanel(new GridLayout(2, 1, 0, 2));
        titleTextStack.setOpaque(false);

        JLabel mainTitle = new JLabel("Passenger Details");
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        mainTitle.setForeground(TEXT_DARK);

        JLabel subTitle = new JLabel("Please provide the following information for all passengers.");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subTitle.setForeground(TEXT_MUTED);

        titleTextStack.add(mainTitle);
        titleTextStack.add(subTitle);

        titlePanel.add(headerIconCircle);
        titlePanel.add(titleTextStack);

        JPanel formStack = new JPanel();
        formStack.setLayout(new BoxLayout(formStack, BoxLayout.Y_AXIS));
        formStack.setOpaque(false);
        formStack.setBorder(new EmptyBorder(15, 0, 15, 0));

        fullNameField = createStyledTextField("Enter passenger full name");
        ageField = createStyledTextField("Enter age (e.g. 25)");

        genderBox = new JComboBox<>(new String[]{"Select Gender", "Male", "Female", "Other"});
        styleCustomComboBox(genderBox);

        phoneField = createStyledTextField("Enter 10 digit mobile number");

        formStack.add(createFormRow("user", "Full Name", fullNameField));
        formStack.add(Box.createVerticalStrut(14));
        formStack.add(createFormRow("calendar", "Age", ageField));
        formStack.add(Box.createVerticalStrut(14));
        formStack.add(createFormRow("gender", "Gender", genderBox));
        formStack.add(Box.createVerticalStrut(14));
        formStack.add(createFormRow("phone", "Phone Number", phoneField));

        JPanel bottomRow = new JPanel(new BorderLayout(15, 0));
        bottomRow.setOpaque(false);

        RoundedPanel infoStrip = new RoundedPanel(12, INFO_STRIP_BG, null);
        infoStrip.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));

        JLabel infoIcon = new JLabel(new ExactVectorIcon("info", 18, PRIMARY_BLUE));
        JLabel infoText = new JLabel("Please enter passenger details carefully.");
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoText.setForeground(TEXT_DARK);

        infoStrip.add(infoIcon);
        infoStrip.add(infoText);

        RoundedButton continueBtn = new RoundedButton("CONTINUE TO PAYMENT  →", PRIMARY_BLUE, PRIMARY_BLUE_HOVER, Color.WHITE, 12);
        continueBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        continueBtn.setPreferredSize(new Dimension(230, 42));
        continueBtn.addActionListener(e -> continueToPayment());

        bottomRow.add(infoStrip, BorderLayout.CENTER);
        bottomRow.add(continueBtn, BorderLayout.EAST);

        rightFormPanel.add(titlePanel, BorderLayout.NORTH);
        rightFormPanel.add(formStack, BorderLayout.CENTER);
        rightFormPanel.add(bottomRow, BorderLayout.SOUTH);

        card.add(leftBanner, BorderLayout.WEST);
        card.add(rightFormPanel, BorderLayout.CENTER);

        backgroundPanel.add(card);
        add(backgroundPanel, BorderLayout.CENTER);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FIELD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();

                super.paintComponent(g);

                if (getText().isEmpty()) {
                    Graphics2D g2Text = (Graphics2D) g.create();
                    g2Text.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2Text.setColor(new Color(170, 185, 205));
                    g2Text.setFont(getFont());
                    Insets insets = getInsets();
                    FontMetrics fm = g2Text.getFontMetrics();
                    g2Text.drawString(placeholder, insets.left, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                    g2Text.dispose();
                }
            }
        };

        field.setUI(new BasicTextFieldUI());
        field.setOpaque(false);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(TEXT_DARK);
        field.setCaretColor(TEXT_DARK);
        field.setPreferredSize(new Dimension(550, 40));
        field.setMaximumSize(new Dimension(800, 40));
        field.setBorder(new EmptyBorder(6, 12, 6, 12));

        return field;
    }

    private JPanel createFormRow(String iconType, String labelText, JComponent fieldComp) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(800, 65));

        JPanel iconWrapper = new JPanel(new GridBagLayout());
        iconWrapper.setOpaque(false);
        JLabel iconCircle = createIconCircle(iconType, ICON_BG, PRIMARY_BLUE, 40);
        iconWrapper.add(iconCircle);

        JPanel fieldStack = new JPanel(new BorderLayout(0, 4));
        fieldStack.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_DARK);

        fieldStack.add(label, BorderLayout.NORTH);
        fieldStack.add(fieldComp, BorderLayout.CENTER);

        row.add(iconWrapper, BorderLayout.WEST);
        row.add(fieldStack, BorderLayout.CENTER);

        return row;
    }

    private JLabel createIconCircle(String iconType, Color bg, Color fg, int diameter) {
        JLabel circle = new JLabel(new ExactVectorIcon(iconType, diameter / 2, fg), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);

                int d = Math.min(getWidth(), getHeight());
                int x = (getWidth() - d) / 2;
                int y = (getHeight() - d) / 2;

                g2.fillOval(x, y, d, d);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        circle.setPreferredSize(new Dimension(diameter, diameter));
        circle.setMinimumSize(new Dimension(diameter, diameter));
        circle.setMaximumSize(new Dimension(diameter, diameter));
        return circle;
    }

    private void styleCustomComboBox(JComboBox<String> combo) {
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(PRIMARY_BLUE);
                        int w = getWidth();
                        int h = getHeight();
                        int[] xPoints = {w / 2 - 4, w / 2 + 4, w / 2};
                        int[] yPoints = {h / 2 - 2, h / 2 - 2, h / 2 + 3};
                        g2.fillPolygon(xPoints, yPoints, 3);
                        g2.dispose();
                    }
                };
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                button.setOpaque(false);
                return button;
            }

            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FIELD_BG);
                g2.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 10, 10);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 10, 10);
                g2.dispose();
                super.paint(g, c);
            }
        });

        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setForeground(TEXT_DARK);
        combo.setOpaque(false);
        combo.setFocusable(false);
        combo.setPreferredSize(new Dimension(550, 40));
        combo.setMaximumSize(new Dimension(800, 40));
        combo.setBorder(new EmptyBorder(4, 10, 4, 10));
    }

    private class LeftBannerPanel extends JPanel {
        private BufferedImage bgImage;

        public LeftBannerPanel() {
            setOpaque(false);
            loadImage();
        }

        private void loadImage() {
            try {
                URL resource = PassengerDetails.class.getResource("/images/passengerdetails.jpg");
                if (resource != null) {
                    bgImage = javax.imageio.ImageIO.read(resource);
                } else {
                    File file = new File("src/images/passengerdetails.jpg");
                    if (file.exists()) {
                        bgImage = javax.imageio.ImageIO.read(file);
                    } else {
                        File altFile = new File("images/passengerdetails.jpg");
                        if (altFile.exists()) {
                            bgImage = javax.imageio.ImageIO.read(altFile);
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("Image Read Error: " + ex.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();

            Path2D clipPath = new Path2D.Double();
            clipPath.moveTo(0, 0);
            clipPath.lineTo(w - 55, 0);
            clipPath.curveTo(w - 20, h * 0.35, w - 95, h * 0.65, w - 12, h);
            clipPath.lineTo(0, h);
            clipPath.closePath();

            g2.setClip(clipPath);

            if (bgImage != null) {
                g2.drawImage(bgImage, 0, 0, w, h, this);
            } else {
                GradientPaint gp = new GradientPaint(0, 0, NAVY_BLUE, w, h, NAVY_DARK);
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);
            }

            g2.setColor(new Color(6, 32, 68, 140));
            g2.fillRect(0, 0, w, h);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 22));
            g2.drawString("Your Journey", 35, 90);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
            g2.setColor(CYAN_ACCENT);
            g2.drawString("Starts Here", 35, 128);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            g2.setColor(new Color(220, 235, 255));
            g2.drawString("Enter your details to", 35, 165);
            g2.drawString("complete your booking.", 35, 185);

            g2.setColor(CYAN_ACCENT);
            g2.fillRect(35, 212, 50, 3);

            g2.dispose();
        }
    }

    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;
        private final Color border;

        public RoundedPanel(int radius, Color bg, Color border) {
            this.radius = radius;
            this.bg = bg;
            this.border = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (bg != null) {
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            }

            if (border != null) {
                g2.setColor(border);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class RoundedButton extends JButton {
        private final Color normalBg;
        private final Color hoverBg;
        private final int radius;

        public RoundedButton(String text, Color bg, Color hoverBg, Color fg, int radius) {
            super(text);
            this.normalBg = bg;
            this.hoverBg = hoverBg;
            this.radius = radius;
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setForeground(fg);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { setBackground(hoverBg); repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { setBackground(normalBg); repaint(); }
            });
            setBackground(normalBg);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground() != null ? getBackground() : normalBg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class ExactVectorIcon implements Icon {
        private final String type;
        private final int size;
        private final Color color;

        public ExactVectorIcon(String type, int size, Color color) {
            this.type = type;
            this.size = size;
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color != null ? color : c.getForeground());
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int s = size;

            if ("logo_plane".equals(type) || "plane".equals(type)) {
                Path2D p = new Path2D.Double();
                p.moveTo(x + 2, y + s * 0.7);
                p.lineTo(x + s * 0.45, y + s * 0.55);
                p.lineTo(x + s * 0.35, y + s * 0.2);
                p.lineTo(x + s * 0.5, y + s * 0.15);
                p.lineTo(x + s * 0.7, y + s * 0.45);
                p.lineTo(x + s * 0.9, y + s * 0.4);
                p.lineTo(x + s, y + s * 0.5);
                p.lineTo(x + s * 0.8, y + s * 0.6);
                p.lineTo(x + s * 0.65, y + s * 0.85);
                p.lineTo(x + s * 0.55, y + s * 0.85);
                p.lineTo(x + s * 0.60, y + s * 0.65);
                p.closePath();
                g2.fill(p);
            } else if ("user".equals(type)) {
                g2.drawOval(x + s / 2 - 4, y + 2, 8, 8);
                g2.drawArc(x + 2, y + 10, s - 4, 10, 0, 180);
            } else if ("user_circle".equals(type)) {
                g2.drawOval(x + 1, y + 1, s - 2, s - 2);
                g2.drawOval(x + s / 2 - 3, y + 3, 6, 6);
                g2.drawArc(x + 4, y + 9, s - 8, 6, 0, 180);
            } else if ("users_group".equals(type)) {
                g2.drawOval(x + s / 2 - 4, y + 2, 8, 8);
                g2.drawArc(x + 2, y + 10, s - 4, 10, 0, 180);
            } else if ("calendar".equals(type)) {
                g2.drawRoundRect(x + 2, y + 4, s - 4, s - 6, 4, 4);
                g2.drawLine(x + 2, y + 9, x + s - 2, y + 9);
                g2.drawLine(x + 5, y + 2, x + 5, y + 5);
                g2.drawLine(x + s - 5, y + 2, x + s - 5, y + 5);
            } else if ("gender".equals(type)) {
                g2.drawOval(x + 3, y + 3, s - 8, s - 8);
                g2.drawLine(x + s - 4, y + 2, x + s - 7, y + 2);
                g2.drawLine(x + s - 4, y + 2, x + s - 4, y + 5);
                g2.drawLine(x + s - 7, y + 7, x + s - 3, y + 3);
            } else if ("phone".equals(type)) {
                g2.drawRoundRect(x + 4, y + 2, s - 8, s - 4, 3, 3);
                g2.drawLine(x + 7, y + s - 4, x + s - 7, y + s - 4);
            } else if ("info".equals(type)) {
                g2.drawOval(x + 1, y + 1, s - 2, s - 2);
                g2.setFont(new Font("Segoe UI", Font.BOLD, s - 4));
                g2.drawString("i", x + s / 2 - 2, y + s - 3);
            }

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    private void continueToPayment() {
        String fullName = fullNameField.getText().trim();
        String ageText = ageField.getText().trim();
        String phone = phoneField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();

        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter passenger name.", "Required", JOptionPane.WARNING_MESSAGE);
            fullNameField.requestFocus();
            return;
        }

        if (ageText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter passenger age.", "Required", JOptionPane.WARNING_MESSAGE);
            ageField.requestFocus();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Invalid Age", JOptionPane.WARNING_MESSAGE);
            ageField.requestFocus();
            return;
        }

        if (age < 1 || age > 120) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age between 1 and 120.", "Invalid Age", JOptionPane.WARNING_MESSAGE);
            ageField.requestFocus();
            return;
        }

        if (gender == null || "Select Gender".equalsIgnoreCase(gender)) {
            JOptionPane.showMessageDialog(this, "Please select passenger gender.", "Required", JOptionPane.WARNING_MESSAGE);
            genderBox.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter phone number.", "Required", JOptionPane.WARNING_MESSAGE);
            phoneField.requestFocus();
            return;
        }

        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone number must contain exactly 10 digits.", "Invalid Phone", JOptionPane.WARNING_MESSAGE);
            phoneField.requestFocus();
            return;
        }

        new Payment(userId, userName, flightId, flightNumber, airlineName, flightPrice, selectedSeatId, selectedSeatNumber, fullName, age, gender, phone).setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PassengerDetails(1, "Satyam", 1, "6E-123", "IndiGo", 6500, 1, "1A").setVisible(true));
    }
}