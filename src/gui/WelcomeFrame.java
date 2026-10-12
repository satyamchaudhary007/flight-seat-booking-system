package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WelcomeFrame extends JFrame {

    private final Color NAVY = new Color(7, 29, 55);
    private final Color NAVY_2 = new Color(12, 45, 82);
    private final Color BLUE = new Color(42, 139, 239);
    private final Color SKY = new Color(87, 184, 255);
    private final Color WHITE = Color.WHITE;
    private final Color TEXT = new Color(24, 42, 63);
    private final Color MUTED = new Color(104, 120, 138);
    private final Color BACKGROUND = new Color(244, 247, 251);

    public WelcomeFrame() {

        setTitle("SkySeat - Flight Seat Booking System");
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);

        // =====================================================
        // TOP BAR
        // =====================================================

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(NAVY);
        topBar.setPreferredSize(new Dimension(1100, 72));
        topBar.setBorder(
                BorderFactory.createEmptyBorder(0, 35, 0, 35)
        );

        JPanel brandPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        0,
                        0
                )
        );
        brandPanel.setOpaque(false);

        JLabel brand = new JLabel("SkySeat");
        brand.setFont(
                new Font("Segoe UI", Font.BOLD, 29)
        );
        brand.setForeground(Color.WHITE);

        JLabel brandDot = new JLabel("  •");
        brandDot.setFont(
                new Font("Segoe UI", Font.BOLD, 20)
        );
        brandDot.setForeground(SKY);

        JLabel brandSub = new JLabel(
                "  FLIGHT BOOKING"
        );
        brandSub.setFont(
                new Font("Segoe UI", Font.PLAIN, 11)
        );
        brandSub.setForeground(
                new Color(180, 203, 225)
        );

        brandPanel.add(brand);
        brandPanel.add(brandDot);
        brandPanel.add(brandSub);

        JLabel systemLabel = new JLabel(
                "Flight Seat Booking System"
        );
        systemLabel.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );
        systemLabel.setForeground(
                new Color(215, 228, 242)
        );

        topBar.add(
                brandPanel,
                BorderLayout.WEST
        );

        topBar.add(
                systemLabel,
                BorderLayout.EAST
        );

        // =====================================================
        // MAIN CONTENT
        // =====================================================

        JPanel content = new JPanel(
                new GridLayout(1, 2, 0, 0)
        );
        content.setBackground(BACKGROUND);

        // =====================================================
        // LEFT HERO
        // =====================================================

        FlightHeroPanel heroPanel =
                new FlightHeroPanel();

        content.add(heroPanel);

        // =====================================================
        // RIGHT LOGIN / REGISTER
        // =====================================================

        JPanel rightPanel = new ImageBackgroundPanel();
        rightPanel.setLayout(
                new BoxLayout(
                        rightPanel,
                        BoxLayout.Y_AXIS
                )
        );

        rightPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        65,
                        48,
                        35,
                        48
                )
        );

        JLabel smallLabel =
                new JLabel("WELCOME ABOARD");

        smallLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        smallLabel.setForeground(BLUE);
        smallLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        rightPanel.add(smallLabel);

        rightPanel.add(
                Box.createVerticalStrut(9)
        );

        JLabel heading =
                new JLabel(
                        "<html>Your journey<br>starts here.</html>"
                );

        heading.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        35
                )
        );

        heading.setForeground(TEXT);
        heading.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        rightPanel.add(heading);

        rightPanel.add(
                Box.createVerticalStrut(12)
        );

        JLabel description =
                new JLabel(
                        "<html>Book your flight, select your preferred seat<br>" +
                                "and make your journey comfortable.</html>"
                );

        description.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        description.setForeground(MUTED);
        description.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        rightPanel.add(description);

        rightPanel.add(
                Box.createVerticalStrut(38)
        );

        // =====================================================
        // LOGIN CARD
        // =====================================================

        JPanel loginPanel =
                createOptionPanel(
                        "Existing User",
                        "Continue with your account",
                        "LOGIN",
                        "01"
                );

        loginPanel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        rightPanel.add(loginPanel);

        rightPanel.add(
                Box.createVerticalStrut(13)
        );

        // =====================================================
        // REGISTER CARD
        // =====================================================

        JPanel registerPanel =
                createOptionPanel(
                        "New User",
                        "Create your SkySeat account",
                        "REGISTER",
                        "02"
                );

        registerPanel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        rightPanel.add(registerPanel);

        // =====================================================
        // BUTTON ACTIONS
        // =====================================================

        JButton loginButton =
                (JButton) loginPanel
                        .getClientProperty("button");

        JButton registerButton =
                (JButton) registerPanel
                        .getClientProperty("button");

        loginButton.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });

        registerButton.addActionListener(e -> {
            new Register().setVisible(true);
            dispose();
        });

        rightPanel.add(
                Box.createVerticalGlue()
        );

        // =====================================================
        // SECURITY TEXT
        // =====================================================

        JPanel securePanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                0,
                                0
                        )
                );

        securePanel.setOpaque(false);
        securePanel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel secure =
                new JLabel(
                        "●  Secure & simple flight booking"
                );

        secure.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        secure.setForeground(
                new Color(120, 135, 150)
        );

        securePanel.add(secure);

        rightPanel.add(securePanel);

        content.add(rightPanel);

        // =====================================================
        // FOOTER
        // =====================================================

        JPanel footer =
                new JPanel(
                        new BorderLayout()
                );

        footer.setBackground(NAVY);
        footer.setPreferredSize(
                new Dimension(1100, 40)
        );

        JLabel copyright =
                new JLabel(
                        "  © 2026 SkySeat"
                );

        copyright.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        copyright.setForeground(
                new Color(180, 200, 220)
        );

        JLabel footerRight =
                new JLabel(
                        "FLY SMART  •  TRAVEL BETTER   "
                );

        footerRight.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        10
                )
        );

        footerRight.setForeground(SKY);

        footer.add(
                copyright,
                BorderLayout.WEST
        );

        footer.add(
                footerRight,
                BorderLayout.EAST
        );

        // =====================================================
        // ADD EVERYTHING
        // =====================================================

        mainPanel.add(
                topBar,
                BorderLayout.NORTH
        );

        mainPanel.add(
                content,
                BorderLayout.CENTER
        );

        mainPanel.add(
                footer,
                BorderLayout.SOUTH
        );

        add(mainPanel);
    }

    // =========================================================
    // OPTION CARD
    // =========================================================

    private JPanel createOptionPanel(
            String title,
            String subtitle,
            String buttonText,
            String number
    ) {

        RoundedPanel panel =
                new RoundedPanel(
                        18,
                        new Color(255, 255, 255, 222)
                );

        panel.setLayout(
                new BorderLayout()
        );

        panel.setPreferredSize(
                new Dimension(430, 132)
        );

        panel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        132
                )
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        16,
                        18,
                        16,
                        16
                )
        );

        // =====================================================
        // NUMBER
        // =====================================================

        JPanel numberPanel =
                new RoundedPanel(
                        12,
                        new Color(231, 242, 253, 235)
                );

        numberPanel.setLayout(
                new GridBagLayout()
        );

        numberPanel.setPreferredSize(
                new Dimension(65, 65)
        );

        numberPanel.setMinimumSize(
                new Dimension(65, 65)
        );

        numberPanel.setMaximumSize(
                new Dimension(65, 65)
        );

        JLabel numberLabel =
                new JLabel(number);

        numberLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        numberLabel.setForeground(BLUE);

        numberPanel.add(numberLabel);

        panel.add(
                numberPanel,
                BorderLayout.WEST
        );

        // =====================================================
        // TEXT
        // =====================================================

        JPanel textPanel =
                new JPanel();

        textPanel.setOpaque(false);

        textPanel.setLayout(
                new BoxLayout(
                        textPanel,
                        BoxLayout.Y_AXIS
                )
        );

        textPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        3,
                        17,
                        0,
                        10
                )
        );

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        titleLabel.setForeground(TEXT);

        JLabel subtitleLabel =
                new JLabel(subtitle);

        subtitleLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        subtitleLabel.setForeground(MUTED);

        textPanel.add(titleLabel);

        textPanel.add(
                Box.createVerticalStrut(6)
        );

        textPanel.add(subtitleLabel);

        panel.add(
                textPanel,
                BorderLayout.CENTER
        );

        // =====================================================
        // BUTTON
        // =====================================================

        RoundedButton button =
                new RoundedButton(
                        buttonText,
                        8
                );

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        button.setForeground(Color.WHITE);
        button.setBackground(BLUE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setPreferredSize(
                new Dimension(136, 94)
        );

        button.setMinimumSize(
                new Dimension(136, 94)
        );

        button.setMaximumSize(
                new Dimension(136, 94)
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        panel.add(
                button,
                BorderLayout.EAST
        );

        // =====================================================
        // IMPORTANT
        // =====================================================

        panel.putClientProperty(
                "button",
                button
        );

        return panel;
    }

    // =========================================================
    // CUSTOM FLIGHT HERO
    // =========================================================

    private class FlightHeroPanel
            extends JPanel {

        public FlightHeroPanel() {

            setPreferredSize(
                    new Dimension(550, 568)
            );

            setBackground(NAVY);
            setLayout(null);

            // -------------------------------------------------
            // HERO TITLE
            // -------------------------------------------------

            JLabel label =
                    new JLabel(
                            "<html><div style='text-align:left'>" +
                                    "FLY YOUR WAY<br>" +
                                    "<span style='color:#57B8FF'>" +
                                    "WITH SKYSEAT</span>" +
                                    "</div></html>"
                    );

            label.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            31
                    )
            );

            label.setForeground(Color.WHITE);

            label.setBounds(
                    45,
                    42,
                    390,
                    90
            );

            add(label);

            // -------------------------------------------------
            // SUBTITLE
            // -------------------------------------------------

            JLabel subtitle =
                    new JLabel(
                            "<html>Simple booking. Smart seating.<br>" +
                                    "A smoother way to travel.</html>"
                    );

            subtitle.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            13
                    )
            );

            subtitle.setForeground(
                    new Color(184, 207, 229)
            );

            subtitle.setBounds(
                    47,
                    140,
                    330,
                    45
            );

            add(subtitle);

            // -------------------------------------------------
            // FLIGHT ROUTE CARD
            // -------------------------------------------------

            RoundedPanel routeCard =
                    new RoundedPanel(
                            22,
                            new Color(16, 54, 94)
                    );

            routeCard.setLayout(null);

            routeCard.setBounds(
                    45,
                    230,
                    430,
                    165
            );

            add(routeCard);

            // FROM

            JLabel fromSmall =
                    new JLabel("FROM");

            fromSmall.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            10
                    )
            );

            fromSmall.setForeground(
                    new Color(141, 177, 211)
            );

            fromSmall.setBounds(
                    28,
                    24,
                    70,
                    20
            );

            routeCard.add(fromSmall);

            JLabel from =
                    new JLabel("DEL");

            from.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            30
                    )
            );

            from.setForeground(Color.WHITE);

            from.setBounds(
                    28,
                    42,
                    80,
                    40
            );

            routeCard.add(from);

            JLabel fromCity =
                    new JLabel("New Delhi");

            fromCity.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            11
                    )
            );

            fromCity.setForeground(
                    new Color(184, 207, 229)
            );

            fromCity.setBounds(
                    28,
                    82,
                    100,
                    20
            );

            routeCard.add(fromCity);

            // TO

            JLabel toSmall =
                    new JLabel("TO");

            toSmall.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            10
                    )
            );

            toSmall.setForeground(
                    new Color(141, 177, 211)
            );

            toSmall.setBounds(
                    330,
                    24,
                    50,
                    20
            );

            routeCard.add(toSmall);

            JLabel to =
                    new JLabel("BOM");

            to.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            30
                    )
            );

            to.setForeground(Color.WHITE);

            to.setBounds(
                    330,
                    42,
                    80,
                    40
            );

            routeCard.add(to);

            JLabel toCity =
                    new JLabel("Mumbai");

            toCity.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            11
                    )
            );

            toCity.setForeground(
                    new Color(184, 207, 229)
            );

            toCity.setBounds(
                    330,
                    82,
                    100,
                    20
            );

            routeCard.add(toCity);

            JLabel route =
                    new JLabel("✈");

            route.setFont(
                    new Font(
                            "Segoe UI Symbol",
                            Font.PLAIN,
                            23
                    )
            );

            route.setForeground(SKY);

            route.setBounds(
                    195,
                    58,
                    45,
                    35
            );

            routeCard.add(route);

            JLabel routeText =
                    new JLabel(
                            "NON-STOP  •  2h 10m"
                    );

            routeText.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            9
                    )
            );

            routeText.setForeground(
                    new Color(150, 194, 230)
            );

            routeText.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            routeText.setBounds(
                    140,
                    112,
                    150,
                    20
            );

            routeCard.add(routeText);

            // -------------------------------------------------
            // BOTTOM TEXT
            // -------------------------------------------------

            JLabel bottom =
                    new JLabel(
                            "YOUR JOURNEY. YOUR SEAT. YOUR WAY."
                    );

            bottom.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            10
                    )
            );

            bottom.setForeground(
                    new Color(115, 165, 210)
            );

            bottom.setBounds(
                    47,
                    430,
                    350,
                    25
            );

            add(bottom);

            JLabel info =
                    new JLabel(
                            "<html>Choose your preferred seat and enjoy<br>" +
                                    "a comfortable journey from take-off to landing.</html>"
                    );

            info.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            12
                    )
            );

            info.setForeground(
                    new Color(166, 194, 218)
            );

            info.setBounds(
                    47,
                    456,
                    350,
                    45
            );

            add(info);
        }

        @Override
        protected void paintComponent(
                Graphics g
        ) {

            super.paintComponent(g);

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // =================================================
            // GRADIENT BACKGROUND
            // =================================================

            GradientPaint gradient =
                    new GradientPaint(
                            0,
                            0,
                            NAVY,
                            getWidth(),
                            getHeight(),
                            NAVY_2
                    );

            g2.setPaint(gradient);

            g2.fillRect(
                    0,
                    0,
                    getWidth(),
                    getHeight()
            );

            // =================================================
            // DECORATIVE CIRCLE
            // =================================================

            g2.setColor(
                    new Color(
                            35,
                            110,
                            180,
                            35
                    )
            );

            g2.fillOval(
                    getWidth() - 150,
                    -100,
                    260,
                    260
            );

            g2.setColor(
                    new Color(
                            80,
                            180,
                            255,
                            18
                    )
            );

            g2.fillOval(
                    -130,
                    getHeight() - 140,
                    280,
                    280
            );

            // =================================================
            // FLIGHT PATH
            // =================================================

            g2.setColor(
                    new Color(
                            85,
                            185,
                            255,
                            110
                    )
            );

            g2.setStroke(
                    new BasicStroke(
                            2.0f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND,
                            1,
                            new float[]{7, 7},
                            0
                    )
            );

            CubicCurve2D curve =
                    new CubicCurve2D.Float(
                            95,
                            205,
                            180,
                            130,
                            340,
                            195,
                            440,
                            145
                    );

            g2.draw(curve);

            // =================================================
            // DESTINATION DOTS
            // =================================================

            g2.setColor(SKY);

            g2.fillOval(
                    88,
                    198,
                    14,
                    14
            );

            g2.fillOval(
                    433,
                    138,
                    14,
                    14
            );

            // =================================================
            // SMALL DECORATIVE DOTS
            // =================================================

            g2.setColor(
                    new Color(
                            105,
                            190,
                            255,
                            90
                    )
            );

            for (int i = 0; i < 5; i++) {

                g2.fillOval(
                        420 + (i * 18),
                        480 + (i * 7),
                        4,
                        4
                );
            }

            g2.dispose();
        }
    }

    // =========================================================
    // RIGHT PANEL WITH AIRPLANE BACKGROUND
    // =========================================================

    private class ImageBackgroundPanel extends JPanel {

        private final Image backgroundImage;

        public ImageBackgroundPanel() {
            setOpaque(false);

            ImageIcon icon = null;

            // Recommended location: src/images/flight.jpg
            java.net.URL resource =
                    ImageBackgroundPanel.class.getResource("/images/flight.jpg");

            if (resource != null) {
                icon = new ImageIcon(resource);
            } else {
                // Fallback if the image is kept in the project working directory.
                java.io.File file = new java.io.File("images/flight.jpg");
                if (!file.exists()) {
                    file = new java.io.File("flight.jpg");
                }

                if (file.exists()) {
                    icon = new ImageIcon(file.getAbsolutePath());
                }
            }

            backgroundImage = icon != null ? icon.getImage() : null;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );

            // White base behind the image.
            g2.setColor(BACKGROUND);
            g2.fillRect(0, 0, getWidth(), getHeight());

            if (backgroundImage != null) {
                int panelWidth = getWidth();
                int panelHeight = getHeight();

                int imageWidth = backgroundImage.getWidth(this);
                int imageHeight = backgroundImage.getHeight(this);

                if (imageWidth > 0 && imageHeight > 0) {
                    // Cover the complete right panel without stretching.
                    double scale = Math.max(
                            (double) panelWidth / imageWidth,
                            (double) panelHeight / imageHeight
                    );

                    int drawWidth = (int) Math.ceil(imageWidth * scale);
                    int drawHeight = (int) Math.ceil(imageHeight * scale);

                    int x = (panelWidth - drawWidth) / 2;
                    int y = (panelHeight - drawHeight) / 2;

                    g2.drawImage(
                            backgroundImage,
                            x,
                            y,
                            drawWidth,
                            drawHeight,
                            this
                    );

                }
            }

            g2.dispose();
        }
    }

    // =========================================================
    // ROUNDED PANEL
    // =========================================================

    private static class RoundedPanel
            extends JPanel {

        private final int radius;
        private final Color panelColor;

        public RoundedPanel(
                int radius,
                Color panelColor
        ) {

            this.radius = radius;
            this.panelColor = panelColor;

            setOpaque(false);
        }

        @Override
        protected void paintComponent(
                Graphics g
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // Shadow

            g2.setColor(
                    new Color(
                            0,
                            0,
                            0,
                            25
                    )
            );

            g2.fillRoundRect(
                    3,
                    5,
                    getWidth() - 6,
                    getHeight() - 6,
                    radius,
                    radius
            );

            // Panel

            g2.setColor(panelColor);

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth() - 5,
                    getHeight() - 5,
                    radius,
                    radius
            );

            g2.setColor(new Color(255, 255, 255, 175));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(
                    0,
                    0,
                    getWidth() - 6,
                    getHeight() - 6,
                    radius,
                    radius
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // =========================================================
    // ROUNDED BUTTON
    // =========================================================

    private static class RoundedButton extends JButton {

        private final int radius;
        private float hoverProgress = 0f;
        private Timer hoverTimer;

        private final Color NORMAL = new Color(42, 139, 239);
        private final Color HOVER = new Color(87, 184, 255);
        private final Color PRESSED = new Color(30, 116, 211);

        public RoundedButton(String text, int radius) {
            super(text);

            this.radius = radius;

            setContentAreaFilled(false);
            setOpaque(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setRolloverEnabled(true);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    animateHover(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    animateHover(false);
                }
            });
        }

        private void animateHover(boolean entering) {
            if (hoverTimer != null && hoverTimer.isRunning()) {
                hoverTimer.stop();
            }

            final float target = entering ? 1f : 0f;

            hoverTimer = new Timer(12, null);
            hoverTimer.addActionListener(e -> {
                float step = 0.10f;

                if (entering) {
                    hoverProgress = Math.min(target, hoverProgress + step);
                } else {
                    hoverProgress = Math.max(target, hoverProgress - step);
                }

                repaint();

                if (hoverProgress == target) {
                    ((Timer) e.getSource()).stop();
                }
            });

            hoverTimer.start();
        }

        private Color blend(Color a, Color b, float amount) {
            int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * amount);
            int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * amount);
            int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * amount);
            return new Color(r, g, bl);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );

            boolean pressed = getModel().isPressed();
            Color baseColor = getBackground();

            if (baseColor == null) {
                baseColor = NORMAL;
            }

            Color topColor;
            Color bottomColor;

            if (pressed) {
                topColor = PRESSED;
                bottomColor = PRESSED.darker();
            } else {
                Color hoverColor = blend(baseColor, HOVER, hoverProgress);
                topColor = hoverColor.brighter();
                bottomColor = hoverColor;
            }

            if (hoverProgress > 0f && !pressed) {
                g2.setColor(new Color(42, 139, 239, 55));
                g2.fillRoundRect(2, 5, getWidth() - 4, getHeight() - 2, radius + 4, radius + 4);
            }

            GradientPaint gradient = new GradientPaint(
                    0, 0, topColor,
                    0, getHeight(), bottomColor
            );

            g2.setPaint(gradient);
            g2.fillRoundRect(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 1,
                    radius,
                    radius
            );

            if (hoverProgress > 0f && !pressed) {
                g2.setColor(new Color(255, 255, 255, 45));
                g2.drawRoundRect(
                        0,
                        0,
                        getWidth() - 2,
                        getHeight() - 2,
                        radius,
                        radius
                );
            }

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new WelcomeFrame().setVisible(true);

        });
    }
}
