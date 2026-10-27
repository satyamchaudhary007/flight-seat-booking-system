package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FlightResults extends JFrame {

    private final int userId;
    private final String userName;
    private final String fromCity;
    private final String toCity;
    private final String travelDate;
    private final int passengerCount;

    private JPanel resultsPanel;
    private final DecimalFormat currencyFmt = new DecimalFormat("₹#,##0");

    public FlightResults(
            int userId,
            String userName,
            String fromCity,
            String toCity,
            String travelDate,
            int passengerCount
    ) {
        this.userId = userId;
        this.userName = userName;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.travelDate = travelDate;
        this.passengerCount = passengerCount;

        setTitle("SkySeat - Available Flights");
        setSize(1300, 820);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLayout(new BorderLayout());

        createHeaderBanner();
        createResultsArea();

        loadFlights();
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

    // TOP HEADER BANNER (Fixed Unicode Icons)
    private void createHeaderBanner() {
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 55, 120), getWidth(), getHeight(), new Color(30, 95, 180));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillOval(getWidth() - 250, -50, 300, 300);
                g2.dispose();
            }
        };
        banner.setPreferredSize(new Dimension(1300, 95));
        banner.setBorder(new EmptyBorder(15, 30, 15, 30));

        JPanel leftRoute = new JPanel();
        leftRoute.setOpaque(false);
        leftRoute.setLayout(new BoxLayout(leftRoute, BoxLayout.Y_AXIS));

        JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        line1.setOpaque(false);

        JLabel planeIc = new JLabel("✈");
        planeIc.setFont(new Font("Segoe UI Symbol", Font.BOLD, 22));
        planeIc.setForeground(Color.WHITE);

        JLabel routeLabel = new JLabel(fromCity + "  →  " + toCity);
        routeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        routeLabel.setForeground(Color.WHITE);

        line1.add(planeIc);
        line1.add(routeLabel);

        JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 4));
        line2.setOpaque(false);

        // Fixed [] Boxes by using standard labels
        JLabel dateLbl = new JLabel("Date: " + travelDate);
        dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLbl.setForeground(new Color(220, 235, 255));

        JLabel passLbl = new JLabel("Passengers: " + passengerCount);
        passLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passLbl.setForeground(new Color(220, 235, 255));

        line2.add(dateLbl);
        line2.add(passLbl);

        leftRoute.add(line1);
        leftRoute.add(line2);

        JPanel rightTagline = new JPanel(new GridLayout(2, 1));
        rightTagline.setOpaque(false);

        JLabel tag1 = new JLabel("\"Your Journey", SwingConstants.RIGHT);
        tag1.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        tag1.setForeground(new Color(200, 225, 255));

        JLabel tag2 = new JLabel(" Our Priority\"", SwingConstants.RIGHT);
        tag2.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 14));
        tag2.setForeground(Color.WHITE);

        rightTagline.add(tag1);
        rightTagline.add(tag2);

        banner.add(leftRoute, BorderLayout.WEST);
        banner.add(rightTagline, BorderLayout.EAST);

        add(banner, BorderLayout.NORTH);
    }

    private void createResultsArea() {
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(new Color(242, 246, 251));
        resultsPanel.setBorder(new EmptyBorder(18, 30, 20, 30));

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(242, 246, 251));
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadFlights() {
        String sql = "SELECT f.FLIGHT_ID, f.FLIGHT_NUMBER, f.DEPARTURE_TIME, f.ARRIVAL_TIME, f.PRICE, " +
                "a.AIRLINE_NAME, a.AIRLINE_CODE, src.CITY AS SOURCE_CITY, src.AIRPORT_CODE AS SOURCE_CODE, " +
                "dest.CITY AS DESTINATION_CITY, dest.AIRPORT_CODE AS DESTINATION_CODE " +
                "FROM FLIGHTS f " +
                "JOIN AIRLINES a ON f.AIRLINE_ID = a.AIRLINE_ID " +
                "JOIN AIRPORTS src ON f.SOURCE_AIRPORT_ID = src.AIRPORT_ID " +
                "JOIN AIRPORTS dest ON f.DESTINATION_AIRPORT_ID = dest.AIRPORT_ID " +
                "WHERE UPPER(src.CITY) = UPPER(?) AND UPPER(dest.CITY) = UPPER(?) " +
                "AND DATE(f.DEPARTURE_TIME) = STR_TO_DATE(?, '%d-%m-%Y') " +
                "ORDER BY f.DEPARTURE_TIME";

        Connection con = null;
        CallableStatement call = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int flightCount = 0;

        try {
            con = DBConnection.getConnection();
            if (con == null) {
                showNoFlights("Database connection failed.");
                return;
            }

            try {
                call = con.prepareCall("{call ENSURE_DAILY_FLIGHTS(?, ?, ?)}");
                call.setString(1, fromCity);
                call.setString(2, toCity);

                String[] parts = travelDate.split("-");
                if (parts.length == 3) {
                    LocalDate localDate = LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
                    call.setDate(3, java.sql.Date.valueOf(localDate));
                    call.execute();
                }
            } catch (Exception ignored) {
            } finally {
                if (call != null) try { call.close(); } catch (Exception ignored) {}
            }

            pst = con.prepareStatement(sql);
            pst.setString(1, fromCity);
            pst.setString(2, toCity);
            pst.setString(3, travelDate);

            rs = pst.executeQuery();

            while (rs.next()) {
                flightCount++;
                int flightId = rs.getInt("FLIGHT_ID");
                String flightNumber = rs.getString("FLIGHT_NUMBER");
                String airlineName = rs.getString("AIRLINE_NAME");
                String airlineCode = rs.getString("AIRLINE_CODE");
                String sourceCity = rs.getString("SOURCE_CITY");
                String sourceCode = rs.getString("SOURCE_CODE");
                String destinationCity = rs.getString("DESTINATION_CITY");
                String destinationCode = rs.getString("DESTINATION_CODE");
                String departure = formatTime(rs.getTimestamp("DEPARTURE_TIME"));
                String arrival = formatTime(rs.getTimestamp("ARRIVAL_TIME"));
                double basePrice = rs.getDouble("PRICE");
                double finalPrice = calculateDynamicPrice(basePrice);

                addFlightCard(flightId, airlineName, airlineCode, flightNumber, sourceCity, sourceCode, destinationCity, destinationCode, departure, arrival, basePrice, finalPrice);
            }

            if (flightCount == 0) {
                showNoFlights("No flights available for this route and date.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showNoFlights("Unable to load flights.\n" + ex.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (pst != null) pst.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void addFlightCard(
            int flightId, String airlineName, String airlineCode, String flightNumber,
            String sourceCity, String sourceCode, String destinationCity, String destinationCode,
            String departure, String arrival, double basePrice, double finalPrice
    ) {
        Color accentColor = getAirlineThemeColor(airlineName);

        RoundedCard card = new RoundedCard(16, Color.WHITE, new Color(225, 232, 240), accentColor);
        card.setLayout(new BorderLayout(20, 0));
        card.setMaximumSize(new Dimension(1260, 115));
        card.setPreferredSize(new Dimension(1200, 115));
        card.setBorder(new EmptyBorder(12, 18, 12, 22));

        // 1. LEFT SECTION (Larger Logo + Details)
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        left.setOpaque(false);

        JLabel logoHolder = new JLabel();
        // Increased container size for logos
        logoHolder.setPreferredSize(new Dimension(80, 60));
        loadAirlineLogo(logoHolder, airlineName);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel nameLbl = new JLabel(airlineName + " (" + airlineCode + ")");
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLbl.setForeground(new Color(15, 23, 42));

        JLabel numLbl = new JLabel("Flight " + flightNumber);
        numLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        numLbl.setForeground(new Color(100, 116, 139));

        // Removed [] box character, using clean text arrow
        JLabel routeLbl = new JLabel(sourceCity + " (" + sourceCode + ")  →  " + destinationCity + " (" + destinationCode + ")");
        routeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        routeLbl.setForeground(new Color(100, 116, 139));

        JLabel badge = new JLabel("Economy");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(accentColor);
        badge.setOpaque(true);
        badge.setBackground(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 22));
        badge.setBorder(new EmptyBorder(2, 8, 2, 8));

        details.add(nameLbl);
        details.add(Box.createVerticalStrut(2));
        details.add(numLbl);
        details.add(Box.createVerticalStrut(2));
        details.add(routeLbl);
        details.add(Box.createVerticalStrut(4));
        details.add(badge);

        left.add(logoHolder);
        left.add(details);

        // 2. MIDDLE SECTION (Timeline View)
        JPanel middle = new JPanel(new FlowLayout(FlowLayout.CENTER, 22, 18));
        middle.setOpaque(false);

        JPanel depStack = new JPanel(new GridLayout(2, 1));
        depStack.setOpaque(false);
        JLabel depTime = new JLabel(departure);
        depTime.setFont(new Font("Segoe UI", Font.BOLD, 18));
        depTime.setForeground(new Color(15, 23, 42));
        JLabel depCode = new JLabel(sourceCity + " (" + sourceCode + ")", SwingConstants.LEFT);
        depCode.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        depCode.setForeground(new Color(100, 116, 139));
        depStack.add(depTime);
        depStack.add(depCode);

        JPanel timeline = new JPanel(new GridLayout(3, 1));
        timeline.setOpaque(false);

        JLabel durLbl = new JLabel("2h 00m", SwingConstants.CENTER);
        durLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        durLbl.setForeground(new Color(100, 116, 139));

        JLabel lineGraphic = new JLabel("──── ✈ ────", SwingConstants.CENTER);
        lineGraphic.setFont(new Font("Segoe UI Symbol", Font.BOLD, 12));
        lineGraphic.setForeground(new Color(148, 163, 184));

        JLabel stopLbl = new JLabel("Non-stop", SwingConstants.CENTER);
        stopLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        stopLbl.setForeground(new Color(51, 65, 85));

        timeline.add(durLbl);
        timeline.add(lineGraphic);
        timeline.add(stopLbl);

        JPanel arrStack = new JPanel(new GridLayout(2, 1));
        arrStack.setOpaque(false);
        JLabel arrTime = new JLabel(arrival);
        arrTime.setFont(new Font("Segoe UI", Font.BOLD, 18));
        arrTime.setForeground(new Color(15, 23, 42));
        JLabel arrCode = new JLabel(destinationCity + " (" + destinationCode + ")", SwingConstants.LEFT);
        arrCode.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        arrCode.setForeground(new Color(100, 116, 139));
        arrStack.add(arrTime);
        arrStack.add(arrCode);

        middle.add(depStack);
        middle.add(timeline);
        middle.add(arrStack);

        // 3. RIGHT SECTION (Pricing & Select Button)
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 18));
        right.setOpaque(false);

        JPanel priceStack = new JPanel(new GridLayout(2, 1));
        priceStack.setOpaque(false);

        double totalCalculatedPrice = finalPrice * passengerCount;
        JLabel priceLabel = new JLabel(currencyFmt.format(totalCalculatedPrice), SwingConstants.RIGHT);
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        priceLabel.setForeground(new Color(15, 23, 42));

        JLabel baseLabel = new JLabel("Base Fare " + currencyFmt.format(basePrice), SwingConstants.RIGHT);
        baseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        baseLabel.setForeground(new Color(100, 116, 139));

        priceStack.add(priceLabel);
        priceStack.add(baseLabel);

        JButton selectButton = new JButton("SELECT FLIGHT  →") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        selectButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        selectButton.setForeground(Color.WHITE);
        selectButton.setBackground(accentColor);
        selectButton.setPreferredSize(new Dimension(150, 40));
        selectButton.setFocusPainted(false);
        selectButton.setContentAreaFilled(false);
        selectButton.setBorderPainted(false);
        selectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        selectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                selectButton.setBackground(accentColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                selectButton.setBackground(accentColor);
            }
        });

        selectButton.addActionListener(e -> {
            try {
                new SeatSelection(userId, userName, flightId, fromCity, toCity, totalCalculatedPrice).setVisible(true);
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Unable to open Seat Selection.\n" + ex.getMessage(), "Seat Selection Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        right.add(priceStack);
        right.add(selectButton);

        card.add(left, BorderLayout.WEST);
        card.add(middle, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        resultsPanel.add(card);
        resultsPanel.add(Box.createVerticalStrut(14));
    }

    private Color getAirlineThemeColor(String airlineName) {
        String name = airlineName.toLowerCase();
        if (name.contains("indigo")) return new Color(0, 102, 255);
        if (name.contains("spicejet")) return new Color(217, 39, 64);
        if (name.contains("akasa")) return new Color(110, 45, 210);
        if (name.contains("express")) return new Color(13, 148, 136);
        if (name.contains("air india")) return new Color(0, 90, 210);
        return new Color(0, 102, 255);
    }

    // Increased logo rendering bounds (76x56 px) for clear visuals
    private void loadAirlineLogo(JLabel label, String airlineName) {
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
            Image scaled = img.getScaledInstance(76, 56, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
        } else {
            label.setIcon(new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getAirlineThemeColor(airlineName));
                    g2.fillRoundRect(x, y, 76, 56, 12, 12);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    String letter = airlineName.isEmpty() ? "A" : airlineName.substring(0, 1);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(letter, x + (76 - fm.stringWidth(letter)) / 2, y + (56 + fm.getAscent() - fm.getDescent()) / 2);
                    g2.dispose();
                }

                @Override public int getIconWidth() { return 76; }
                @Override public int getIconHeight() { return 56; }
            });
        }
    }

    private double calculateDynamicPrice(double basePrice) {
        try {
            String[] parts = travelDate.split("-");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            LocalDate travel = LocalDate.of(year, month, day);
            LocalDate today = LocalDate.now();

            long days = ChronoUnit.DAYS.between(today, travel);

            if (days >= 60) return basePrice;
            if (days >= 30) return basePrice * 1.10;
            if (days >= 15) return basePrice * 1.20;
            if (days >= 7) return basePrice * 1.40;
            if (days >= 3) return basePrice * 1.60;
            if (days >= 1) return basePrice * 1.80;
            return basePrice * 2.00;

        } catch (Exception e) {
            return basePrice;
        }
    }

    private String formatTime(java.sql.Timestamp timestamp) {
        if (timestamp == null) return "--";
        java.time.LocalTime time = timestamp.toLocalDateTime().toLocalTime();
        return time.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
    }

    private void showNoFlights(String message) {
        resultsPanel.removeAll();
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(100, 116, 139));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(900, 250));
        panel.add(label);

        resultsPanel.add(panel);
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    static class RoundedCard extends JPanel {
        private final int radius;
        private final Color bg;
        private final Color border;
        private final Color leftAccent;

        public RoundedCard(int radius, Color bg, Color border, Color leftAccent) {
            this.radius = radius;
            this.bg = bg;
            this.border = border;
            this.leftAccent = leftAccent;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));

            g2.setColor(leftAccent);
            g2.fill(new RoundRectangle2D.Double(0, 0, 6, getHeight() - 1, radius, radius));
            g2.fillRect(3, 0, 4, getHeight() - 1);

            if (border != null) {
                g2.setColor(border);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlightResults(1, "Satyam", "Patna", "Delhi", "10-10-2026", 2).setVisible(true));
    }
}