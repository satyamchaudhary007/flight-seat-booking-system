package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

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
        setSize(1350, 830);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLayout(new BorderLayout());

        createHeader();
        createMainUI();
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
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(12, 60, 130), getWidth(), getHeight(), new Color(25, 100, 190));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 15));
                g2.fillOval(getWidth() - 300, -90, 400, 300);
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(1350, 75));
        header.setBorder(new EmptyBorder(10, 30, 10, 30));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        leftPanel.setOpaque(false);

        JLabel logoText = new JLabel("SKYSEAT");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoText.setForeground(Color.WHITE);

        JLabel subText = new JLabel("  |   Fly Your Dreams");
        subText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subText.setForeground(new Color(210, 230, 255));

        leftPanel.add(logoText);
        leftPanel.add(subText);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        rightPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Welcome, " + userName);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);

        rightPanel.add(userLabel);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void createMainUI() {
        JPanel container = new JPanel(new BorderLayout(0, 15));
        container.setBackground(new Color(243, 246, 251));
        container.setBorder(new EmptyBorder(20, 35, 20, 35));

        // Sub Header Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        titlePanel.setOpaque(false);

        JLabel titleIcon = createBadgeIconLabel("CARD", new Color(0, 95, 230), new Color(225, 238, 255), 46, 46);

        JPanel textStack = new JPanel(new GridLayout(2, 1, 0, 2));
        textStack.setOpaque(false);

        JLabel titleLabel = new JLabel("Payment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));
        titleLabel.setForeground(new Color(15, 23, 42));

        JLabel subtitleLabel = new JLabel("Complete your payment to confirm the booking.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(100, 116, 139));

        textStack.add(titleLabel);
        textStack.add(subtitleLabel);

        titlePanel.add(titleIcon);
        titlePanel.add(textStack);

        // Center Content Split
        JPanel centerGrid = new JPanel(new GridBagLayout());
        centerGrid.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weighty = 1.0;

        // Left Section
        gbc.gridx = 0; gbc.weightx = 0.62;
        centerGrid.add(createLeftPaymentPanel(), gbc);

        // Right Section
        gbc.gridx = 1; gbc.weightx = 0.38;
        centerGrid.add(createRightSummaryPanel(), gbc);

        // Footer Section
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel secureText = new JLabel("100% Secure Payment   |   Your data is safe with us");
        secureText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        secureText.setForeground(new Color(100, 116, 139));

        JButton payButton = new JButton("PAY ₹" + String.format("%.2f", flightPrice) + "  →") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        payButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        payButton.setForeground(Color.WHITE);
        payButton.setBackground(new Color(0, 95, 230));
        payButton.setPreferredSize(new Dimension(215, 46));
        payButton.setFocusPainted(false);
        payButton.setContentAreaFilled(false);
        payButton.setBorderPainted(false);
        payButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        payButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { payButton.setBackground(new Color(0, 75, 200)); }
            @Override
            public void mouseExited(MouseEvent e) { payButton.setBackground(new Color(0, 95, 230)); }
        });

        payButton.addActionListener(e -> processPayment());

        footer.add(secureText, BorderLayout.WEST);
        footer.add(payButton, BorderLayout.EAST);

        container.add(titlePanel, BorderLayout.NORTH);
        container.add(centerGrid, BorderLayout.CENTER);
        container.add(footer, BorderLayout.SOUTH);

        add(container, BorderLayout.CENTER);
    }

    private JPanel createLeftPaymentPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // Flight Details Card
        RoundedCard detailsCard = new RoundedCard(16, Color.WHITE, new Color(226, 232, 240));
        detailsCard.setLayout(new BorderLayout(0, 15));
        detailsCard.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel headerStack = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        headerStack.setOpaque(false);
        JLabel iconL = createBadgeIconLabel("PLANE", Color.WHITE, new Color(0, 95, 230), 38, 38);
        JPanel subStack = new JPanel(new GridLayout(2, 1));
        subStack.setOpaque(false);
        JLabel title1 = new JLabel("Flight Details");
        title1.setFont(new Font("Segoe UI", Font.BOLD, 17));
        JLabel title2 = new JLabel("Your selected flight and booking details");
        title2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        title2.setForeground(new Color(100, 116, 139));
        subStack.add(title1); subStack.add(title2);
        headerStack.add(iconL); headerStack.add(subStack);

        JPanel dynamicAirlineBox = new JPanel(new GridBagLayout());
        dynamicAirlineBox.setOpaque(false);
        dynamicAirlineBox.setBorder(new EmptyBorder(12, 0, 15, 0));

        GridBagConstraints gbcBox = new GridBagConstraints();
        gbcBox.fill = GridBagConstraints.BOTH;
        gbcBox.anchor = GridBagConstraints.CENTER;

        // Left Logo + Name Stack
        gbcBox.gridx = 0; gbcBox.weightx = 0.35;
        JPanel logoNameStack = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        logoNameStack.setOpaque(false);

        JLabel logoHolder = new JLabel();
        logoHolder.setPreferredSize(new Dimension(95, 60));
        loadSelectedAirlineLogo(logoHolder, airlineName);

        JPanel nameStack = new JPanel(new GridLayout(2, 1, 0, 2));
        nameStack.setOpaque(false);
        JLabel aName = new JLabel(airlineName);
        aName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        aName.setForeground(new Color(15, 23, 42));
        JLabel fNum = new JLabel(flightNumber);
        fNum.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fNum.setForeground(new Color(100, 116, 139));
        nameStack.add(aName); nameStack.add(fNum);

        logoNameStack.add(logoHolder);
        logoNameStack.add(nameStack);
        dynamicAirlineBox.add(logoNameStack, gbcBox);

        // Right Route Section (Filling 100% Space)
        gbcBox.gridx = 1; gbcBox.weightx = 0.65;
        JPanel routeGraphic = new JPanel(new BorderLayout(15, 0));
        routeGraphic.setOpaque(false);

        JLabel pat = new JLabel("<html><center><b>PAT</b><br><font color='#64748B'>Patna<br>(PAT)</font></center></html>");
        pat.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel del = new JLabel("<html><center><b>DEL</b><br><font color='#64748B'>Delhi<br>(DEL)</font></center></html>");
        del.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel routeLine = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(190, 210, 240));
                float[] dash = {6.0f, 6.0f};
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                g2.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);

                g2.setColor(new Color(0, 95, 230));
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawLine(cx - 7, cy, cx + 7, cy);
                g2.drawLine(cx + 1, cy - 5, cx + 6, cy);
                g2.drawLine(cx + 1, cy + 5, cx + 6, cy);
                g2.dispose();
            }
        };

        routeGraphic.add(pat, BorderLayout.WEST);
        routeGraphic.add(routeLine, BorderLayout.CENTER);
        routeGraphic.add(del, BorderLayout.EAST);

        dynamicAirlineBox.add(routeGraphic, gbcBox);

        // Grid Details
        JPanel infoGrid = new JPanel(new GridLayout(3, 2, 20, 14));
        infoGrid.setOpaque(false);
        infoGrid.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(240, 244, 248)));

        infoGrid.add(createDetailItem("PLANE", "Airline", airlineName));
        infoGrid.add(createDetailItem("SEAT", "Seat", selectedSeatNumber));
        infoGrid.add(createDetailItem("PLANE", "Flight", flightNumber));
        infoGrid.add(createDetailItem("MONEY", "Amount", "₹" + String.format("%.2f", flightPrice)));
        infoGrid.add(createDetailItem("USER", "Passenger", passengerName));

        detailsCard.add(headerStack, BorderLayout.NORTH);
        detailsCard.add(dynamicAirlineBox, BorderLayout.CENTER);
        detailsCard.add(infoGrid, BorderLayout.SOUTH);

        // Styled Payment Method Dropdown
        RoundedCard methodCard = new RoundedCard(16, Color.WHITE, new Color(226, 232, 240));
        methodCard.setLayout(new BorderLayout(15, 12));
        methodCard.setBorder(new EmptyBorder(18, 24, 18, 24));

        JPanel methodHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        methodHeader.setOpaque(false);
        JLabel mIcon = createBadgeIconLabel("CARD", new Color(0, 95, 230), new Color(225, 238, 255), 36, 36);
        JLabel mTitle = new JLabel("Payment Method");
        mTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        methodHeader.add(mIcon); methodHeader.add(mTitle);

        JPanel comboWrapper = new JPanel(new BorderLayout());
        comboWrapper.setOpaque(false);

        paymentMethodBox = new JComboBox<>(new String[]{"UPI", "Credit Card", "Debit Card", "Net Banking"});
        paymentMethodBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        paymentMethodBox.setPreferredSize(new Dimension(520, 40));
        paymentMethodBox.setBackground(new Color(248, 250, 254));
        paymentMethodBox.setFocusable(false);

        paymentMethodBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 225, 245), 1, true),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));

        comboWrapper.add(paymentMethodBox, BorderLayout.WEST);

        methodCard.add(methodHeader, BorderLayout.NORTH);
        methodCard.add(comboWrapper, BorderLayout.CENTER);

        leftPanel.add(detailsCard);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(methodCard);

        return leftPanel;
    }

    private JPanel createRightSummaryPanel() {
        JPanel rightStack = new JPanel();
        rightStack.setOpaque(false);
        rightStack.setLayout(new BoxLayout(rightStack, BoxLayout.Y_AXIS));

        // Secure Notice Top Card
        RoundedCard secureCard = new RoundedCard(16, new Color(236, 244, 255), new Color(200, 220, 250));
        secureCard.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 10));
        secureCard.setBorder(new EmptyBorder(6, 12, 6, 12));

        JLabel sIcon = new JLabel();
        sIcon.setPreferredSize(new Dimension(42, 42));
        loadPaymentLogo(sIcon);

        JPanel sText = new JPanel(new GridLayout(2, 1, 0, 2));
        sText.setOpaque(false);
        JLabel st1 = new JLabel("Secure Payment");
        st1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel st2 = new JLabel("Protected with 256-bit SSL encryption.");
        st2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        st2.setForeground(new Color(100, 116, 139));
        sText.add(st1); sText.add(st2);
        secureCard.add(sIcon); secureCard.add(sText);

        // Summary Breakdown Card
        RoundedCard summaryCard = new RoundedCard(16, Color.WHITE, new Color(226, 232, 240));
        summaryCard.setLayout(new BorderLayout(0, 18));
        summaryCard.setBorder(new EmptyBorder(22, 24, 22, 24));

        JLabel sumTitle = new JLabel("Booking Summary");
        sumTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel list = new JPanel(new GridLayout(5, 2, 10, 16));
        list.setOpaque(false);

        addSummaryRow(list, "Airline", airlineName);
        addSummaryRow(list, "Flight", flightNumber);
        addSummaryRow(list, "Passenger", passengerName);
        addSummaryRow(list, "Seat", selectedSeatNumber);
        addSummaryRow(list, "Amount", "₹" + String.format("%.2f", flightPrice));

        JPanel totalLine = new JPanel(new BorderLayout());
        totalLine.setOpaque(false);
        totalLine.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JLabel totLbl = new JLabel("Total Amount");
        totLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        totLbl.setForeground(new Color(15, 23, 42));

        JLabel totVal = new JLabel("₹" + String.format("%.2f", flightPrice));
        totVal.setFont(new Font("Segoe UI", Font.BOLD, 23));
        totVal.setForeground(new Color(0, 95, 230));

        totalLine.add(totLbl, BorderLayout.WEST);
        totalLine.add(totVal, BorderLayout.EAST);

        summaryCard.add(sumTitle, BorderLayout.NORTH);
        summaryCard.add(list, BorderLayout.CENTER);
        summaryCard.add(totalLine, BorderLayout.SOUTH);

        rightStack.add(secureCard);
        rightStack.add(Box.createVerticalStrut(15));
        rightStack.add(summaryCard);

        return rightStack;
    }

    private void addSummaryRow(JPanel panel, String key, String value) {
        JLabel k = new JLabel(key);
        k.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        k.setForeground(new Color(100, 116, 139));

        JLabel v = new JLabel(value, SwingConstants.RIGHT);
        v.setFont(new Font("Segoe UI", Font.BOLD, 13));
        v.setForeground(new Color(15, 23, 42));

        panel.add(k);
        panel.add(v);
    }

    private JPanel createDetailItem(String iconType, String title, String val) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        p.setOpaque(false);

        JLabel ic = createBadgeIconLabel(iconType, new Color(0, 95, 230), new Color(225, 238, 255), 36, 36);
        JPanel textStack = new JPanel(new GridLayout(2, 1, 0, 2));
        textStack.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        t.setForeground(new Color(100, 116, 139));

        JLabel v = new JLabel(val);
        v.setFont(new Font("Segoe UI", Font.BOLD, 13));
        v.setForeground(new Color(15, 23, 42));

        textStack.add(t); textStack.add(v);

        p.add(ic); p.add(textStack);
        return p;
    }

    private JLabel createBadgeIconLabel(String type, Color iconCol, Color bgCol, int width, int height) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(width, height));
        label.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(bgCol);
                g2.fill(new Ellipse2D.Double(x, y, width, height));

                g2.setColor(iconCol);
                g2.setStroke(new BasicStroke(1.8f));

                int cx = x + width / 2;
                int cy = y + height / 2;

                if ("CARD".equals(type)) {
                    g2.drawRoundRect(cx - 8, cy - 5, 16, 10, 3, 3);
                    g2.drawLine(cx - 8, cy - 1, cx + 8, cy - 1);
                } else if ("PLANE".equals(type)) {
                    g2.drawLine(cx - 6, cy + 3, cx + 6, cy - 3);
                    g2.drawLine(cx - 1, cy - 1, cx + 3, cy + 4);
                } else if ("SEAT".equals(type)) {
                    g2.drawRoundRect(cx - 5, cy - 5, 10, 8, 2, 2);
                    g2.drawLine(cx - 6, cy + 4, cx + 6, cy + 4);
                } else if ("USER".equals(type)) {
                    g2.drawOval(cx - 3, cy - 6, 6, 6);
                    g2.drawArc(cx - 6, cy + 1, 12, 8, 0, 180);
                } else if ("MONEY".equals(type)) {
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString("₹", cx - fm.stringWidth("₹") / 2, cy + 4);
                } else if ("SHIELD".equals(type)) {
                    g2.drawRoundRect(cx - 6, cy - 7, 12, 14, 4, 4);
                }
                g2.dispose();
            }

            @Override public int getIconWidth() { return width; }
            @Override public int getIconHeight() { return height; }
        });
        return label;
    }

    private void loadPaymentLogo(JLabel label) {
        String[] paths = {
                "/images/payment.jpg",
                "/images/payment.png",
                "src/images/payment.jpg",
                "src/images/payment.png",
                "images/payment.jpg",
                "images/payment.png"
        };

        BufferedImage img = null;
        for (String p : paths) {
            try {
                if (p.startsWith("/")) {
                    URL u = getClass().getResource(p);
                    if (u != null) { img = javax.imageio.ImageIO.read(u); break; }
                } else {
                    File f = new File(p);
                    if (f.exists()) { img = javax.imageio.ImageIO.read(f); break; }
                }
            } catch (Exception ignored) {}
        }

        if (img != null) {
            Image scaled = img.getScaledInstance(42, 42, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
        } else {
            label.setIcon(createBadgeIconLabel("SHIELD", Color.WHITE, new Color(0, 95, 230), 42, 42).getIcon());
        }
    }

    private void loadSelectedAirlineLogo(JLabel label, String airlineName) {
        String fileName = "INDIGO.jpg";
        String clean = airlineName.toLowerCase();

        if (clean.contains("spicejet")) fileName = "SpiceJet.jpg";
        else if (clean.contains("express")) fileName = "AirIndiaExpress.jpg";
        else if (clean.contains("akasa")) fileName = "AkasaAir.jpg";
        else if (clean.contains("air india")) fileName = "AirIndia.jpg";

        String[] paths = {
                "/images/" + fileName,
                "src/images/" + fileName,
                "images/" + fileName
        };

        BufferedImage img = null;
        for (String p : paths) {
            try {
                if (p.startsWith("/")) {
                    URL u = getClass().getResource(p);
                    if (u != null) { img = javax.imageio.ImageIO.read(u); break; }
                } else {
                    File f = new File(p);
                    if (f.exists()) { img = javax.imageio.ImageIO.read(f); break; }
                }
            } catch (Exception ignored) {}
        }

        if (img != null) {
            Image scaled = img.getScaledInstance(95, 60, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
        } else {
            label.setIcon(new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0, 95, 230));
                    g2.fillRoundRect(x, y, 95, 60, 12, 12);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    String letter = airlineName.isEmpty() ? "A" : airlineName.substring(0, 1);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(letter, x + (95 - fm.stringWidth(letter)) / 2, y + (60 + fm.getAscent() - fm.getDescent()) / 2);
                    g2.dispose();
                }

                @Override public int getIconWidth() { return 95; }
                @Override public int getIconHeight() { return 60; }
            });
        }
    }

    private void processPayment() {
        String paymentMethod = (String) paymentMethodBox.getSelectedItem();

        int result = JOptionPane.showConfirmDialog(
                this,
                "Confirm payment of ₹" + String.format("%.2f", flightPrice) + " using " + paymentMethod + "?",
                "Confirm Payment",
                JOptionPane.YES_NO_OPTION
        );

        if (result != JOptionPane.YES_OPTION) return;

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

    static class RoundedCard extends JPanel {
        private final int radius;
        private final Color bg;
        private final Color border;

        public RoundedCard(int radius, Color bg, Color border) {
            this.radius = radius;
            this.bg = bg;
            this.border = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));

            if (border != null) {
                g2.setColor(border);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Payment(1, "Satyam", 1, "6E 717", "IndiGo", 9790.00, 1, "6D", "satyam kumar", 20, "Male", "9876543210").setVisible(true));
    }
}