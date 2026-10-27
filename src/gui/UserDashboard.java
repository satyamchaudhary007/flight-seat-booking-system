package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class UserDashboard extends JFrame {

    private final int userId;
    private final String userName;

    private IconTextField fromField;
    private IconTextField toField;
    private IconTextField dateField;
    private JComboBox<String> passengersCombo;

    private JPopupMenu fromPopup;
    private JPopupMenu toPopup;

    private JList<String> fromList;
    private JList<String> toList;

    private JPopupMenu calendarPopup;

    private YearMonth currentMonth;
    private JPanel calendarDaysPanel;
    private JLabel monthYearLabel;

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Design Colors
    private final Color SIDEBAR_BG = new Color(11, 27, 51);
    private final Color PRIMARY_BLUE = new Color(0, 102, 255);
    private final Color PRIMARY_BLUE_HOVER = new Color(0, 85, 220);
    private final Color MAIN_BG = new Color(245, 247, 250);
    private final Color CARD_BORDER = new Color(226, 232, 240);
    private final Color TEXT_DARK = new Color(15, 23, 42);
    private final Color TEXT_MUTED = new Color(100, 116, 139);

    public UserDashboard(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;

        setTitle("SkySeat - Flight Booking Dashboard");
        setSize(1350, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Enforce Full Screen Maximized State
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLayout(new BorderLayout());

        createHeader();
        createSidebar();
        createDashboard();
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

    // ------------------- HEADER -------------------
    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(1350, 65));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BORDER));

        // Left Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 18));
        logoPanel.setOpaque(false);

        JLabel logoIcon = new JLabel(new VectorIcon("plane", 22, PRIMARY_BLUE));
        JLabel logoText = new JLabel("SKYSEAT");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoText.setForeground(TEXT_DARK);

        logoPanel.add(logoIcon);
        logoPanel.add(logoText);

        // Center Tagline Pill
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        centerPanel.setOpaque(false);

        RoundedPanel pill = new RoundedPanel(20, new Color(241, 245, 249), null);
        pill.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel pillIcon = new JLabel(new VectorIcon("plane", 12, TEXT_MUTED));
        JLabel pillText = new JLabel("Fly More. Explore More.");
        pillText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pillText.setForeground(TEXT_MUTED);

        pill.add(pillIcon);
        pill.add(pillText);
        centerPanel.add(pill);

        // Right User Profile Panel (CLICKABLE)
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        profilePanel.setOpaque(false);
        profilePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Bell Notification Button
        JLabel bellIcon = new JLabel(new VectorIcon("bell", 20, TEXT_DARK));
        bellIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // User Avatar Circle
        JLabel avatarCircle = new JLabel(new VectorIcon("user", 16, Color.WHITE), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_BLUE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatarCircle.setPreferredSize(new Dimension(32, 32));

        JPanel userTextPanel = new JPanel(new GridLayout(2, 1));
        userTextPanel.setOpaque(false);
        JLabel welcomeSub = new JLabel("Welcome,");
        welcomeSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        welcomeSub.setForeground(TEXT_MUTED);

        JLabel welcomeName = new JLabel(userName);
        welcomeName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        welcomeName.setForeground(TEXT_DARK);

        userTextPanel.add(welcomeSub);
        userTextPanel.add(welcomeName);

        JLabel dropdownArrow = new JLabel(new VectorIcon("arrow_down", 10, TEXT_DARK));

        profilePanel.add(bellIcon);
        profilePanel.add(Box.createHorizontalStrut(8));
        profilePanel.add(avatarCircle);
        profilePanel.add(userTextPanel);
        profilePanel.add(dropdownArrow);

        // Profile Click Event
        MouseAdapter openProfileAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Profile(userId, userName).setVisible(true);
            }
        };
        profilePanel.addMouseListener(openProfileAdapter);
        avatarCircle.addMouseListener(openProfileAdapter);
        userTextPanel.addMouseListener(openProfileAdapter);

        header.add(logoPanel, BorderLayout.WEST);
        header.add(centerPanel, BorderLayout.CENTER);
        header.add(profilePanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    // ------------------- SIDEBAR -------------------
    private void createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(220, 700));

        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(15, 10, 15, 10));

        // Saare Left Sidebar Buttons
        JButton dashboardButton = createMenuButton("Dashboard", "home", true);
        JButton bookFlightButton = createMenuButton("Book Flight", "plane", false);
        JButton bookingsButton = createMenuButton("My Bookings", "calendar", false);
        JButton ticketsButton = createMenuButton("My Tickets", "ticket", false);
        JButton profileButton = createMenuButton("My Profile", "user", false);
        JButton helpButton = createMenuButton("Help", "help", false);
        JButton logoutButton = createMenuButton("Logout", "logout", false);

        menuPanel.add(dashboardButton);
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(bookFlightButton);
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(bookingsButton);
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(ticketsButton);
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(profileButton);
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(helpButton);
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(logoutButton);

        sidebar.add(menuPanel, BorderLayout.NORTH);

        // Sidebar Footer Graphics
        JPanel footerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(255, 255, 255, 40));
                Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4.0f}, 0.0f);
                g2.setStroke(dashed);

                Path2D path = new Path2D.Double();
                path.moveTo(20, getHeight() - 20);
                path.curveTo(60, getHeight() - 60, 120, getHeight() - 10, 180, getHeight() - 50);
                g2.draw(path);

                Icon smallPlane = new VectorIcon("plane", 14, new Color(0, 102, 255));
                smallPlane.paintIcon(this, g2, 15, getHeight() - 75);

                g2.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                g2.setColor(new Color(160, 175, 200));
                g2.drawString("Your journey", 20, getHeight() - 35);
                g2.drawString("starts here...", 20, getHeight() - 20);

                g2.dispose();
            }
        };
        footerPanel.setPreferredSize(new Dimension(220, 110));
        footerPanel.setOpaque(false);

        sidebar.add(footerPanel, BorderLayout.SOUTH);

        // Button Action Listeners
        bookFlightButton.addActionListener(e -> {
            if (fromField != null) {
                fromField.requestFocusInWindow();
            }
        });
        bookingsButton.addActionListener(e -> new MyBookings(userId, userName).setVisible(true));
        ticketsButton.addActionListener(e -> new MyBookings(userId, userName).setVisible(true));
        profileButton.addActionListener(e -> new Profile(userId, userName).setVisible(true));
        helpButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "For any help or query, please contact support@skyseat.com", "SkySeat Help", JOptionPane.INFORMATION_MESSAGE));

        logoutButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                new Login().setVisible(true);
                dispose();
            }
        });

        add(sidebar, BorderLayout.WEST);
    }

    private JButton createMenuButton(String text, String iconType, boolean active) {
        Color iconColor = active ? Color.WHITE : new Color(160, 175, 200);
        JButton button = new JButton(text, new VectorIcon(iconType, 18, iconColor));
        button.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(195, 40));
        button.setMaximumSize(new Dimension(195, 40));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(15);
        button.setBorder(new EmptyBorder(0, 18, 0, 0));
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
                    button.setBackground(new Color(20, 40, 70));
                    button.setForeground(Color.WHITE);
                    button.setIcon(new VectorIcon(iconType, 18, Color.WHITE));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(SIDEBAR_BG);
                    button.setForeground(new Color(160, 175, 200));
                    button.setIcon(new VectorIcon(iconType, 18, new Color(160, 175, 200)));
                }
            });
        }
        return button;
    }

    // ------------------- MAIN CONTENT -------------------
    private void createDashboard() {
        JPanel main = new JPanel(new BorderLayout(0, 15));
        main.setBackground(MAIN_BG);

        JPanel contentStack = new JPanel();
        contentStack.setLayout(new BoxLayout(contentStack, BoxLayout.Y_AXIS));
        contentStack.setOpaque(false);
        contentStack.setBorder(new EmptyBorder(20, 25, 20, 25));

        contentStack.add(createHeroBanner());
        contentStack.add(Box.createVerticalStrut(20));

        contentStack.add(createSearchCard());
        contentStack.add(Box.createVerticalStrut(20));

        contentStack.add(createBottomGrid());

        JScrollPane scrollPane = new JScrollPane(contentStack);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        main.add(scrollPane, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
    }

    // ------------------- HERO BANNER -------------------
    private JPanel createHeroBanner() {
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(220, 235, 252),
                        getWidth(), getHeight(), new Color(255, 220, 200)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(new Color(255, 255, 255, 120));
                g2.fillOval(w - 300, -50, 400, 200);

                Path2D wing = new Path2D.Double();
                wing.moveTo(w, h * 0.1);
                wing.lineTo(w - 220, h * 0.95);
                wing.lineTo(w - 180, h);
                wing.lineTo(w, h * 0.6);
                wing.closePath();

                g2.setColor(new Color(20, 35, 60));
                g2.fill(wing);

                Path2D tip = new Path2D.Double();
                tip.moveTo(w - 220, h * 0.95);
                tip.lineTo(w - 235, h * 0.3);
                tip.lineTo(w - 215, h * 0.45);
                tip.closePath();
                g2.setColor(new Color(220, 38, 38));
                g2.fill(tip);

                g2.dispose();
            }
        };

        banner.setPreferredSize(new Dimension(1000, 140));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        banner.setBorder(new EmptyBorder(22, 25, 22, 25));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel welcomeSub = new JLabel("WELCOME BACK,");
        welcomeSub.setFont(new Font("Segoe UI", Font.BOLD, 12));
        welcomeSub.setForeground(new Color(71, 85, 105));

        JLabel welcomeTitle = new JLabel(userName);
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeTitle.setForeground(TEXT_DARK);

        JLabel desc1 = new JLabel("Plan your next adventure with ease.");
        desc1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc1.setForeground(new Color(51, 65, 85));

        JLabel desc2 = new JLabel("Book flights, manage your trips, and fly with confidence.");
        desc2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc2.setForeground(new Color(51, 65, 85));

        textPanel.add(welcomeSub);
        textPanel.add(welcomeTitle);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(desc1);
        textPanel.add(desc2);

        banner.add(textPanel, BorderLayout.WEST);

        return banner;
    }

    // ------------------- SEARCH CARD -------------------
    private JPanel createSearchCard() {
        RoundedPanel card = new RoundedPanel(16, Color.WHITE, CARD_BORDER);
        card.setLayout(new BorderLayout(0, 15));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel cardHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        cardHeader.setOpaque(false);

        JLabel iconCircle = createIconCircle("plane", PRIMARY_BLUE, Color.WHITE);

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);
        JLabel title = new JLabel("Search Your Flight");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(TEXT_DARK);

        JLabel subTitle = new JLabel("Find the best flights at the best prices");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subTitle.setForeground(TEXT_MUTED);

        titleBox.add(title);
        titleBox.add(subTitle);

        cardHeader.add(iconCircle);
        cardHeader.add(titleBox);

        card.add(cardHeader, BorderLayout.NORTH);

        JPanel inputsPanel = new JPanel(new GridBagLayout());
        inputsPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 4, 0, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        fromField = new IconTextField("Enter departure city or airport", "pin");
        toField = new IconTextField("Enter arrival city or airport", "pin");
        dateField = new IconTextField("Select date", "calendar");
        dateField.setEditable(false);
        dateField.setCursor(new Cursor(Cursor.HAND_CURSOR));

        passengersCombo = new JComboBox<>(new String[]{"1 Passenger", "2 Passengers", "3 Passengers", "4 Passengers", "5 Passengers"});
        passengersCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passengersCombo.setPreferredSize(new Dimension(140, 40));
        passengersCombo.setBackground(Color.WHITE);

        RoundedButton searchButton = new RoundedButton("Search Flights →", "search", PRIMARY_BLUE, PRIMARY_BLUE_HOVER, Color.WHITE, 10);
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchButton.setPreferredSize(new Dimension(160, 42));

        gbc.gridx = 0; gbc.weightx = 0.25; inputsPanel.add(createFieldWrapper("From", fromField), gbc);
        gbc.gridx = 1; gbc.weightx = 0.25; inputsPanel.add(createFieldWrapper("To", toField), gbc);
        gbc.gridx = 2; gbc.weightx = 0.20; inputsPanel.add(createFieldWrapper("Date", dateField), gbc);
        gbc.gridx = 3; gbc.weightx = 0.15; inputsPanel.add(createFieldWrapper("Passengers", passengersCombo), gbc);

        gbc.gridx = 4; gbc.weightx = 0.15;
        JPanel btnWrapper = new JPanel(new BorderLayout());
        btnWrapper.setOpaque(false);
        JLabel emptyLabel = new JLabel(" ");
        emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnWrapper.add(emptyLabel, BorderLayout.NORTH);
        btnWrapper.add(searchButton, BorderLayout.SOUTH);
        inputsPanel.add(btnWrapper, gbc);

        card.add(inputsPanel, BorderLayout.CENTER);

        setupAutoComplete(fromField, true);
        setupAutoComplete(toField, false);
        setupCalendar(dateField);

        searchButton.addActionListener(e -> {
            String from = fromField.getText().trim();
            String to = toField.getText().trim();
            String date = dateField.getText().trim();
            int passengerCount = passengersCombo.getSelectedIndex() + 1;

            if (from.isEmpty() || to.isEmpty() || date.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter From, To and Date.", "Flight Search", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                FlightResults flightResults = new FlightResults(userId, userName, from, to, date, passengerCount);
                flightResults.setVisible(true);
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Unable to open Flight Results.\n" + ex.getMessage(), "Flight Search Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return card;
    }

    private JPanel createFieldWrapper(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_MUTED);

        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);

        return panel;
    }

    // ------------------- BOTTOM GRID -------------------
    private JPanel createBottomGrid() {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 6, 0, 6);
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.weightx = 0.38;
        grid.add(createUpcomingCard(), gbc);

        gbc.gridx = 1; gbc.weightx = 0.38;
        grid.add(createRecentBookingsCard(), gbc);

        gbc.gridx = 2; gbc.weightx = 0.24;
        JPanel rightCol = new JPanel(new BorderLayout(0, 15));
        rightCol.setOpaque(false);

        rightCol.add(createPromoBanner(), BorderLayout.NORTH);
        rightCol.add(createQuickActionsCard(), BorderLayout.CENTER);

        grid.add(rightCol, gbc);

        return grid;
    }

    // ------------------- UPCOMING FLIGHT CARD -------------------
    private JPanel createUpcomingCard() {
        RoundedPanel card = new RoundedPanel(16, Color.WHITE, CARD_BORDER);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleBox.setOpaque(false);

        JLabel iconCircle = createIconCircle("calendar", new Color(230, 242, 255), PRIMARY_BLUE);
        JLabel title = new JLabel("Upcoming Flight");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_DARK);

        titleBox.add(iconCircle);
        titleBox.add(title);

        JButton viewAll = new JButton("View All →");
        styleLinkButton(viewAll);
        viewAll.addActionListener(e -> new MyBookings(userId, userName).setVisible(true));

        header.add(titleBox, BorderLayout.WEST);
        header.add(viewAll, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        RoundedPanel activeCard = new RoundedPanel(12, new Color(248, 250, 252), CARD_BORDER);
        activeCard.setLayout(new BorderLayout(12, 0));
        activeCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        activeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        ThumbnailGraphic thumb = new ThumbnailGraphic("wing");
        thumb.setPreferredSize(new Dimension(65, 80));

        JPanel flightInfo = new JPanel(new GridLayout(3, 1, 0, 2));
        flightInfo.setOpaque(false);

        JLabel route = new JLabel("DEL  →  BOM");
        route.setFont(new Font("Segoe UI", Font.BOLD, 16));
        route.setForeground(TEXT_DARK);

        JLabel airlines = new JLabel("IndiGo  •  6E 217");
        airlines.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        airlines.setForeground(TEXT_MUTED);

        JLabel dateTime = new JLabel("12 Apr 2025  •  10:20 AM");
        dateTime.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateTime.setForeground(TEXT_MUTED);

        flightInfo.add(route);
        flightInfo.add(airlines);
        flightInfo.add(dateTime);

        JPanel rightBadgePanel = new JPanel(new BorderLayout());
        rightBadgePanel.setOpaque(false);

        StatusBadge onTimeBadge = new StatusBadge("On Time", new Color(220, 252, 231), new Color(22, 101, 52));
        rightBadgePanel.add(onTimeBadge, BorderLayout.SOUTH);

        activeCard.add(thumb, BorderLayout.WEST);
        activeCard.add(flightInfo, BorderLayout.CENTER);
        activeCard.add(rightBadgePanel, BorderLayout.EAST);

        body.add(activeCard);
        body.add(Box.createVerticalStrut(15));

        UpcomingFlightGraphic emptyGraphic = new UpcomingFlightGraphic();
        emptyGraphic.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel mainText = new JLabel("No upcoming booking found.");
        mainText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        mainText.setForeground(TEXT_DARK);
        mainText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subText = new JLabel("<html><center>Plan your next trip and book a flight to your favourite destination.</center></html>");
        subText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subText.setForeground(TEXT_MUTED);
        subText.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundedButton bookBtn = new RoundedButton("Book a Flight →", null, PRIMARY_BLUE, PRIMARY_BLUE_HOVER, Color.WHITE, 18);
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bookBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookBtn.setPreferredSize(new Dimension(140, 36));
        bookBtn.addActionListener(e -> fromField.requestFocusInWindow());

        body.add(emptyGraphic);
        body.add(Box.createVerticalStrut(6));
        body.add(mainText);
        body.add(Box.createVerticalStrut(4));
        body.add(subText);
        body.add(Box.createVerticalStrut(12));
        body.add(bookBtn);

        card.add(body, BorderLayout.CENTER);

        return card;
    }

    // ------------------- RECENT BOOKINGS CARD -------------------
    private JPanel createRecentBookingsCard() {
        RoundedPanel card = new RoundedPanel(16, Color.WHITE, CARD_BORDER);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleBox.setOpaque(false);

        JLabel iconCircle = createIconCircle("ticket", new Color(230, 242, 255), PRIMARY_BLUE);
        JLabel title = new JLabel("Recent Bookings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_DARK);

        titleBox.add(iconCircle);
        titleBox.add(title);

        JButton viewAll = new JButton("View All →");
        styleLinkButton(viewAll);
        viewAll.addActionListener(e -> new MyBookings(userId, userName).setVisible(true));

        header.add(titleBox, BorderLayout.WEST);
        header.add(viewAll, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        listPanel.add(createBookingRow("city", "DEL  →  BOM", "12 Apr 2025  •  10:20 AM", "Confirmed", new Color(220, 252, 231), new Color(22, 101, 52)));
        listPanel.add(Box.createVerticalStrut(8));
        listPanel.add(createBookingRow("sunset", "BLR  →  DEL", "28 Mar 2025  •  05:30 PM", "Completed", new Color(224, 242, 254), new Color(3, 105, 161)));
        listPanel.add(Box.createVerticalStrut(8));
        listPanel.add(createBookingRow("beach", "MAA  →  BLR", "15 Feb 2025  •  11:45 AM", "Confirmed", new Color(220, 252, 231), new Color(22, 101, 52)));
        listPanel.add(Box.createVerticalStrut(8));
        listPanel.add(createBookingRow("pagoda", "CCU  →  DEL", "03 Feb 2025  •  09:15 AM", "Cancelled", new Color(241, 245, 249), new Color(100, 116, 139)));

        card.add(listPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createBookingRow(String thumbType, String routeText, String dateText, String status, Color bgTag, Color fgTag) {
        RoundedPanel row = new RoundedPanel(10, new Color(250, 251, 253), CARD_BORDER);
        row.setLayout(new BorderLayout(10, 0));
        row.setBorder(new EmptyBorder(8, 10, 8, 10));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        ThumbnailGraphic thumb = new ThumbnailGraphic(thumbType);
        thumb.setPreferredSize(new Dimension(45, 45));

        JPanel center = new JPanel(new GridLayout(2, 1));
        center.setOpaque(false);

        JLabel route = new JLabel(routeText);
        route.setFont(new Font("Segoe UI", Font.BOLD, 14));
        route.setForeground(TEXT_DARK);

        JLabel date = new JLabel(dateText);
        date.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        date.setForeground(TEXT_MUTED);

        center.add(route);
        center.add(date);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        right.setOpaque(false);

        StatusBadge badge = new StatusBadge(status, bgTag, fgTag);
        JLabel arrow = new JLabel(new VectorIcon("arrow_right", 12, TEXT_MUTED));

        right.add(badge);
        right.add(arrow);

        row.add(thumb, BorderLayout.WEST);
        row.add(center, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);

        return row;
    }

    // ------------------- PROMO BANNER -------------------
    private JPanel createPromoBanner() {
        JPanel promo = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(15, 65, 180),
                        getWidth(), getHeight(), new Color(60, 130, 246)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillOval(getWidth() - 110, getHeight() - 50, 140, 70);

                Icon bigPlane = new VectorIcon("plane", 36, new Color(255, 255, 255, 180));
                bigPlane.paintIcon(this, g2, getWidth() - 65, 20);

                g2.dispose();
            }
        };

        promo.setPreferredSize(new Dimension(240, 160));
        promo.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel sub = new JLabel("EXPLORE THE WORLD");
        sub.setFont(new Font("Segoe UI", Font.BOLD, 10));
        sub.setForeground(new Color(191, 219, 254));

        JLabel main = new JLabel("<html><b>Best Flights.<br>Better Prices.</b></html>");
        main.setFont(new Font("Segoe UI", Font.BOLD, 17));
        main.setForeground(Color.WHITE);

        JLabel desc = new JLabel("<html>Book now and get amazing deals on your next trip.</html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        desc.setForeground(new Color(224, 242, 254));

        RoundedButton btn = new RoundedButton("Search Flights →", null, Color.WHITE, new Color(240, 245, 255), PRIMARY_BLUE, 14);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setPreferredSize(new Dimension(130, 30));
        btn.addActionListener(e -> fromField.requestFocusInWindow());

        textPanel.add(sub);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(main);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(desc);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(btn);

        promo.add(textPanel, BorderLayout.WEST);

        return promo;
    }

    // ------------------- QUICK ACTIONS -------------------
    private JPanel createQuickActionsCard() {
        RoundedPanel card = new RoundedPanel(16, Color.WHITE, CARD_BORDER);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        header.setOpaque(false);

        JLabel flash = new JLabel(new VectorIcon("flash", 14, PRIMARY_BLUE));
        JLabel title = new JLabel("Quick Actions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(TEXT_DARK);

        header.add(flash);
        header.add(title);

        card.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 10, 10));
        grid.setOpaque(false);

        grid.add(createActionSquare("Book Flight", "plane", new Color(230, 242, 255), PRIMARY_BLUE, e -> fromField.requestFocusInWindow()));
        grid.add(createActionSquare("My Bookings", "calendar", new Color(243, 232, 255), new Color(147, 51, 234), e -> new MyBookings(userId, userName).setVisible(true)));
        grid.add(createActionSquare("My Tickets", "ticket", new Color(220, 252, 231), new Color(22, 163, 74), e -> new MyBookings(userId, userName).setVisible(true)));
        grid.add(createActionSquare("My Profile", "user", new Color(254, 243, 199), new Color(217, 119, 6), e -> new Profile(userId, userName).setVisible(true)));

        card.add(grid, BorderLayout.CENTER);

        return card;
    }

    private JPanel createActionSquare(String title, String iconType, Color bg, Color fg, java.awt.event.ActionListener action) {
        RoundedPanel box = new RoundedPanel(12, new Color(248, 250, 252), CARD_BORDER);
        box.setLayout(new GridBagLayout());
        box.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel iconCircle = createIconCircle(iconType, bg, fg);
        iconCircle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel text = new JLabel(title);
        text.setFont(new Font("Segoe UI", Font.BOLD, 11));
        text.setForeground(TEXT_DARK);
        text.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(iconCircle);
        content.add(Box.createVerticalStrut(6));
        content.add(text);

        box.add(content);

        box.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { action.actionPerformed(null); }
            @Override public void mouseEntered(MouseEvent e) { box.setBackground(new Color(238, 242, 246)); }
            @Override public void mouseExited(MouseEvent e) { box.setBackground(new Color(248, 250, 252)); }
        });

        return box;
    }

    private void styleLinkButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(PRIMARY_BLUE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JLabel createIconCircle(String iconType, Color bg, Color fg) {
        JLabel circle = new JLabel(new VectorIcon(iconType, 16, fg), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        circle.setPreferredSize(new Dimension(34, 34));
        return circle;
    }

    // ------------------- FIXED AUTOCOMPLETE LOGIC -------------------
    private void setupAutoComplete(JTextField field, boolean from) {
        JPopupMenu popup = new JPopupMenu();
        popup.setFocusable(false);

        JList<String> list = new JList<>();
        list.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        list.setFocusable(false);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(28);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(250, 150));
        popup.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        popup.add(scrollPane);

        if (from) {
            fromPopup = popup;
            fromList = list;
        } else {
            toPopup = popup;
            toList = list;
        }

        // Selection flag to prevent document updates from re-opening popup
        final boolean[] isSelecting = {false};

        field.getDocument().addDocumentListener(new DocumentListener() {
            private void updateSuggestions() {
                if (isSelecting[0]) return;

                String text = field.getText();
                if (text == null || text.trim().isEmpty()) {
                    popup.setVisible(false);
                    return;
                }

                text = text.trim();
                String sql = "SELECT DISTINCT CITY, AIRPORT_CODE FROM AIRPORTS WHERE UPPER(CITY) LIKE UPPER(?) OR UPPER(AIRPORT_CODE) LIKE UPPER(?) ORDER BY CITY";
                DefaultListModel<String> model = new DefaultListModel<>();

                try (Connection con = DBConnection.getConnection();
                     PreparedStatement pst = con.prepareStatement(sql)) {

                    if (con == null) {
                        popup.setVisible(false);
                        return;
                    }

                    pst.setString(1, text + "%");
                    pst.setString(2, text + "%");

                    try (ResultSet rs = pst.executeQuery()) {
                        while (rs.next()) {
                            model.addElement(rs.getString("CITY") + " (" + rs.getString("AIRPORT_CODE") + ")");
                        }
                    }

                    list.setModel(model);

                    if (model.getSize() > 0 && field.hasFocus()) {
                        list.setSelectedIndex(0);
                        SwingUtilities.invokeLater(() -> {
                            if (field.hasFocus() && !isSelecting[0]) {
                                popup.show(field, 0, field.getHeight());
                                field.requestFocusInWindow();
                            }
                        });
                    } else {
                        popup.setVisible(false);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    popup.setVisible(false);
                }
            }

            @Override public void insertUpdate(DocumentEvent e) { updateSuggestions(); }
            @Override public void removeUpdate(DocumentEvent e) { updateSuggestions(); }
            @Override public void changedUpdate(DocumentEvent e) { updateSuggestions(); }
        });

        // Hide popup automatically when focus is lost
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                popup.setVisible(false);
            }
        });

        Runnable selectItem = () -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                String city = selected.contains(" (") ? selected.substring(0, selected.lastIndexOf(" (")) : selected;
                isSelecting[0] = true;
                field.setText(city);
                field.setCaretPosition(city.length());
                popup.setVisible(false);
                isSelecting[0] = false;
            } else {
                popup.setVisible(false);
            }
        };

        field.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("DOWN"), "nextSuggestion");
        field.getActionMap().put("nextSuggestion", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                if (!popup.isVisible() || list.getModel().getSize() == 0) return;
                int index = list.getSelectedIndex();
                if (index < list.getModel().getSize() - 1) list.setSelectedIndex(index + 1);
            }
        });

        field.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("UP"), "prevSuggestion");
        field.getActionMap().put("prevSuggestion", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                if (!popup.isVisible() || list.getModel().getSize() == 0) return;
                int index = list.getSelectedIndex();
                if (index > 0) list.setSelectedIndex(index - 1);
            }
        });

        field.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "selectSuggestion");
        field.getActionMap().put("selectSuggestion", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                selectItem.run();
            }
        });

        field.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ESCAPE"), "hidePopup");
        field.getActionMap().put("hidePopup", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                popup.setVisible(false);
            }
        });

        list.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectItem.run();
                }
            }
        });
    }

    // ------------------- CALENDAR LOGIC -------------------
    private void setupCalendar(JTextField field) {
        calendarPopup = new JPopupMenu();
        calendarPopup.setFocusable(false);

        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setPreferredSize(new Dimension(320, 330));
        calendarPanel.setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SIDEBAR_BG);

        JButton previousButton = new JButton("‹");
        JButton nextButton = new JButton("›");
        previousButton.setFont(new Font("Arial", Font.BOLD, 22));
        nextButton.setFont(new Font("Arial", Font.BOLD, 22));
        previousButton.setForeground(Color.WHITE);
        nextButton.setForeground(Color.WHITE);
        previousButton.setBackground(SIDEBAR_BG);
        nextButton.setBackground(SIDEBAR_BG);
        previousButton.setBorderPainted(false);
        nextButton.setBorderPainted(false);

        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setForeground(Color.WHITE);
        monthYearLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        header.add(previousButton, BorderLayout.WEST);
        header.add(monthYearLabel, BorderLayout.CENTER);
        header.add(nextButton, BorderLayout.EAST);

        calendarPanel.add(header, BorderLayout.NORTH);

        JPanel weekPanel = new JPanel(new GridLayout(1, 7));
        weekPanel.setBackground(Color.WHITE);
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : days) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(TEXT_MUTED);
            weekPanel.add(label);
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(weekPanel, BorderLayout.CENTER);
        calendarPanel.add(topPanel, BorderLayout.CENTER);

        calendarDaysPanel = new JPanel(new GridLayout(6, 7, 3, 3));
        calendarDaysPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        calendarDaysPanel.setBackground(Color.WHITE);

        calendarPanel.add(calendarDaysPanel, BorderLayout.SOUTH);
        calendarPopup.add(calendarPanel);

        currentMonth = YearMonth.now();

        previousButton.addActionListener(e -> {
            if (currentMonth.isAfter(YearMonth.now())) {
                currentMonth = currentMonth.minusMonths(1);
                updateCalendar();
            }
        });

        nextButton.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            updateCalendar();
        });

        field.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                currentMonth = YearMonth.now();
                updateCalendar();
                SwingUtilities.invokeLater(() -> {
                    calendarPopup.show(field, 0, field.getHeight());
                    field.requestFocusInWindow();
                });
            }
        });
    }

    private void updateCalendar() {
        if (calendarDaysPanel == null || monthYearLabel == null) return;

        calendarDaysPanel.removeAll();
        monthYearLabel.setText(currentMonth.getMonth().toString() + " " + currentMonth.getYear());

        LocalDate today = LocalDate.now();
        LocalDate firstDay = currentMonth.atDay(1);
        int startDay = convertDayOfWeek(firstDay.getDayOfWeek());
        int daysInMonth = currentMonth.lengthOfMonth();

        for (int i = 0; i < 42; i++) {
            if (i < startDay || i >= startDay + daysInMonth) {
                JLabel empty = new JLabel("");
                empty.setOpaque(true);
                empty.setBackground(Color.WHITE);
                calendarDaysPanel.add(empty);
                continue;
            }

            int day = i - startDay + 1;
            LocalDate selectedDate = currentMonth.atDay(day);
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setFocusPainted(false);
            dayButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            dayButton.setBorder(BorderFactory.createEmptyBorder());

            if (selectedDate.isBefore(today)) {
                dayButton.setEnabled(false);
                dayButton.setForeground(new Color(190, 190, 190));
            } else if (selectedDate.equals(today)) {
                dayButton.setBackground(PRIMARY_BLUE);
                dayButton.setForeground(Color.WHITE);
            } else {
                dayButton.setBackground(Color.WHITE);
                dayButton.setForeground(TEXT_DARK);
            }

            dayButton.addActionListener(e -> {
                dateField.setText(selectedDate.format(dateFormatter));
                calendarPopup.setVisible(false);
                dateField.requestFocusInWindow();
            });

            calendarDaysPanel.add(dayButton);
        }

        calendarDaysPanel.revalidate();
        calendarDaysPanel.repaint();
    }

    private int convertDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case SUNDAY: return 0;
            case MONDAY: return 1;
            case TUESDAY: return 2;
            case WEDNESDAY: return 3;
            case THURSDAY: return 4;
            case FRIDAY: return 5;
            case SATURDAY: return 6;
            default: return 0;
        }
    }

    // ------------------- VECTOR ICON CLASS -------------------
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
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int s = size;
            if ("home".equals(type)) {
                Path2D p = new Path2D.Double();
                p.moveTo(x + s * 0.5, y + s * 0.15);
                p.lineTo(x + s * 0.85, y + s * 0.45);
                p.lineTo(x + s * 0.72, y + s * 0.45);
                p.lineTo(x + s * 0.72, y + s * 0.85);
                p.lineTo(x + s * 0.28, y + s * 0.85);
                p.lineTo(x + s * 0.28, y + s * 0.45);
                p.lineTo(x + s * 0.15, y + s * 0.45);
                p.closePath();
                g2.fill(p);
            } else if ("plane".equals(type)) {
                Path2D p = new Path2D.Double();
                p.moveTo(x + s * 0.1, y + s * 0.55);
                p.lineTo(x + s * 0.9, y + s * 0.15);
                p.lineTo(x + s * 0.65, y + s * 0.9);
                p.lineTo(x + s * 0.5, y + s * 0.6);
                p.closePath();
                g2.fill(p);
            } else if ("calendar".equals(type)) {
                g2.drawRoundRect(x + 2, y + 4, s - 4, s - 6, 3, 3);
                g2.drawLine(x + 2, y + 8, x + s - 2, y + 8);
                g2.drawLine(x + 5, y + 2, x + 5, y + 5);
                g2.drawLine(x + s - 5, y + 2, x + s - 5, y + 5);
            } else if ("ticket".equals(type)) {
                g2.drawRoundRect(x + 2, y + 3, s - 4, s - 6, 4, 4);
                g2.drawLine(x + (int)(s * 0.38), y + 3, x + (int)(s * 0.38), y + s - 3);
            } else if ("user".equals(type)) {
                g2.drawOval(x + s / 2 - 3, y + 2, 6, 6);
                g2.drawArc(x + 2, y + 9, s - 4, 8, 0, 180);
            } else if ("help".equals(type)) {
                g2.drawOval(x + 2, y + 2, s - 4, s - 4);
                g2.setFont(new Font("Arial", Font.BOLD, s - 6));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("?", x + (s - fm.stringWidth("?")) / 2, y + (s + fm.getAscent() - fm.getDescent()) / 2 - 1);
            } else if ("logout".equals(type)) {
                g2.drawArc(x + 1, y + 2, s - 6, s - 4, 90, 270);
                g2.drawLine(x + s / 2 - 2, y + s / 2, x + s - 2, y + s / 2);
            } else if ("pin".equals(type)) {
                g2.drawOval(x + s / 2 - 3, y + 2, 6, 6);
                g2.drawLine(x + s / 2, y + 8, x + s / 2, y + s - 2);
            } else if ("search".equals(type)) {
                g2.drawOval(x + 2, y + 2, s - 7, s - 7);
                g2.drawLine(x + s - 6, y + s - 6, x + s - 1, y + s - 1);
            } else if ("bell".equals(type)) {
                g2.drawArc(x + 3, y + 4, s - 6, s - 6, 0, 180);
                g2.drawLine(x + 2, y + s - 5, x + s - 2, y + s - 5);
                g2.drawArc(x + s / 2 - 2, y + s - 4, 4, 3, 180, 180);
                g2.setColor(new Color(220, 38, 38));
                g2.fillOval(x + s - 6, y + 2, 5, 5);
            } else if ("arrow_down".equals(type)) {
                int[] px = {x + 1, x + s / 2, x + s - 1};
                int[] py = {y + 3, y + s - 3, y + 3};
                g2.fillPolygon(px, py, 3);
            } else if ("arrow_right".equals(type)) {
                g2.drawLine(x + 2, y + s / 2, x + s - 2, y + s / 2);
                g2.drawLine(x + s - 5, y + 2, x + s - 2, y + s / 2);
                g2.drawLine(x + s - 5, y + s - 2, x + s - 2, y + s / 2);
            } else if ("flash".equals(type)) {
                Path2D p = new Path2D.Double();
                p.moveTo(x + s * 0.6, y + 1);
                p.lineTo(x + 2, y + s * 0.55);
                p.lineTo(x + s * 0.5, y + s * 0.55);
                p.lineTo(x + s * 0.4, y + s - 1);
                p.lineTo(x + s - 2, y + s * 0.45);
                p.lineTo(x + s * 0.5, y + s * 0.45);
                p.closePath();
                g2.fill(p);
            }

            g2.dispose();
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    // ------------------- UI COMPONENTS -------------------
    static class StatusBadge extends JPanel {
        public StatusBadge(String text, Color bg, Color fg) {
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 3));

            JLabel label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setForeground(fg);

            add(label);

            setBackground(bg);
            setBorder(BorderFactory.createEmptyBorder());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class ThumbnailGraphic extends JPanel {
        private final String style;

        public ThumbnailGraphic(String style) {
            this.style = style;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));

            if ("wing".equals(style)) {
                GradientPaint gp = new GradientPaint(0, 0, new Color(56, 189, 248), 0, getHeight(), new Color(30, 58, 138));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(Color.WHITE);
                Path2D wing = new Path2D.Double();
                wing.moveTo(getWidth(), getHeight() * 0.2);
                wing.lineTo(0, getHeight());
                wing.lineTo(getWidth(), getHeight());
                wing.closePath();
                g2.fill(wing);

            } else if ("city".equals(style)) {
                GradientPaint gp = new GradientPaint(0, 0, new Color(251, 146, 60), 0, getHeight(), new Color(147, 51, 234));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(15, 23, 42));
                g2.fillRect(5, getHeight() - 25, 10, 25);
                g2.fillRect(18, getHeight() - 32, 12, 32);
                g2.fillRect(32, getHeight() - 20, 8, 20);

            } else if ("sunset".equals(style)) {
                GradientPaint gp = new GradientPaint(0, 0, new Color(244, 63, 94), 0, getHeight(), new Color(251, 191, 36));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(Color.WHITE);
                g2.fillOval(getWidth() / 2 - 8, getHeight() - 15, 16, 16);

            } else if ("beach".equals(style)) {
                GradientPaint gp = new GradientPaint(0, 0, new Color(45, 212, 191), 0, getHeight(), new Color(13, 148, 136));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

            } else {
                GradientPaint gp = new GradientPaint(0, 0, new Color(251, 191, 36), 0, getHeight(), new Color(180, 83, 9));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            g2.dispose();
        }
    }

    static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color backgroundColor;
        private final Color borderColor;

        public RoundedPanel(int radius, Color bg, Color border) {
            this.cornerRadius = radius;
            this.backgroundColor = bg;
            this.borderColor = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (backgroundColor != null) {
                g2.setColor(backgroundColor);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            }

            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            }

            g2.dispose();
        }
    }

    static class RoundedButton extends JButton {
        private final Color normalBg;
        private final Color hoverBg;
        private final int radius;

        public RoundedButton(String text, String iconType, Color bg, Color hoverBg, Color fg, int radius) {
            super(text, iconType != null ? new VectorIcon(iconType, 16, fg) : null);
            this.normalBg = bg;
            this.hoverBg = hoverBg;
            this.radius = radius;
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setForeground(fg);
            setIconTextGap(8);
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

    static class IconTextField extends JTextField {
        private final String placeholder;
        private final String iconType;

        public IconTextField(String placeholder, String iconType) {
            this.placeholder = placeholder;
            this.iconType = iconType;
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setPreferredSize(new Dimension(200, 40));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                    BorderFactory.createEmptyBorder(0, 30, 0, 10)
            ));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (iconType != null) {
                Icon icon = new VectorIcon(iconType, 15, new Color(148, 163, 184));
                icon.paintIcon(this, g2, 8, (getHeight() - 15) / 2);
            }

            if (getText().isEmpty() && placeholder != null) {
                g2.setFont(getFont());
                g2.setColor(new Color(148, 163, 184));
                g2.drawString(placeholder, getInsets().left, getHeight() / 2 + 5);
            }

            g2.dispose();
        }
    }

    static class UpcomingFlightGraphic extends JPanel {
        public UpcomingFlightGraphic() {
            setPreferredSize(new Dimension(140, 70));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            g2.setColor(new Color(235, 243, 255));
            g2.fillOval(cx - 30, cy - 30, 60, 60);

            Icon plane = new VectorIcon("plane", 28, new Color(0, 102, 255));
            plane.paintIcon(this, g2, cx - 14, cy - 14);

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserDashboard(1, "Satyam").setVisible(true));
    }
}