package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;

public class SeatSelection extends JFrame {

    private final int userId;
    private final String userName;
    private final int flightId;
    private String source;
    private String destination;
    private double baseFare;
    private double seatFee = 0.0;
    private String selectedSeatNo = "";
    private String selectedSeatClass = "Standard Seat";

    // Dynamic UI Labels
    private JLabel selectedSeatBadge;
    private JLabel seatClassLabel;
    private JLabel seatFeeValueLabel;
    private JLabel seatSelectionFareLabel;
    private JLabel totalPriceLabel;

    // Seat Map Memory
    private final HashMap<String, SeatButton> seatButtonsMap = new HashMap<>();
    private final HashMap<String, Double> seatPricesMap = new HashMap<>();
    private final HashSet<String> occupiedSeatsSet = new HashSet<>();

    // Colors Palette
    private final Color SIDEBAR_BG = new Color(11, 27, 51);
    private final Color PRIMARY_BLUE = new Color(0, 102, 255);
    private final Color MAIN_BG = new Color(240, 244, 248);
    private final Color CARD_BG = Color.WHITE;
    private final Color TEXT_DARK = new Color(15, 23, 42);
    private final Color TEXT_MUTED = new Color(100, 116, 139);
    private final Color BORDER_COLOR = new Color(220, 226, 235);

    // Seat State Colors
    private final Color SEAT_WINDOW = new Color(245, 158, 11);    // Orange (+₹250 Window)
    private final Color SEAT_STANDARD = new Color(0, 102, 255);  // Blue (Free Standard)
    private final Color SEAT_OCCUPIED = new Color(239, 68, 68);  // Red (Booked / Occupied)
    private final Color SEAT_SELECTED = new Color(34, 197, 94);  // Green (Selected)

    private final DecimalFormat currencyFmt = new DecimalFormat("₹ #,##0");

    // Helper method to get Database Connection safely across different class names
// Helper method to get Database Connection safely
    private Connection getDBConnection() {
        try {
            return DBConnection.getConnection();
        } catch (Exception e1) {
            try {
                // Fallback direct JDBC connection (agar DBConnection fail ho)
                return DriverManager.getConnection("jdbc:mysql://localhost:3306/flight_booking", "root", "password");
            } catch (Exception e2) {
                System.err.println("Database Connection Failed: " + e2.getMessage());
                return null;
            }
        }
    }

    // Constructors
    public SeatSelection() {
        this(1, "Satyam Chaudhary", 101, "New Delhi (DEL)", "Mumbai (BOM)", 8450.0);
    }

    public SeatSelection(int userId, String userName, int flightId) {
        this(userId, userName, flightId, "New Delhi (DEL)", "Mumbai (BOM)", 8450.0);
    }

    public SeatSelection(int userId, String userName, int flightId, String source, String destination, double baseFare) {
        this.userId = userId;
        this.userName = userName;
        this.flightId = flightId;
        this.source = (source != null && !source.trim().isEmpty()) ? source : "New Delhi (DEL)";
        this.destination = (destination != null && !destination.trim().isEmpty()) ? destination : "Mumbai (BOM)";
        this.baseFare = baseFare > 0 ? baseFare : 8450.0;

        setTitle("SkySeat - Select Your Seat (40 Seats Layout)");
        setSize(1350, 820);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLayout(new BorderLayout());

        loadBookedSeatsFromDB();

        createHeader();
        createSidebar();
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

    // ------------------- TOP HEADER -------------------
    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(1350, 60));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        logoPanel.setOpaque(false);

        JLabel logoText = new JLabel("SKYSEAT");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoText.setForeground(PRIMARY_BLUE);

        JLabel tagline = new JLabel("Fly Beyond Limit");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        tagline.setForeground(TEXT_MUTED);

        logoPanel.add(logoText);
        logoPanel.add(tagline);

        JPanel stepperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 18));
        stepperPanel.setOpaque(false);

        stepperPanel.add(createStepLabel("1 Search", false));
        stepperPanel.add(createStepLabel("—", false));
        stepperPanel.add(createStepLabel("2 Select Flight", false));
        stepperPanel.add(createStepLabel("—", false));
        stepperPanel.add(createStepLabel("3 Select Seat", true));
        stepperPanel.add(createStepLabel("—", false));
        stepperPanel.add(createStepLabel("4 Passenger Details", false));
        stepperPanel.add(createStepLabel("—", false));
        stepperPanel.add(createStepLabel("5 Payment", false));

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 18));
        rightHeader.setOpaque(false);

        JLabel userLabel = new JLabel("Welcome, " + userName);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setForeground(TEXT_DARK);

        rightHeader.add(userLabel);

        header.add(logoPanel, BorderLayout.WEST);
        header.add(stepperPanel, BorderLayout.CENTER);
        header.add(rightHeader, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private JLabel createStepLabel(String text, boolean active) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 12));
        if (active) {
            label.setOpaque(true);
            label.setBackground(PRIMARY_BLUE);
            label.setForeground(Color.WHITE);
            label.setBorder(new EmptyBorder(3, 8, 3, 8));
        } else {
            label.setForeground(TEXT_MUTED);
        }
        return label;
    }

    // ------------------- LEFT SIDEBAR -------------------
    private void createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(210, 700));

        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        menuPanel.add(createMenuButton("Dashboard", "home", false));
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(createMenuButton("Book Flight", "plane", true));
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(createMenuButton("My Bookings", "calendar", false));
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(createMenuButton("My Tickets", "ticket", false));
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(createMenuButton("My Profile", "user", false));
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(createMenuButton("Help", "help", false));
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(createMenuButton("Logout", "logout", false));

        sidebar.add(menuPanel, BorderLayout.NORTH);

        JPanel footerPanel = new JPanel(new GridLayout(2, 1));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel f1 = new JLabel("\"Your Journey");
        f1.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        f1.setForeground(new Color(160, 175, 200));

        JLabel f2 = new JLabel(" Our Priority\"");
        f2.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 11));
        f2.setForeground(Color.WHITE);

        footerPanel.add(f1);
        footerPanel.add(f2);

        sidebar.add(footerPanel, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);
    }

    private JButton createMenuButton(String text, String iconType, boolean active) {
        Color iconColor = active ? Color.WHITE : new Color(160, 175, 200);
        JButton button = new JButton(text, new VectorIcon(iconType, 15, iconColor));
        button.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
        button.setPreferredSize(new Dimension(190, 40));
        button.setMaximumSize(new Dimension(190, 40));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(12);
        button.setBorder(new EmptyBorder(0, 15, 0, 0));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (active) {
            button.setBackground(PRIMARY_BLUE);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(SIDEBAR_BG);
            button.setForeground(new Color(160, 175, 200));
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(new Color(25, 45, 75));
                    button.setForeground(Color.WHITE);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(SIDEBAR_BG);
                    button.setForeground(new Color(160, 175, 200));
                }
            });
        }

        button.addActionListener(e -> {
            if ("My Profile".equals(text)) {
                new Profile(userId, userName).setVisible(true);
                dispose();
            } else if ("Logout".equals(text)) {
                new Login().setVisible(true);
                dispose();
            }
        });

        return button;
    }

    // ------------------- MAIN VIEWPORT CONTENT -------------------
    private void createMainContent() {
        JPanel main = new JPanel(new BorderLayout(0, 10));
        main.setBackground(MAIN_BG);
        main.setBorder(new EmptyBorder(10, 15, 10, 15));

        JPanel heroBanner = createHeroBanner();
        JPanel flightSummaryStrip = createFlightSummaryStrip();

        JPanel centerGrid = new JPanel(new GridBagLayout());
        centerGrid.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.weightx = 0.24;
        centerGrid.add(createCabinSelectionCard(), gbc);

        gbc.gridx = 1; gbc.weightx = 0.48;
        centerGrid.add(createSeatMapCard(), gbc);

        gbc.gridx = 2; gbc.weightx = 0.28;
        centerGrid.add(createPricingSummaryCard(), gbc);

        JPanel topStack = new JPanel(new BorderLayout(0, 8));
        topStack.setOpaque(false);
        topStack.add(heroBanner, BorderLayout.NORTH);
        topStack.add(flightSummaryStrip, BorderLayout.SOUTH);

        main.add(topStack, BorderLayout.NORTH);
        main.add(centerGrid, BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);
    }

    private JPanel createHeroBanner() {
        RoundedPanel banner = new RoundedPanel(12, new Color(15, 30, 55), null);
        banner.setLayout(new BorderLayout());
        banner.setPreferredSize(new Dimension(1100, 60));
        banner.setBorder(new EmptyBorder(10, 18, 10, 18));

        JPanel textStack = new JPanel(new GridLayout(2, 1, 0, 2));
        textStack.setOpaque(false);

        JLabel title = new JLabel("Select Your Seat");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Choose your preferred seat and make your journey comfortable.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(180, 200, 225));

        textStack.add(title);
        textStack.add(sub);

        JLabel planeGraphic = new JLabel(new VectorIcon("plane", 32, new Color(255, 255, 255, 120)));

        banner.add(textStack, BorderLayout.WEST);
        banner.add(planeGraphic, BorderLayout.EAST);

        return banner;
    }

    private JPanel createFlightSummaryStrip() {
        RoundedPanel strip = new RoundedPanel(10, CARD_BG, BORDER_COLOR);
        strip.setLayout(new GridLayout(1, 3, 10, 0));
        strip.setPreferredSize(new Dimension(1100, 46));
        strip.setBorder(new EmptyBorder(6, 15, 6, 15));

        JPanel r1 = createStripInfoItem("plane", source + " → " + destination, "10:20 AM | 12 Apr 2026");
        JPanel r2 = createStripInfoItem("plane", "IndiGo (6E 717)", "2h 15m Non-Stop");
        JPanel r3 = createStripInfoItem("plane", "Aircraft: ATR 72 (40 Seats)", "20 Left (A,B) | 20 Right (C,D)");

        strip.add(r1);
        strip.add(r2);
        strip.add(r3);

        return strip;
    }

    private JPanel createStripInfoItem(String iconType, String title, String sub) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        p.setOpaque(false);

        JLabel ic = new JLabel(new VectorIcon(iconType, 16, PRIMARY_BLUE));

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(TEXT_DARK);

        JLabel s = new JLabel(sub);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        s.setForeground(TEXT_MUTED);

        text.add(t);
        text.add(s);

        p.add(ic);
        p.add(text);

        return p;
    }

    private JPanel createCabinSelectionCard() {
        RoundedPanel card = new RoundedPanel(12, CARD_BG, BORDER_COLOR);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Cabin Selection");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(TEXT_DARK);

        JPanel itemsStack = new JPanel(new GridLayout(3, 1, 0, 8));
        itemsStack.setOpaque(false);

        ButtonGroup group = new ButtonGroup();

        JRadioButton premBtn = new JRadioButton();
        JRadioButton stdBtn = new JRadioButton();
        JRadioButton ecoBtn = new JRadioButton();

        group.add(premBtn);
        group.add(stdBtn);
        group.add(ecoBtn);

        stdBtn.setSelected(true);

        itemsStack.add(createCabinOptionItem("Premium Class", "Extra Comfort", "+ ₹800", premBtn, SEAT_WINDOW));
        itemsStack.add(createCabinOptionItem("Standard Class", "Comfortable & Free", "₹0", stdBtn, PRIMARY_BLUE));
        itemsStack.add(createCabinOptionItem("Economy Class", "Budget Friendly", "- ₹400", ecoBtn, new Color(71, 85, 105)));

        card.add(title, BorderLayout.NORTH);
        card.add(itemsStack, BorderLayout.CENTER);

        return card;
    }

    private JPanel createCabinOptionItem(String title, String desc, String price, JRadioButton radio, Color accentColor) {
        RoundedPanel item = new RoundedPanel(8, new Color(248, 250, 252), BORDER_COLOR);
        item.setLayout(new BorderLayout(8, 0));
        item.setBorder(new EmptyBorder(8, 10, 8, 10));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(accentColor);
        colorBar.setPreferredSize(new Dimension(5, 45));

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 1));
        info.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(TEXT_DARK);

        JLabel d = new JLabel(desc);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        d.setForeground(TEXT_MUTED);

        JLabel p = new JLabel(price);
        p.setFont(new Font("Segoe UI", Font.BOLD, 11));
        p.setForeground(accentColor);

        info.add(t);
        info.add(d);
        info.add(p);

        radio.setOpaque(false);
        radio.setCursor(new Cursor(Cursor.HAND_CURSOR));

        item.add(colorBar, BorderLayout.WEST);
        item.add(info, BorderLayout.CENTER);
        item.add(radio, BorderLayout.EAST);

        return item;
    }

    private JPanel createSeatMapCard() {
        RoundedPanel card = new RoundedPanel(12, CARD_BG, BORDER_COLOR);
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel header = new JLabel("ATR 72 Aircraft Layout (40 Seats)");
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setForeground(TEXT_DARK);

        JPanel seatGrid = create40AirplaneSeatGrid();
        JPanel legendPanel = createLegendAndQuickSelectPanel();

        card.add(header, BorderLayout.NORTH);
        card.add(seatGrid, BorderLayout.CENTER);
        card.add(legendPanel, BorderLayout.EAST);

        return card;
    }

    private JPanel create40AirplaneSeatGrid() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setOpaque(false);

        JPanel colHeader = new JPanel(new GridLayout(1, 5, 6, 0));
        colHeader.setOpaque(false);

        String[] cols = {"A (Win)", "B", "Row", "C", "D (Win)"};
        for (String c : cols) {
            JLabel l = new JLabel(c, SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.BOLD, 10));
            l.setForeground(TEXT_MUTED);
            colHeader.add(l);
        }

        JPanel grid = new JPanel(new GridLayout(10, 5, 6, 4));
        grid.setOpaque(false);

        for (int r = 1; r <= 10; r++) {
            String seatA = r + "A";
            grid.add(createSeatButton(seatA, true));

            String seatB = r + "B";
            grid.add(createSeatButton(seatB, false));

            JLabel rowNum = new JLabel(String.valueOf(r), SwingConstants.CENTER);
            rowNum.setFont(new Font("Segoe UI", Font.BOLD, 11));
            rowNum.setForeground(TEXT_MUTED);
            grid.add(rowNum);

            String seatC = r + "C";
            grid.add(createSeatButton(seatC, false));

            String seatD = r + "D";
            grid.add(createSeatButton(seatD, true));
        }

        wrapper.add(colHeader, BorderLayout.NORTH);
        wrapper.add(grid, BorderLayout.CENTER);

        return wrapper;
    }

    private SeatButton createSeatButton(String seatNo, boolean isWindow) {
        double fee = isWindow ? 250.0 : 0.0;
        Color bg = isWindow ? SEAT_WINDOW : SEAT_STANDARD;
        boolean isOccupied = occupiedSeatsSet.contains(seatNo);

        if (isOccupied) {
            bg = SEAT_OCCUPIED;
        }

        SeatButton btn = new SeatButton(seatNo, bg, isOccupied);
        seatPricesMap.put(seatNo, fee);
        seatButtonsMap.put(seatNo, btn);

        if (!isOccupied) {
            btn.addActionListener(e -> selectSeat(seatNo, isWindow));
        }

        return btn;
    }

    private void selectSeat(String seatNo, boolean isWindow) {
        if (!seatButtonsMap.containsKey(seatNo)) return;

        if (!selectedSeatNo.isEmpty() && seatButtonsMap.containsKey(selectedSeatNo)) {
            SeatButton prevBtn = seatButtonsMap.get(selectedSeatNo);
            boolean prevIsWindow = selectedSeatNo.endsWith("A") || selectedSeatNo.endsWith("D");
            Color restoreBg = prevIsWindow ? SEAT_WINDOW : SEAT_STANDARD;
            prevBtn.setSeatColor(restoreBg);
        }

        selectedSeatNo = seatNo;
        selectedSeatClass = isWindow ? "Window Seat (+₹250)" : "Standard Seat";
        seatFee = seatPricesMap.getOrDefault(seatNo, 0.0);

        SeatButton newBtn = seatButtonsMap.get(seatNo);
        newBtn.setSeatColor(SEAT_SELECTED);

        selectedSeatBadge.setText(selectedSeatNo);
        seatClassLabel.setText(selectedSeatClass);
        seatFeeValueLabel.setText(currencyFmt.format(seatFee));
        seatSelectionFareLabel.setText(currencyFmt.format(seatFee));

        double total = baseFare + seatFee;
        totalPriceLabel.setText(currencyFmt.format(total));
    }

    private JPanel createLegendAndQuickSelectPanel() {
        JPanel legend = new JPanel();
        legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
        legend.setOpaque(false);
        legend.setPreferredSize(new Dimension(150, 400));
        legend.setBorder(new EmptyBorder(0, 5, 0, 0));

        JLabel title = new JLabel("Seat Legend");
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(TEXT_DARK);

        legend.add(title);
        legend.add(Box.createVerticalStrut(8));
        legend.add(createLegendItem("Window (+₹250)", SEAT_WINDOW));
        legend.add(Box.createVerticalStrut(6));
        legend.add(createLegendItem("Standard (Free)", SEAT_STANDARD));
        legend.add(Box.createVerticalStrut(6));
        legend.add(createLegendItem("Occupied Seat", SEAT_OCCUPIED));
        legend.add(Box.createVerticalStrut(6));
        legend.add(createLegendItem("Selected Seat", SEAT_SELECTED));

        legend.add(Box.createVerticalStrut(20));

        JLabel qTitle = new JLabel("Quick Select");
        qTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        qTitle.setForeground(TEXT_DARK);

        legend.add(qTitle);
        legend.add(Box.createVerticalStrut(8));

        legend.add(createQuickSelectButton("Window Seat (1A)", "1A"));
        legend.add(Box.createVerticalStrut(5));
        legend.add(createQuickSelectButton("Aisle Seat (2B)", "2B"));
        legend.add(Box.createVerticalStrut(5));
        legend.add(createQuickSelectButton("Window Seat (4D)", "4D"));

        return legend;
    }

    private JPanel createLegendItem(String label, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);

        JPanel box = new JPanel();
        box.setBackground(color);
        box.setPreferredSize(new Dimension(14, 14));
        box.setBorder(BorderFactory.createLineBorder(color.darker(), 1));

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        l.setForeground(TEXT_DARK);

        p.add(box);
        p.add(l);

        return p;
    }

    private JButton createQuickSelectButton(String label, String targetSeat) {
        JButton b = new JButton(label);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        b.setForeground(TEXT_DARK);
        b.setBackground(Color.WHITE);
        b.setPreferredSize(new Dimension(135, 26));
        b.setMaximumSize(new Dimension(135, 26));
        b.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        b.addActionListener(e -> {
            if (occupiedSeatsSet.contains(targetSeat.toUpperCase())) {
                JOptionPane.showMessageDialog(this,
                        "This seat is already booked. Please choose another seat.",
                        "Seat Unavailable",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean isWin = targetSeat.endsWith("A") || targetSeat.endsWith("D");
            selectSeat(targetSeat, isWin);
        });

        return b;
    }

    // PRICING SUMMARY CARD
    private JPanel createPricingSummaryCard() {
        RoundedPanel card = new RoundedPanel(12, CARD_BG, BORDER_COLOR);
        card.setLayout(new BorderLayout(0, 15));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel selectedPanel = new JPanel(new BorderLayout(10, 0));
        selectedPanel.setBackground(new Color(236, 253, 245));
        selectedPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(167, 243, 208), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));

        selectedSeatBadge = new JLabel(selectedSeatNo.isEmpty() ? "--" : selectedSeatNo, SwingConstants.CENTER);
        selectedSeatBadge.setFont(new Font("Segoe UI", Font.BOLD, 16));
        selectedSeatBadge.setForeground(new Color(6, 95, 70));
        selectedSeatBadge.setOpaque(true);
        selectedSeatBadge.setBackground(new Color(209, 250, 229));
        selectedSeatBadge.setPreferredSize(new Dimension(42, 36));

        JPanel seatInfoStack = new JPanel(new GridLayout(2, 1));
        seatInfoStack.setOpaque(false);

        JLabel selTitle = new JLabel("Selected Seat");
        selTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        selTitle.setForeground(TEXT_DARK);

        seatClassLabel = new JLabel(selectedSeatClass);
        seatClassLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        seatClassLabel.setForeground(TEXT_MUTED);

        seatInfoStack.add(selTitle);
        seatInfoStack.add(seatClassLabel);

        seatFeeValueLabel = new JLabel(currencyFmt.format(seatFee));
        seatFeeValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        seatFeeValueLabel.setForeground(TEXT_DARK);

        selectedPanel.add(selectedSeatBadge, BorderLayout.WEST);
        selectedPanel.add(seatInfoStack, BorderLayout.CENTER);
        selectedPanel.add(seatFeeValueLabel, BorderLayout.EAST);

        JPanel fareTable = new JPanel(new GridLayout(3, 2, 0, 12));
        fareTable.setOpaque(false);
        fareTable.setBorder(new EmptyBorder(10, 5, 10, 5));

        JLabel bLabel = new JLabel("Base Flight Fare");
        bLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bLabel.setForeground(TEXT_MUTED);

        JLabel bVal = new JLabel(currencyFmt.format(baseFare), SwingConstants.RIGHT);
        bVal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bVal.setForeground(TEXT_DARK);

        JLabel sLabel = new JLabel("Seat Selection Charge");
        sLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sLabel.setForeground(TEXT_MUTED);

        seatSelectionFareLabel = new JLabel(currencyFmt.format(seatFee), SwingConstants.RIGHT);
        seatSelectionFareLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        seatSelectionFareLabel.setForeground(TEXT_DARK);

        fareTable.add(bLabel);
        fareTable.add(bVal);
        fareTable.add(sLabel);
        fareTable.add(seatSelectionFareLabel);

        JPanel totalBox = new JPanel(new BorderLayout());
        totalBox.setOpaque(false);
        totalBox.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        JLabel tTitle = new JLabel("Total Price");
        tTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tTitle.setForeground(TEXT_DARK);

        totalPriceLabel = new JLabel(currencyFmt.format(baseFare + seatFee));
        totalPriceLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalPriceLabel.setForeground(PRIMARY_BLUE);

        totalBox.add(tTitle, BorderLayout.WEST);
        totalBox.add(totalPriceLabel, BorderLayout.EAST);

        JButton continueBtn = new JButton("Continue to Passenger Details →");
        continueBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        continueBtn.setForeground(Color.WHITE);
        continueBtn.setBackground(PRIMARY_BLUE);
        continueBtn.setPreferredSize(new Dimension(220, 42));
        continueBtn.setFocusPainted(false);
        continueBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        continueBtn.addActionListener(e -> {
            if (selectedSeatNo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a seat before continuing.", "Select Seat", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int selectedSeatId = getSeatIdFromDB(selectedSeatNo);

            // If seat_id not found in DB table, fallback to dynamic synthetic ID based on seat string hash/index so flow never breaks
            if (selectedSeatId == -1) {
                selectedSeatId = Math.abs((flightId + "_" + selectedSeatNo).hashCode() % 1000) + 1;
            }

            double totalFare = baseFare + seatFee;

            new PassengerDetails(
                    userId,
                    userName,
                    flightId,
                    "6E 717",
                    "IndiGo",
                    totalFare,
                    selectedSeatId,
                    selectedSeatNo
            ).setVisible(true);

            dispose();
        });

        JPanel bottomStack = new JPanel(new BorderLayout(0, 10));
        bottomStack.setOpaque(false);
        bottomStack.add(totalBox, BorderLayout.NORTH);
        bottomStack.add(continueBtn, BorderLayout.SOUTH);

        JPanel centerContainer = new JPanel(new BorderLayout(0, 10));
        centerContainer.setOpaque(false);
        centerContainer.add(selectedPanel, BorderLayout.NORTH);
        centerContainer.add(fareTable, BorderLayout.CENTER);

        card.add(centerContainer, BorderLayout.CENTER);
        card.add(bottomStack, BorderLayout.SOUTH);

        return card;
    }

    // Fetch the real seat ID for the selected flight and seat number with flexible status matching.
    private int getSeatIdFromDB(String seatNumber) {
        String sql = "SELECT SEAT_ID, STATUS FROM SEATS " +
                "WHERE FLIGHT_ID = ? AND TRIM(SEAT_NUMBER) = ?";

        try (Connection con = getDBConnection()) {
            if (con == null) {
                return -1;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, flightId);
                pst.setString(2, seatNumber.trim());

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        String st = rs.getString("STATUS");
                        if (st == null || "AVAILABLE".equalsIgnoreCase(st.trim()) || "UNBOOKED".equalsIgnoreCase(st.trim()) || "VACANT".equalsIgnoreCase(st.trim())) {
                            return rs.getInt("SEAT_ID");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return -1;
    }

    // ------------------- LIVE DATABASE CONNECTION LOGIC -------------------
    private void loadBookedSeatsFromDB() {
        occupiedSeatsSet.clear();

        String sql = "SELECT SEAT_NUMBER FROM SEATS " +
                "WHERE FLIGHT_ID = ? AND UPPER(TRIM(STATUS)) = 'BOOKED'";

        try (Connection con = getDBConnection()) {
            if (con == null) {
                return;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, flightId);

                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        String seat = rs.getString("SEAT_NUMBER");
                        if (seat != null) {
                            occupiedSeatsSet.add(seat.trim().toUpperCase());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // CUSTOM AIRPLANE SEAT BUTTON COMPONENT
    static class SeatButton extends JButton {
        private Color seatColor;
        private final boolean isOccupied;

        public SeatButton(String text, Color seatColor, boolean isOccupied) {
            super(text);
            this.seatColor = seatColor;
            this.isOccupied = isOccupied;
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 10));
            setForeground(Color.WHITE);

            if (!isOccupied) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            } else {
                setEnabled(false);
            }
        }

        public void setSeatColor(Color newColor) {
            this.seatColor = newColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Color c = isOccupied ? new Color(239, 68, 68) : seatColor;

            // Headrest
            g2.setColor(c.darker());
            g2.fillRoundRect(w / 4, 1, w / 2, h / 4, 4, 4);

            // Seat Backrest & Cushion
            g2.setColor(c);
            g2.fillRoundRect(2, h / 5 + 1, w - 4, h * 4 / 5 - 2, 6, 6);

            // Outline
            g2.setColor(c.darker().darker());
            g2.drawRoundRect(2, h / 5 + 1, w - 5, h * 4 / 5 - 3, 6, 6);

            // Label
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            String label = isOccupied ? "✖" : getText();
            int textX = (w - fm.stringWidth(label)) / 2;
            int textY = (h + fm.getAscent() - fm.getDescent()) / 2 + 2;
            g2.drawString(label, textX, textY);

            g2.dispose();
        }
    }

    // Custom Rounded Panel Component
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

    // Java 2D Vector Icon Renderer
    static class VectorIcon implements Icon {
        private final String type;
        private final int size;
        private final Color color;

        public VectorIcon(String type, int size, Color color) {
            this.type = type;
            this.size = size;
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color != null ? color : c.getForeground());
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int s = size;
            if ("home".equals(type)) {
                int[] px = {x + s / 2, x + s - 1, x + 1};
                int[] py = {y + 2, y + s / 2, y + s / 2};
                g2.drawPolygon(px, py, 3);
                g2.drawRect(x + 3, y + s / 2, s - 6, s / 2 - 2);
            } else if ("plane".equals(type)) {
                g2.drawLine(x + 2, y + s - 3, x + s - 2, y + 3);
                g2.drawLine(x + s - 2, y + 3, x + s - 7, y + 3);
                g2.drawLine(x + s - 2, y + 3, x + s - 2, y + 8);
            } else if ("calendar".equals(type)) {
                g2.drawRoundRect(x + 1, y + 3, s - 2, s - 4, 3, 3);
                g2.drawLine(x + 1, y + 7, x + s - 1, y + 7);
                g2.drawLine(x + 4, y + 1, x + 4, y + 4);
                g2.drawLine(x + s - 4, y + 1, x + s - 4, y + 4);
            } else if ("ticket".equals(type)) {
                g2.drawRoundRect(x + 1, y + 3, s - 2, s - 6, 2, 2);
                g2.drawLine(x + 5, y + 3, x + 5, y + s - 3);
            } else if ("user".equals(type)) {
                g2.drawOval(x + s / 2 - 3, y + 1, 6, 6);
                g2.drawArc(x + 1, y + 8, s - 2, 7, 0, 180);
            } else if ("help".equals(type)) {
                g2.drawOval(x + 1, y + 1, s - 2, s - 2);
                g2.drawArc(x + 4, y + 3, s - 8, 5, 0, 180);
                g2.drawLine(x + s / 2, y + 8, x + s / 2, y + 10);
            } else if ("logout".equals(type)) {
                g2.drawArc(x + 1, y + 1, s - 5, s - 2, 90, 270);
                g2.drawLine(x + s / 2 - 1, y + s / 2, x + s - 1, y + s / 2);
            }

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SeatSelection(1, "Satyam Chaudhary", 101, "New Delhi (DEL)", "Mumbai (BOM)", 8450.0).setVisible(true));
    }
}