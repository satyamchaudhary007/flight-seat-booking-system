package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Profile extends JFrame {

    private final int userId;
    private String userName;
    private String userEmail = "";

    // Form Input Fields
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField dobField;
    private JComboBox<String> genderCombo;
    private JComboBox<String> nationalityCombo;
    private JTextArea addressArea;

    // Dynamic Header Label
    private JLabel headerUserLabel;

    // DB Memory State for Reset Button
    private String origName = "";
    private String origEmail = "";
    private String origPhone = "";
    private String origDob = "";
    private String origGender = "Male";
    private String origNationality = "Indian";
    private String origAddress = "";

    // Color Palette
    private final Color SIDEBAR_BG = new Color(11, 27, 51);      // Dark Navy
    private final Color PRIMARY_BLUE = new Color(0, 102, 255);    // SkySeat Blue
    private final Color MAIN_BG = new Color(240, 244, 248);      // Soft Background
    private final Color CARD_BG = Color.WHITE;
    private final Color CARD_HEADER_BG = new Color(236, 243, 254); // Light Blue Header Band
    private final Color TEXT_DARK = new Color(15, 23, 42);
    private final Color TEXT_MUTED = new Color(100, 116, 139);
    private final Color BORDER_COLOR = new Color(210, 218, 228);
    private final Color ICON_BOX_BG = new Color(245, 247, 250);

    public Profile() {
        this(1, "Rahul Sharma");
    }

    public Profile(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;

        setTitle("SkySeat - My Profile");
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLayout(new BorderLayout());

        createHeader();
        createSidebar();
        createMainContent();

        loadUserDataFromDB();
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

    // ------------------- TOP HEADER BAR -------------------
    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(1280, 60));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        logoPanel.setOpaque(false);

        JPanel textStack = new JPanel(new GridLayout(2, 1));
        textStack.setOpaque(false);

        JLabel logoText = new JLabel("SKYSEAT");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoText.setForeground(PRIMARY_BLUE);

        JLabel logoSub = new JLabel("Flight Booking System");
        logoSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        logoSub.setForeground(TEXT_MUTED);

        textStack.add(logoText);
        textStack.add(logoSub);

        logoPanel.add(textStack);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 18));
        rightHeader.setOpaque(false);

        headerUserLabel = new JLabel("Welcome, " + userName);
        headerUserLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        headerUserLabel.setForeground(TEXT_DARK);

        String currentDate = new SimpleDateFormat("dd MMM yyyy").format(new Date());
        JLabel dateLabel = new JLabel("  |   " + currentDate);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(TEXT_MUTED);

        rightHeader.add(headerUserLabel);
        rightHeader.add(dateLabel);

        header.add(logoPanel, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    // ------------------- LEFT SIDEBAR -------------------
    private void createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(220, 700));

        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(25, 12, 20, 12));

        menuPanel.add(createMenuButton("Dashboard", "home", false));
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(createMenuButton("Search Flights", "search", false));
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(createMenuButton("My Bookings", "calendar", false));
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(createMenuButton("My Profile", "user", true));
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(createMenuButton("Settings", "gear", false));
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(createMenuButton("Logout", "logout", false));

        sidebar.add(menuPanel, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
    }

    private JButton createMenuButton(String text, String iconType, boolean active) {
        Color iconColor = active ? Color.WHITE : new Color(160, 175, 200);
        JButton button = new JButton(text, new VectorIcon(iconType, 16, iconColor));
        button.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(196, 42));
        button.setMaximumSize(new Dimension(196, 42));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(14);
        button.setBorder(new EmptyBorder(0, 16, 0, 0));
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
                    button.setIcon(new VectorIcon(iconType, 16, Color.WHITE));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(SIDEBAR_BG);
                    button.setForeground(new Color(160, 175, 200));
                    button.setIcon(new VectorIcon(iconType, 16, new Color(160, 175, 200)));
                }
            });
        }

        button.addActionListener(e -> {
            if ("Dashboard".equals(text) || "Search Flights".equals(text)) {
                new UserDashboard(userId, userName).setVisible(true);
                dispose();
            } else if ("My Bookings".equals(text)) {
                new MyBookings(userId, userName).setVisible(true);
                dispose();
            } else if ("Logout".equals(text)) {
                int res = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    new Login().setVisible(true);
                    dispose();
                }
            }
        });

        return button;
    }

    // ------------------- MAIN CONTENT -------------------
    private void createMainContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(MAIN_BG);
        main.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Top Row Title & Breadcrumb
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("My Profile");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        pageTitle.setForeground(TEXT_DARK);

        JLabel pageSub = new JLabel("View and update your personal information");
        pageSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pageSub.setForeground(TEXT_MUTED);

        titleBox.add(pageTitle);
        titleBox.add(pageSub);

        JPanel breadcrumb = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        breadcrumb.setOpaque(false);

        JLabel homeIcon = new JLabel(new VectorIcon("home", 13, TEXT_MUTED));
        JLabel homeText = new JLabel("Home  >");
        homeText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        homeText.setForeground(TEXT_MUTED);

        JLabel profileText = new JLabel("My Profile");
        profileText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        profileText.setForeground(PRIMARY_BLUE);

        breadcrumb.add(homeIcon);
        breadcrumb.add(homeText);
        breadcrumb.add(profileText);

        topRow.add(titleBox, BorderLayout.WEST);
        topRow.add(breadcrumb, BorderLayout.EAST);

        // Card Container
        JPanel cardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        cardWrapper.setOpaque(false);

        RoundedPanel card = new RoundedPanel(16, CARD_BG, BORDER_COLOR);
        card.setLayout(new BorderLayout(0, 18));
        card.setPreferredSize(new Dimension(920, 520));

        // Header Band Inside Card
        RoundedPanel cardTitleBand = new RoundedPanel(12, CARD_HEADER_BG, null);
        cardTitleBand.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));

        JLabel iconLabel = new JLabel(new VectorIcon("user", 18, PRIMARY_BLUE));
        JLabel cardTitle = new JLabel("Personal Information");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cardTitle.setForeground(TEXT_DARK);

        cardTitleBand.add(iconLabel);
        cardTitleBand.add(cardTitle);

        JPanel cardHeaderContainer = new JPanel(new BorderLayout());
        cardHeaderContainer.setOpaque(false);
        cardHeaderContainer.setBorder(new EmptyBorder(12, 12, 0, 12));
        cardHeaderContainer.add(cardTitleBand, BorderLayout.CENTER);

        card.add(cardHeaderContainer, BorderLayout.NORTH);

        // 2-COLUMN FORM GRID
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        formGrid.setBorder(new EmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 12, 8, 12);

        nameField = new JTextField();
        emailField = new JTextField();
        emailField.setEditable(false);

        phoneField = new JTextField();
        dobField = new JTextField();

        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        nationalityCombo = new JComboBox<>(new String[]{"Indian", "American", "British", "Canadian", "Other"});
        nationalityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        addressArea = new JTextArea(3, 20);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);

        // ROUNDED CURLY INPUT BOX WRAPPERS
        JPanel nameComp = createFieldWithIcon("user", nameField, null);
        JPanel emailComp = createFieldWithIcon("mail", emailField, null);
        JPanel phoneComp = createFieldWithIcon("phone", phoneField, null);
        JPanel dobComp = createFieldWithIcon("calendar", dobField, new VectorIcon("calendar", 14, TEXT_MUTED));
        JPanel genderComp = createFieldWithIcon("users", genderCombo, null);
        JPanel nationalityComp = createFieldWithIcon("globe", nationalityCombo, null);
        JPanel addressComp = createFieldWithIcon("pin", addressArea, null);

        // LEFT COLUMN
        addGridCell(formGrid, gbc, 0, 0, "Full Name", nameComp);
        addGridCell(formGrid, gbc, 1, 0, "Email Address", emailComp);
        addGridCell(formGrid, gbc, 2, 0, "Phone Number", phoneComp);
        addGridCell(formGrid, gbc, 3, 0, "Date of Birth", dobComp);

        // RIGHT COLUMN
        addGridCell(formGrid, gbc, 0, 2, "Gender", genderComp);
        addGridCell(formGrid, gbc, 1, 2, "Nationality", nationalityComp);

        // Address Multi-Row Cell
        gbc.gridy = 2;
        gbc.gridx = 2;
        gbc.weightx = 0.15;
        JLabel addrLabel = new JLabel("Address");
        addrLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addrLabel.setForeground(TEXT_DARK);
        formGrid.add(addrLabel, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.35;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        formGrid.add(addressComp, gbc);

        card.add(formGrid, BorderLayout.CENTER);

        // Action Buttons Row
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonRow.setOpaque(false);
        buttonRow.setBorder(new EmptyBorder(0, 0, 15, 0));

        JButton saveBtn = new JButton("Save Changes", new VectorIcon("save", 14, Color.WHITE));
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setBackground(PRIMARY_BLUE);
        saveBtn.setPreferredSize(new Dimension(170, 40));
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> saveUserDataToDB());

        JButton resetBtn = new JButton("Reset", new VectorIcon("reset", 14, PRIMARY_BLUE));
        resetBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        resetBtn.setForeground(PRIMARY_BLUE);
        resetBtn.setBackground(Color.WHITE);
        resetBtn.setPreferredSize(new Dimension(130, 40));
        resetBtn.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 1));
        resetBtn.setFocusPainted(false);
        resetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetBtn.addActionListener(e -> resetFormFields());

        buttonRow.add(saveBtn);
        buttonRow.add(resetBtn);

        card.add(buttonRow, BorderLayout.SOUTH);
        cardWrapper.add(card);

        JPanel contentStack = new JPanel(new BorderLayout(0, 15));
        contentStack.setOpaque(false);
        contentStack.add(topRow, BorderLayout.NORTH);
        contentStack.add(cardWrapper, BorderLayout.CENTER);

        JScrollPane mainScroll = new JScrollPane(contentStack);
        mainScroll.setBorder(null);
        mainScroll.getViewport().setOpaque(false);
        mainScroll.setOpaque(false);

        main.add(mainScroll, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
    }

    private JPanel createFieldWithIcon(String iconType, JComponent inputComp, Icon rightIcon) {
        RoundedPanel wrapper = new RoundedPanel(12, Color.WHITE, BORDER_COLOR);
        wrapper.setLayout(new BorderLayout());

        JPanel iconBox = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ICON_BOX_BG);

                Path2D path = new Path2D.Double();
                path.moveTo(12, 0);
                path.lineTo(getWidth(), 0);
                path.lineTo(getWidth(), getHeight());
                path.lineTo(12, getHeight());
                path.quadTo(0, getHeight(), 0, getHeight() - 12);
                path.lineTo(0, 12);
                path.quadTo(0, 0, 12, 0);
                path.closePath();
                g2.fill(path);

                g2.setColor(BORDER_COLOR);
                g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
                g2.dispose();
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(42, 38));

        JLabel iconLabel = new JLabel(new VectorIcon(iconType, 15, TEXT_MUTED));
        iconBox.add(iconLabel);

        if (inputComp instanceof JTextField) {
            JTextField tf = (JTextField) inputComp;
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            tf.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            tf.setOpaque(false);
            tf.setPreferredSize(new Dimension(180, 38));
        } else if (inputComp instanceof JComboBox) {
            JComboBox<?> cb = (JComboBox<?>) inputComp;
            cb.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            cb.setOpaque(false);
            cb.setPreferredSize(new Dimension(180, 38));
        } else if (inputComp instanceof JTextArea) {
            JTextArea ta = (JTextArea) inputComp;
            ta.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            ta.setOpaque(false);
            iconBox.setPreferredSize(new Dimension(42, 80));
        }

        wrapper.add(iconBox, BorderLayout.WEST);
        wrapper.add(inputComp, BorderLayout.CENTER);

        if (rightIcon != null) {
            JLabel rightLabel = new JLabel(rightIcon);
            rightLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 12));
            wrapper.add(rightLabel, BorderLayout.EAST);
        }

        return wrapper;
    }

    private void addGridCell(JPanel grid, GridBagConstraints gbc, int row, int col, String labelText, JComponent comp) {
        gbc.gridy = row;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = col;
        gbc.weightx = 0.15;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_DARK);
        grid.add(label, gbc);

        gbc.gridx = col + 1;
        gbc.weightx = 0.35;
        grid.add(comp, gbc);
    }

    // ------------------- FIXED DATABASE LOGIC WITH FULL DEBUGGING -------------------
    private void loadUserDataFromDB() {
        // Checking Database Connection
        Connection con = DBConnection.getConnection();
        if (con == null) {
            System.err.println("[DB ERROR] DBConnection.getConnection() returned NULL! Check DB Server or Credentials.");
            setFallbackData();
            return;
        }

        // Querying User Data
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, userId);
            System.out.println("[DB DEBUG] Fetching data for USER_ID = " + userId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // Safe Column Extraction (Handles Case In-sensitivity & Alternate Column Names)
                    origName = getColumnSafe(rs, "name", "full_name", userName);
                    origEmail = getColumnSafe(rs, "email", "email_id", "rahul.sharma@example.com");
                    origPhone = getColumnSafe(rs, "phone", "phone_number", "+91 98765 43210");
                    origGender = getColumnSafe(rs, "gender", "sex", "Male");
                    origNationality = getColumnSafe(rs, "nationality", "country", "Indian");
                    origAddress = getColumnSafe(rs, "address", "location", "123, MG Road\nConnaught Place\nNew Delhi - 110001");
                    origDob = getColumnSafe(rs, "dob", "date_of_birth", "15-03-2000");

                    System.out.println("[DB SUCCESS] Data loaded successfully for " + origName);
                    updateFieldsWithData();
                } else {
                    System.err.println("[DB WARNING] No record found in 'users' table for user_id = " + userId);
                    setFallbackData();
                }
            }
        } catch (Exception ex) {
            System.err.println("[DB EXCEPTION] Error executing SQL query:");
            ex.printStackTrace(); // Output stacktrace in console so you can see exact error
            setFallbackData();
        } finally {
            try { con.close(); } catch (Exception ignored) {}
        }
    }

    // Helper Method to Safely Get Column Value Even If Column Names Differ
    private String getColumnSafe(ResultSet rs, String col1, String col2, String defaultVal) {
        try {
            String val = rs.getString(col1);
            if (val != null && !val.trim().isEmpty()) return val;
        } catch (Exception ignored) {}

        try {
            String val = rs.getString(col2);
            if (val != null && !val.trim().isEmpty()) return val;
        } catch (Exception ignored) {}

        return defaultVal;
    }

    private void saveUserDataToDB() {
        String newName = nameField.getText().trim();
        String newPhone = phoneField.getText().trim();
        String newDob = dobField.getText().trim();
        String newGender = (String) genderCombo.getSelectedItem();
        String newNationality = (String) nationalityCombo.getSelectedItem();
        String newAddress = addressArea.getText().trim();

        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection con = DBConnection.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "UPDATE users SET name = ?, phone = ?, gender = ?, nationality = ?, address = ?, dob = ? WHERE user_id = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, newName);
            pst.setString(2, newPhone);
            pst.setString(3, newGender);
            pst.setString(4, newNationality);
            pst.setString(5, newAddress);
            pst.setString(6, newDob);
            pst.setInt(7, userId);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                userName = newName;
                origName = newName;
                origPhone = newPhone;
                origDob = newDob;
                origGender = newGender;
                origNationality = newNationality;
                origAddress = newAddress;

                if (headerUserLabel != null) headerUserLabel.setText("Welcome, " + userName);
                JOptionPane.showMessageDialog(this, "Profile updated successfully in Database!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No record updated. Check user_id in database.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            System.err.println("[DB UPDATE ERROR] Could not save changes:");
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Update Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { con.close(); } catch (Exception ignored) {}
        }
    }

    private void resetFormFields() {
        updateFieldsWithData();
    }

    private void updateFieldsWithData() {
        userName = origName;
        userEmail = origEmail;

        if (headerUserLabel != null) headerUserLabel.setText("Welcome, " + userName);

        nameField.setText(origName);
        emailField.setText(origEmail);
        phoneField.setText(origPhone);
        dobField.setText(origDob);
        genderCombo.setSelectedItem(origGender);
        nationalityCombo.setSelectedItem(origNationality);
        addressArea.setText(origAddress);
    }

    private void setFallbackData() {
        origName = userName;
        origEmail = "rahul.sharma@example.com";
        origPhone = "+91 98765 43210";
        origDob = "15-03-2000";
        origGender = "Male";
        origNationality = "Indian";
        origAddress = "123, MG Road\nConnaught Place\nNew Delhi - 110001";

        updateFieldsWithData();
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
            } else if ("search".equals(type)) {
                g2.drawOval(x + 1, y + 1, s - 6, s - 6);
                g2.drawLine(x + s - 5, y + s - 5, x + s - 1, y + s - 1);
            } else if ("calendar".equals(type)) {
                g2.drawRoundRect(x + 1, y + 3, s - 2, s - 4, 3, 3);
                g2.drawLine(x + 1, y + 7, x + s - 1, y + 7);
                g2.drawLine(x + 4, y + 1, x + 4, y + 4);
                g2.drawLine(x + s - 4, y + 1, x + s - 4, y + 4);
            } else if ("user".equals(type)) {
                g2.drawOval(x + s / 2 - 3, y + 1, 6, 6);
                g2.drawArc(x + 1, y + 8, s - 2, 7, 0, 180);
            } else if ("mail".equals(type)) {
                g2.drawRoundRect(x + 1, y + 3, s - 2, s - 6, 3, 3);
                g2.drawLine(x + 1, y + 4, x + s / 2, y + s / 2 + 1);
                g2.drawLine(x + s / 2, y + s / 2 + 1, x + s - 1, y + 4);
            } else if ("phone".equals(type)) {
                g2.drawRoundRect(x + 3, y + 1, s - 6, s - 2, 3, 3);
                g2.drawLine(x + 6, y + s - 4, x + s - 6, y + s - 4);
            } else if ("pin".equals(type)) {
                g2.drawOval(x + s / 2 - 3, y + 1, 6, 6);
                g2.drawLine(x + s / 2, y + 7, x + s / 2, y + s - 1);
            } else if ("users".equals(type)) {
                g2.drawOval(x + 2, y + 2, 5, 5);
                g2.drawArc(x + 1, y + 8, 8, 5, 0, 180);
                g2.drawOval(x + 8, y + 2, 5, 5);
                g2.drawArc(x + 7, y + 8, 8, 5, 0, 180);
            } else if ("globe".equals(type)) {
                g2.drawOval(x + 1, y + 1, s - 2, s - 2);
                g2.drawLine(x + 1, y + s / 2, x + s - 1, y + s / 2);
                g2.drawArc(x + 3, y + 1, s - 6, s - 2, 0, 360);
            } else if ("save".equals(type)) {
                g2.drawRoundRect(x + 1, y + 1, s - 2, s - 2, 3, 3);
                g2.drawRect(x + 3, y + 1, s - 6, 4);
            } else if ("reset".equals(type)) {
                g2.drawArc(x + 1, y + 1, s - 2, s - 2, 45, 270);
                g2.drawLine(x + s - 3, y + 3, x + s - 3, y + 7);
                g2.drawLine(x + s - 3, y + 3, x + s - 7, y + 3);
            } else if ("gear".equals(type)) {
                g2.drawOval(x + 2, y + 2, s - 4, s - 4);
                g2.drawOval(x + s / 2 - 2, y + s / 2 - 2, 4, 4);
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
        SwingUtilities.invokeLater(() -> new Profile(1, "Rahul Sharma").setVisible(true));
    }
}