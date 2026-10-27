package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.UUID;

public class BookingConfirmation extends JFrame {

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
    private final String paymentMethod;

    private String pnr;

    public BookingConfirmation(
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
            String passengerPhone,
            String paymentMethod
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
        this.paymentMethod = paymentMethod;

        setTitle("SkySeat - Booking Confirmation");
        setSize(1350, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLayout(new BorderLayout());

        createHeader();
        createLoadingScreen();

        SwingUtilities.invokeLater(this::createBooking);
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
                GradientPaint gp = new GradientPaint(0, 0, new Color(10, 50, 110), getWidth(), getHeight(), new Color(20, 90, 180));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative airplane background shapes
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillOval(getWidth() - 350, -100, 450, 250);
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(1350, 75));
        header.setBorder(new EmptyBorder(10, 35, 10, 35));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        leftPanel.setOpaque(false);

        JLabel logoIcon = createVectorIconLabel("plane", Color.WHITE, 28, 28);
        JLabel logoText = new JLabel("SKYSEAT");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoText.setForeground(Color.WHITE);

        JLabel subText = new JLabel("  |   Fly Your Dreams");
        subText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subText.setForeground(new Color(200, 220, 250));

        leftPanel.add(logoIcon);
        leftPanel.add(logoText);
        leftPanel.add(subText);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        rightPanel.setOpaque(false);

        JLabel userIcon = createVectorIconLabel("user-round", Color.WHITE, 20, 20);
        JLabel userLabel = new JLabel("Welcome, " + userName);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);

        rightPanel.add(userIcon);
        rightPanel.add(userLabel);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void createLoadingScreen() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(243, 246, 251));

        JLabel label = new JLabel("Processing your booking...");
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(new Color(15, 23, 42));

        panel.add(label);
        add(panel, BorderLayout.CENTER);
    }

    private void createBooking() {
        Connection con = null;
        PreparedStatement passengerStatement = null;
        PreparedStatement bookingStatement = null;
        PreparedStatement paymentStatement = null;
        PreparedStatement seatStatement = null;

        ResultSet passengerKeys = null;
        ResultSet bookingKeys = null;

        try {
            con = DBConnection.getConnection();
            if (con == null) {
                showError("Database connection failed. Please check your MySQL setup.");
                return;
            }

            con.setAutoCommit(false);

            String passengerSql = "INSERT INTO PASSENGERS " +
                    "(USER_ID, FULL_NAME, AGE, GENDER, PHONE) " +
                    "VALUES (?, ?, ?, ?, ?)";

            passengerStatement = con.prepareStatement(passengerSql, Statement.RETURN_GENERATED_KEYS);
            passengerStatement.setInt(1, userId);
            passengerStatement.setString(2, passengerName);
            passengerStatement.setInt(3, passengerAge);
            passengerStatement.setString(4, passengerGender);
            passengerStatement.setString(5, passengerPhone);
            passengerStatement.executeUpdate();

            passengerKeys = passengerStatement.getGeneratedKeys();
            int passengerId;

            if (passengerKeys != null && passengerKeys.next()) {
                passengerId = passengerKeys.getInt(1);
            } else {
                passengerId = getLatestPassengerId(con);
            }

            pnr = generatePNR();

            String bookingSql = "INSERT INTO BOOKINGS " +
                    "(USER_ID, FLIGHT_ID, SEAT_ID, PASSENGER_ID, PNR, BOOKING_DATE, STATUS) " +
                    "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";

            bookingStatement = con.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS);
            bookingStatement.setInt(1, userId);
            bookingStatement.setInt(2, flightId);
            bookingStatement.setInt(3, selectedSeatId);
            bookingStatement.setInt(4, passengerId);
            bookingStatement.setString(5, pnr);
            bookingStatement.setString(6, "CONFIRMED");
            bookingStatement.executeUpdate();

            bookingKeys = bookingStatement.getGeneratedKeys();
            int bookingId;

            if (bookingKeys != null && bookingKeys.next()) {
                bookingId = bookingKeys.getInt(1);
            } else {
                bookingId = getLatestBookingId(con);
            }

            String paymentSql = "INSERT INTO PAYMENTS " +
                    "(BOOKING_ID, AMOUNT, PAYMENT_METHOD, PAYMENT_STATUS, PAYMENT_DATE) " +
                    "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

            paymentStatement = con.prepareStatement(paymentSql);
            paymentStatement.setInt(1, bookingId);
            paymentStatement.setDouble(2, flightPrice);
            paymentStatement.setString(3, paymentMethod);
            paymentStatement.setString(4, "SUCCESS");
            paymentStatement.executeUpdate();

            String seatSql = "UPDATE SEATS SET STATUS = 'BOOKED' " +
                    "WHERE (SEAT_ID = ? OR TRIM(SEAT_NUMBER) = ?) AND FLIGHT_ID = ?";

            seatStatement = con.prepareStatement(seatSql);
            seatStatement.setInt(1, selectedSeatId);
            seatStatement.setString(2, selectedSeatNumber != null ? selectedSeatNumber.trim() : "");
            seatStatement.setInt(3, flightId);

            seatStatement.executeUpdate();

            con.commit();
            showConfirmation();

        } catch (SQLException ex) {
            ex.printStackTrace();
            try { if (con != null) con.rollback(); } catch (Exception ignored) {}
            showError("Booking failed due to a database issue. Please try again.");
        } catch (Exception ex) {
            ex.printStackTrace();
            try { if (con != null) con.rollback(); } catch (Exception ignored) {}
            showError("An unexpected error occurred during processing:\n" + ex.getMessage());
        } finally {
            try { if (passengerKeys != null) passengerKeys.close(); } catch (Exception ignored) {}
            try { if (bookingKeys != null) bookingKeys.close(); } catch (Exception ignored) {}
            try { if (passengerStatement != null) passengerStatement.close(); } catch (Exception ignored) {}
            try { if (bookingStatement != null) bookingStatement.close(); } catch (Exception ignored) {}
            try { if (paymentStatement != null) paymentStatement.close(); } catch (Exception ignored) {}
            try { if (seatStatement != null) seatStatement.close(); } catch (Exception ignored) {}
            try { if (con != null) { con.setAutoCommit(true); con.close(); } } catch (Exception ignored) {}
        }
    }

    private int getLatestPassengerId(Connection con) throws Exception {
        String sql = "SELECT PASSENGER_ID FROM PASSENGERS WHERE USER_ID = ? ORDER BY PASSENGER_ID DESC LIMIT 1";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getInt("PASSENGER_ID");
            }
        }
        throw new Exception("Unable to find passenger ID.");
    }

    private int getLatestBookingId(Connection con) throws Exception {
        String sql = "SELECT BOOKING_ID FROM BOOKINGS WHERE USER_ID = ? ORDER BY BOOKING_ID DESC LIMIT 1";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getInt("BOOKING_ID");
            }
        }
        throw new Exception("Unable to find booking ID.");
    }

    private String generatePNR() {
        return "SKY" + UUID.randomUUID().toString().replace("-", "").substring(0, 7).toUpperCase();
    }

    private void showConfirmation() {
        Component northComp = null;
        BorderLayout layout = (BorderLayout) getLayout();
        if (layout != null) {
            northComp = layout.getLayoutComponent(BorderLayout.NORTH);
        }

        getContentPane().removeAll();

        if (northComp != null) {
            add(northComp, BorderLayout.NORTH);
        } else {
            createHeader();
        }

        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setBackground(new Color(243, 246, 251));

        JScrollPane scrollPane = new JScrollPane(createConfirmationUI());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainWrapper.add(scrollPane, BorderLayout.CENTER);
        add(mainWrapper, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel createConfirmationUI() {
        JPanel container = new JPanel(new BorderLayout(0, 20));
        container.setBackground(new Color(243, 246, 251));
        container.setBorder(new EmptyBorder(25, 60, 25, 60));

        // Top Success Banner with PNR Badge
        JPanel topBanner = new JPanel();
        topBanner.setLayout(new BoxLayout(topBanner, BoxLayout.Y_AXIS));
        topBanner.setOpaque(false);

        // Circular Green Icon Container
        JPanel greenCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(16, 185, 129));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        greenCircle.setPreferredSize(new Dimension(54, 54));
        greenCircle.setMaximumSize(new Dimension(54, 54));
        greenCircle.setOpaque(false);
        greenCircle.setLayout(new GridBagLayout());
        greenCircle.add(createVectorIconLabel("circle-check-big", Color.WHITE, 28, 28));
        greenCircle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel successTitle = new JLabel("BOOKING CONFIRMED");
        successTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        successTitle.setForeground(new Color(16, 185, 129));
        successTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitle = new JLabel("Your flight has been successfully booked!");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subTitle.setForeground(new Color(71, 85, 105));
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // PNR Tag Badge
        RoundedCard pnrCard = new RoundedCard(20, new Color(238, 242, 255), new Color(199, 210, 254));
        pnrCard.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 6));
        pnrCard.setMaximumSize(new Dimension(240, 36));

        JLabel pnrIcon = createVectorIconLabel("armchair", new Color(79, 70, 229), 16, 16);
        JLabel pnrText = new JLabel("PNR: " + pnr);
        pnrText.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnrText.setForeground(new Color(49, 46, 129));

        pnrCard.add(pnrIcon);
        pnrCard.add(pnrText);

        topBanner.add(greenCircle);
        topBanner.add(Box.createVerticalStrut(10));
        topBanner.add(successTitle);
        topBanner.add(Box.createVerticalStrut(4));
        topBanner.add(subTitle);
        topBanner.add(Box.createVerticalStrut(12));
        topBanner.add(pnrCard);

        // Center Split Panel
        JPanel contentCard = new RoundedCard(24, Color.WHITE, new Color(226, 232, 240));
        contentCard.setLayout(new GridBagLayout());
        contentCard.setBorder(new EmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Left Airline Brand Card
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.32; gbc.weighty = 1.0;
        contentCard.add(createAirlineCard(), gbc);

        // Center Details Table
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.38;
        contentCard.add(createDetailsTable(), gbc);

        // Right Your Journey Box
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.30;
        contentCard.add(createJourneyBox(), gbc);

        // Action Buttons Footer
        JPanel actionFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionFooter.setOpaque(false);

        JButton ticketButton = createStyledButton("VIEW TICKET", new Color(255, 255, 255), new Color(0, 95, 230), true, "plane");
        ticketButton.addActionListener(e -> showTicket());

        JButton closeButton = createStyledButton("CLOSE", new Color(0, 95, 230), Color.WHITE, false, "close");
        closeButton.addActionListener(e -> dispose());

        actionFooter.add(ticketButton);
        actionFooter.add(closeButton);

        container.add(topBanner, BorderLayout.NORTH);
        container.add(contentCard, BorderLayout.CENTER);
        container.add(actionFooter, BorderLayout.SOUTH);

        return container;
    }

    private JPanel createAirlineCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(6, 40, 110), 0, getHeight(), new Color(14, 110, 210));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillOval(-30, getHeight() - 120, 180, 180);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(30, 20, 25, 20));

        JPanel centerInfo = new JPanel();
        centerInfo.setLayout(new BoxLayout(centerInfo, BoxLayout.Y_AXIS));
        centerInfo.setOpaque(false);

        JLabel logoGraphic = createVectorIconLabel("plane", Color.WHITE, 46, 46);
        logoGraphic.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brandName = new JLabel(airlineName);
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 26));
        brandName.setForeground(Color.WHITE);
        brandName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel fNum = new JLabel(flightNumber);
        fNum.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        fNum.setForeground(new Color(210, 230, 255));
        fNum.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerInfo.add(Box.createVerticalGlue());
        centerInfo.add(logoGraphic);
        centerInfo.add(Box.createVerticalStrut(10));
        centerInfo.add(brandName);
        centerInfo.add(Box.createVerticalStrut(4));
        centerInfo.add(fNum);
        centerInfo.add(Box.createVerticalGlue());

        JPanel footerInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        footerInfo.setOpaque(false);
        JLabel miniPlane = createVectorIconLabel("plane", new Color(180, 215, 255), 14, 14);
        JLabel tag = new JLabel("On time. Every time.");
        tag.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tag.setForeground(new Color(210, 230, 255));
        footerInfo.add(miniPlane);
        footerInfo.add(tag);

        card.add(centerInfo, BorderLayout.CENTER);
        card.add(footerInfo, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createDetailsTable() {
        JPanel table = new JPanel(new GridLayout(7, 1, 0, 0));
        table.setOpaque(false);
        table.setBorder(new EmptyBorder(5, 10, 5, 10));

        table.add(createRowItem("user-round", "Passenger", passengerName, false, null));
        table.add(createRowItem("plane", "Airline", airlineName, false, null));
        table.add(createRowItem("plane", "Flight", flightNumber, false, null));
        table.add(createRowItem("armchair", "Seat", selectedSeatNumber, false, null));
        table.add(createRowItem("indian-rupee", "Amount", "₹" + String.format("%.2f", flightPrice), false, null));
        table.add(createRowItem("credit-card", "Payment", paymentMethod, false, null));
        table.add(createRowItem("circle-check-big", "Status", "CONFIRMED", true, new Color(16, 185, 129)));

        return table;
    }

    private JPanel createRowItem(String iconName, String label, String value, boolean isBadge, Color badgeBg) {
        JPanel row = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(241, 245, 249));
                g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(8, 0, 8, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        left.setOpaque(false);

        // Circular background container for table icons
        JPanel circleIconBg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(238, 242, 255));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        circleIconBg.setPreferredSize(new Dimension(30, 30));
        circleIconBg.setOpaque(false);
        circleIconBg.setLayout(new GridBagLayout());
        circleIconBg.add(createVectorIconLabel(iconName, new Color(0, 95, 230), 16, 16));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(new Color(100, 116, 139));

        left.add(circleIconBg);
        left.add(lbl);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        right.setOpaque(false);

        if (isBadge) {
            RoundedCard badge = new RoundedCard(14, new Color(209, 250, 229), null);
            badge.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 3));
            JLabel badgeText = new JLabel(value);
            badgeText.setFont(new Font("Segoe UI", Font.BOLD, 12));
            badgeText.setForeground(new Color(6, 95, 70));
            badge.add(badgeText);
            right.add(badge);
        } else {
            JLabel val = new JLabel(value);
            val.setFont(new Font("Segoe UI", Font.BOLD, 14));
            val.setForeground(new Color(15, 23, 42));
            right.add(val);
        }

        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private JPanel createJourneyBox() {
        RoundedCard card = new RoundedCard(16, new Color(248, 250, 252), new Color(226, 232, 240));
        card.setLayout(new BorderLayout(0, 15));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Header with Circular Blue Plane Icon
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        header.setOpaque(false);

        JPanel blueCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(224, 231, 255));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        blueCircle.setPreferredSize(new Dimension(30, 30));
        blueCircle.setOpaque(false);
        blueCircle.setLayout(new GridBagLayout());
        blueCircle.add(createVectorIconLabel("plane", new Color(0, 95, 230), 16, 16));

        JLabel jTitle = new JLabel("Your Journey");
        jTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        jTitle.setForeground(new Color(30, 41, 59));

        header.add(blueCircle);
        header.add(jTitle);

        // Flight Route Graphic with Plane Icon in Dotted Line
        RoundedCard routeCard = new RoundedCard(12, Color.WHITE, new Color(241, 245, 249));
        routeCard.setLayout(new BorderLayout());
        routeCard.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel src = new JLabel("<html><b>Patna</b><br><font color='#64748B' size='2'>PAT</font></html>");
        JLabel dst = new JLabel("<html><center><b>Delhi</b><br><font color='#64748B' size='2'>DEL</font></center></html>");

        JLabel lineGraphic = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(203, 213, 225));
                float[] dash = {4.0f, 4.0f};
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                g2.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);

                int cx = getWidth() / 2 - 8;
                int cy = getHeight() / 2 - 8;
                g2.setColor(new Color(0, 95, 230));
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                Path2D.Double centerPlane = new Path2D.Double();
                centerPlane.moveTo(17.8, 19.2); centerPlane.lineTo(16, 11); centerPlane.lineTo(19.5, 7.5);
                centerPlane.curveTo(21, 6, 21.5, 4, 21, 3);
                centerPlane.curveTo(20, 2.5, 18, 3, 16.5, 4.5);
                centerPlane.lineTo(13, 8); centerPlane.lineTo(4.8, 6.2);
                centerPlane.curveTo(4.3, 6.1, 3.9, 6.3, 3.7, 6.7);
                centerPlane.lineTo(3.4, 7.2);
                centerPlane.curveTo(3.2, 7.7, 3.3, 8.2, 3.7, 8.5);
                centerPlane.lineTo(9, 12); centerPlane.lineTo(7, 15); centerPlane.lineTo(4, 15);
                centerPlane.lineTo(3, 16); centerPlane.lineTo(6, 18); centerPlane.lineTo(8, 21);
                centerPlane.lineTo(9, 20); centerPlane.lineTo(9, 17); centerPlane.lineTo(12, 15);
                centerPlane.lineTo(15.5, 20.3);

                g2.translate(cx, cy);
                g2.scale(16.0 / 24.0, 16.0 / 24.0);
                g2.draw(centerPlane);
                g2.dispose();
            }
        };

        routeCard.add(src, BorderLayout.WEST);
        routeCard.add(lineGraphic, BorderLayout.CENTER);
        routeCard.add(dst, BorderLayout.EAST);

        // Sub details list
        JPanel subList = new JPanel(new GridLayout(3, 1, 0, 12));
        subList.setOpaque(false);

        subList.add(createMiniDetail("calendar-clock", "Flight Time", "(Approx.)", "- (Not shown)"));
        subList.add(createMiniDetail("calendar-days", "Date", "", "- (Not shown)"));
        subList.add(createMiniDetail("armchair", "Seat", "", selectedSeatNumber));

        card.add(header, BorderLayout.NORTH);
        card.add(routeCard, BorderLayout.CENTER);
        card.add(subList, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createMiniDetail(String iconName, String title, String subTitle, String val) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        JLabel ic = createVectorIconLabel(iconName, new Color(100, 116, 139), 16, 16);

        JPanel textStack = new JPanel(new GridLayout(2, 1, 0, 0));
        textStack.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setForeground(new Color(71, 85, 105));
        textStack.add(t);
        if (!subTitle.isEmpty()) {
            JLabel s = new JLabel(subTitle);
            s.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            s.setForeground(new Color(148, 163, 184));
            textStack.add(s);
        }

        left.add(ic);
        left.add(textStack);

        JLabel v = new JLabel(val);
        v.setFont(new Font("Segoe UI", Font.BOLD, 12));
        v.setForeground(new Color(30, 41, 59));

        row.add(left, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    // Styled Button Creator with optional vector icon
    private JButton createStyledButton(String text, Color bg, Color fg, boolean hasBorder, String btnIcon) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                if (hasBorder) {
                    g2.setColor(new Color(0, 95, 230));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (btnIcon != null && !btnIcon.isEmpty()) {
            JLabel iconLabel = createVectorIconLabel(btnIcon, fg, 14, 14);
            btn.setIcon(iconLabel.getIcon());
        }

        return btn;
    }

    // Dynamic Vector Graphics Generator (Handles all 8 Icons + Close X)
    private JLabel createVectorIconLabel(String iconName, Color strokeColor, int width, int height) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(width, height));
        label.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(strokeColor);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                double scaleX = width / 24.0;
                double scaleY = height / 24.0;
                g2.translate(x, y);
                g2.scale(scaleX, scaleY);

                switch (iconName) {
                    case "plane":
                        Path2D.Double plane = new Path2D.Double();
                        plane.moveTo(17.8, 19.2); plane.lineTo(16, 11); plane.lineTo(19.5, 7.5);
                        plane.curveTo(21, 6, 21.5, 4, 21, 3);
                        plane.curveTo(20, 2.5, 18, 3, 16.5, 4.5);
                        plane.lineTo(13, 8); plane.lineTo(4.8, 6.2);
                        plane.curveTo(4.3, 6.1, 3.9, 6.3, 3.7, 6.7);
                        plane.lineTo(3.4, 7.2);
                        plane.curveTo(3.2, 7.7, 3.3, 8.2, 3.7, 8.5);
                        plane.lineTo(9, 12); plane.lineTo(7, 15); plane.lineTo(4, 15);
                        plane.lineTo(3, 16); plane.lineTo(6, 18); plane.lineTo(8, 21);
                        plane.lineTo(9, 20); plane.lineTo(9, 17); plane.lineTo(12, 15);
                        plane.lineTo(15.5, 20.3);
                        g2.draw(plane);
                        break;

                    case "calendar-clock":
                        g2.drawOval(10, 10, 12, 12);
                        g2.draw(new Path2D.Double() {{
                            moveTo(16, 14); lineTo(16, 16.2); lineTo(17.6, 17.2);
                            moveTo(16, 2); lineTo(16, 5);
                            moveTo(21, 7.3); lineTo(21, 5);
                            curveTo(21, 3.9, 20.1, 3, 19, 3);
                            lineTo(5, 3);
                            curveTo(3.9, 3, 3, 3.9, 3, 5);
                            lineTo(3, 19);
                            curveTo(3, 20.1, 3.9, 21, 5, 21);
                            lineTo(7.3, 21);
                            moveTo(3, 9); lineTo(8.8, 9);
                            moveTo(8, 2); lineTo(8, 5);
                        }});
                        break;

                    case "calendar-days":
                        g2.drawRoundRect(3, 3, 18, 18, 4, 4);
                        g2.draw(new Path2D.Double() {{
                            moveTo(8, 2); lineTo(8, 5);
                            moveTo(16, 2); lineTo(16, 5);
                            moveTo(3, 9); lineTo(21, 9);
                            moveTo(8, 13); lineTo(8.01, 13);
                            moveTo(12, 13); lineTo(12.01, 13);
                            moveTo(16, 13); lineTo(16.01, 13);
                            moveTo(8, 17); lineTo(8.01, 17);
                            moveTo(12, 17); lineTo(12.01, 17);
                            moveTo(16, 17); lineTo(16.01, 17);
                        }});
                        break;

                    case "armchair":
                        g2.draw(new Path2D.Double() {{
                            moveTo(19, 9); lineTo(19, 6);
                            curveTo(19, 4.9, 18.1, 4, 17, 4);
                            lineTo(7, 4);
                            curveTo(5.9, 4, 5, 4.9, 5, 6);
                            lineTo(5, 9);

                            moveTo(3, 16);
                            curveTo(3, 17.1, 3.9, 18, 5, 18);
                            lineTo(19, 18);
                            curveTo(20.1, 18, 21, 17.1, 21, 16);
                            lineTo(21, 11);
                            curveTo(21, 9.9, 20.1, 9, 19, 9);
                            lineTo(19, 10.5);
                            curveTo(19, 10.8, 18.8, 11, 18.5, 11);
                            lineTo(9.5, 11);
                            curveTo(9.2, 11, 9, 10.8, 9, 10.5);
                            lineTo(9, 9);
                            curveTo(7.9, 9, 7, 9.9, 7, 11);
                            lineTo(7, 16);

                            moveTo(5, 18); lineTo(5, 20);
                            moveTo(19, 18); lineTo(19, 20);
                        }});
                        break;

                    case "indian-rupee":
                        g2.draw(new Path2D.Double() {{
                            moveTo(6, 3); lineTo(18, 3);
                            moveTo(6, 8); lineTo(18, 8);
                            moveTo(6, 13); lineTo(14.5, 21);
                            moveTo(6, 13); lineTo(9, 13);
                            moveTo(9, 13);
                            curveTo(15.6, 13, 15.6, 3, 9, 3);
                        }});
                        break;

                    case "user-round":
                        g2.drawOval(7, 3, 10, 10);
                        g2.draw(new Path2D.Double() {{
                            moveTo(20, 21);
                            curveTo(20, 16.5, 16.4, 13, 12, 13);
                            curveTo(7.6, 13, 4, 16.5, 4, 21);
                        }});
                        break;

                    case "credit-card":
                        g2.drawRoundRect(2, 5, 20, 14, 2, 2);
                        g2.drawLine(2, 10, 22, 10);
                        g2.drawLine(6, 14, 8, 14);
                        break;

                    case "circle-check-big":
                        Path2D.Double circleCheck = new Path2D.Double();
                        circleCheck.moveTo(21.801, 10);
                        circleCheck.curveTo(21.93, 10.65, 22, 11.32, 22, 12);
                        circleCheck.curveTo(22, 17.52, 17.52, 22, 12, 22);
                        circleCheck.curveTo(6.48, 22, 2, 17.52, 2, 12);
                        circleCheck.curveTo(2, 6.48, 6.48, 2, 12, 2);
                        circleCheck.curveTo(13.8, 2, 15.48, 2.48, 17, 3.335);
                        g2.draw(circleCheck);

                        Path2D.Double checkMark = new Path2D.Double();
                        checkMark.moveTo(9, 11);
                        checkMark.lineTo(12, 14);
                        checkMark.lineTo(22, 4);
                        g2.draw(checkMark);
                        break;

                    case "close":
                        g2.drawLine(18, 6, 6, 18);
                        g2.drawLine(6, 6, 18, 18);
                        break;
                }
                g2.dispose();
            }

            @Override public int getIconWidth() { return width; }
            @Override public int getIconHeight() { return height; }
        });
        return label;
    }

    private void showTicket() {
        JOptionPane.showMessageDialog(
                this,
                "SKYSEAT TICKET\n\nPNR: " + pnr +
                        "\nPassenger: " + passengerName +
                        "\nFlight: " + flightNumber +
                        "\nAirline: " + airlineName +
                        "\nSeat: " + selectedSeatNumber +
                        "\nAmount: ₹" + String.format("%.2f", flightPrice),
                "Ticket",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showError(String message) {
        getContentPane().removeAll();
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(243, 246, 251));

        JLabel label = new JLabel("<html><center>" + message.replace("\n", "<br>") + "</center></html>");
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(220, 38, 38));

        panel.add(label);
        add(panel, BorderLayout.CENTER);

        revalidate();
        repaint();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BookingConfirmation(
                    1, "kkk", 1, "6E 717", "IndiGo", 5370,
                    1, "2D", "aman gupta", 20, "Male", "9876543210", "Debit Card"
            ).setVisible(true);
        });
    }
}