package gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDashboard extends JFrame {

    private final int userId;
    private final String userName;

    private JTextField fromField;
    private JTextField toField;
    private JTextField dateField;

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

    public UserDashboard(int userId, String userName) {

        this.userId = userId;
        this.userName = userName;

        setTitle("SkySeat - User Dashboard");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createHeader();
        createSidebar();
        createDashboard();
    }

    private void createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 55, 100));
        header.setPreferredSize(new Dimension(1200, 70));

        JLabel logo = new JLabel("  SKYSEAT");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel welcome =
                new JLabel("Welcome, " + userName + "    ");

        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Arial", Font.BOLD, 15));

        header.add(logo, BorderLayout.WEST);
        header.add(welcome, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void createSidebar() {

        JPanel sidebar = new JPanel();

        sidebar.setBackground(new Color(30, 40, 55));
        sidebar.setPreferredSize(new Dimension(200, 680));
        sidebar.setLayout(new GridLayout(7, 1, 0, 8));

        JButton dashboardButton =
                createMenuButton("Dashboard");

        JButton bookFlightButton =
                createMenuButton("Book Flight");

        JButton bookingsButton =
                createMenuButton("My Bookings");

        JButton ticketsButton =
                createMenuButton("My Tickets");

        JButton profileButton =
                createMenuButton("My Profile");

        JButton helpButton =
                createMenuButton("Help");

        JButton logoutButton =
                createMenuButton("Logout");

        sidebar.add(dashboardButton);
        sidebar.add(bookFlightButton);
        sidebar.add(bookingsButton);
        sidebar.add(ticketsButton);
        sidebar.add(profileButton);
        sidebar.add(helpButton);
        sidebar.add(logoutButton);

        logoutButton.addActionListener(e -> {

            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                new Login().setVisible(true);
                dispose();
            }
        });

        add(sidebar, BorderLayout.WEST);
    }

    private JButton createMenuButton(String text) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("Arial", Font.BOLD, 15)
        );

        button.setForeground(Color.WHITE);

        button.setBackground(
                new Color(30, 40, 55)
        );

        button.setFocusPainted(false);

        return button;
    }

    private void createDashboard() {

        JPanel main =
                new JPanel(new BorderLayout(15, 15));

        main.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        JLabel heading =
                new JLabel(
                        "Welcome back, " + userName
                );

        heading.setFont(
                new Font("Arial", Font.BOLD, 26)
        );

        main.add(
                heading,
                BorderLayout.NORTH
        );

        JPanel center =
                new JPanel(new BorderLayout(15, 15));

        JPanel searchCard =
                createSearchCard();

        JPanel upcomingCard =
                createUpcomingCard();

        JPanel recentCard =
                createRecentBookingsCard();

        center.add(
                searchCard,
                BorderLayout.NORTH
        );

        center.add(
                upcomingCard,
                BorderLayout.CENTER
        );

        center.add(
                recentCard,
                BorderLayout.SOUTH
        );

        main.add(
                center,
                BorderLayout.CENTER
        );

        add(main, BorderLayout.CENTER);
    }

    private JPanel createSearchCard() {

        JPanel card =
                new JPanel(new GridBagLayout());

        card.setBorder(
                BorderFactory.createTitledBorder(
                        "Search Your Flight"
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(8, 8, 8, 8);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        JLabel fromLabel =
                new JLabel("From");

        JLabel toLabel =
                new JLabel("To");

        JLabel dateLabel =
                new JLabel("Date");

        JLabel passengerLabel =
                new JLabel("Passengers");

        fromField =
                new JTextField(12);

        toField =
                new JTextField(12);

        dateField =
                new JTextField(10);

        dateField.setEditable(false);
        dateField.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        JComboBox<Integer> passengers =
                new JComboBox<>(
                        new Integer[]{
                                1, 2, 3, 4, 5, 6
                        }
                );

        JButton searchButton =
                new JButton("SEARCH FLIGHTS");

        searchButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        gbc.gridx = 0;
        gbc.gridy = 0;
        card.add(fromLabel, gbc);

        gbc.gridx = 1;
        card.add(fromField, gbc);

        gbc.gridx = 2;
        card.add(toLabel, gbc);

        gbc.gridx = 3;
        card.add(toField, gbc);

        gbc.gridx = 4;
        card.add(dateLabel, gbc);

        gbc.gridx = 5;
        card.add(dateField, gbc);

        gbc.gridx = 6;
        card.add(passengerLabel, gbc);

        gbc.gridx = 7;
        card.add(passengers, gbc);

        gbc.gridx = 8;
        card.add(searchButton, gbc);

        setupAutoComplete(fromField, true);
        setupAutoComplete(toField, false);

        setupCalendar(dateField);

        searchButton.addActionListener(e -> {

            String from =
                    fromField.getText().trim();

            String to =
                    toField.getText().trim();

            String date =
                    dateField.getText().trim();

            int passengerCount =
                    (Integer) passengers.getSelectedItem();

            if (from.isEmpty()
                    || to.isEmpty()
                    || date.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter From, To and Date.",
                        "Flight Search",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            try {

                FlightResults flightResults =
                        new FlightResults(
                                userId,
                                userName,
                                from,
                                to,
                                date,
                                passengerCount
                        );

                flightResults.setVisible(true);

                dispose();

            } catch (Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to open Flight Results.\n"
                                + ex.getMessage(),
                        "Flight Search Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        return card;
    }

    private void setupAutoComplete(
            JTextField field,
            boolean from
    ) {

        JPopupMenu popup =
                new JPopupMenu();

        popup.setFocusable(false);

        JList<String> list =
                new JList<>();

        list.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        list.setFocusable(false);

        list.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        list.setFixedCellHeight(28);

        JScrollPane scrollPane =
                new JScrollPane(list);

        scrollPane.setPreferredSize(
                new Dimension(250, 150)
        );

        popup.setBorder(
                BorderFactory.createLineBorder(
                        new Color(150, 150, 150)
                )
        );

        popup.add(scrollPane);

        if (from) {
            fromPopup = popup;
            fromList = list;
        } else {
            toPopup = popup;
            toList = list;
        }

        field.getDocument().addDocumentListener(
                new DocumentListener() {

                    private void updateSuggestions() {

                        String text =
                                field.getText();

                        if (text == null
                                || text.trim().isEmpty()) {

                            popup.setVisible(false);
                            return;
                        }

                        text = text.trim();

                        String sql =
                                "SELECT DISTINCT CITY, AIRPORT_CODE " +
                                        "FROM AIRPORTS " +
                                        "WHERE UPPER(CITY) LIKE UPPER(?) " +
                                        "OR UPPER(AIRPORT_CODE) LIKE UPPER(?) " +
                                        "ORDER BY CITY";

                        DefaultListModel<String> model =
                                new DefaultListModel<>();

                        Connection con = null;
                        PreparedStatement pst = null;
                        ResultSet rs = null;

                        try {

                            con =
                                    DBConnection.getConnection();

                            if (con == null) {
                                popup.setVisible(false);
                                return;
                            }

                            pst =
                                    con.prepareStatement(sql);

                            pst.setString(
                                    1,
                                    text + "%"
                            );

                            pst.setString(
                                    2,
                                    text + "%"
                            );

                            rs =
                                    pst.executeQuery();

                            while (rs.next()) {

                                String city =
                                        rs.getString("CITY");

                                String code =
                                        rs.getString(
                                                "AIRPORT_CODE"
                                        );

                                model.addElement(
                                        city + " (" + code + ")"
                                );
                            }

                            list.setModel(model);

                            if (model.getSize() > 0
                                    && field.hasFocus()) {

                                list.setSelectedIndex(0);

                                SwingUtilities.invokeLater(() -> {

                                    if (field.hasFocus()) {

                                        popup.show(
                                                field,
                                                0,
                                                field.getHeight()
                                        );

                                        field.requestFocusInWindow();
                                    }
                                });

                            } else {

                                popup.setVisible(false);
                            }

                        } catch (Exception ex) {

                            ex.printStackTrace();
                            popup.setVisible(false);

                        } finally {

                            try {
                                if (rs != null) {
                                    rs.close();
                                }
                            } catch (Exception ignored) {
                            }

                            try {
                                if (pst != null) {
                                    pst.close();
                                }
                            } catch (Exception ignored) {
                            }

                            try {
                                if (con != null) {
                                    con.close();
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }

                    @Override
                    public void insertUpdate(
                            DocumentEvent e
                    ) {
                        updateSuggestions();
                    }

                    @Override
                    public void removeUpdate(
                            DocumentEvent e
                    ) {
                        updateSuggestions();
                    }

                    @Override
                    public void changedUpdate(
                            DocumentEvent e
                    ) {
                        updateSuggestions();
                    }
                }
        );

        field.getInputMap(
                JComponent.WHEN_FOCUSED
        ).put(
                KeyStroke.getKeyStroke("DOWN"),
                "nextSuggestion"
        );

        field.getActionMap().put(
                "nextSuggestion",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(
                            java.awt.event.ActionEvent e
                    ) {

                        if (!popup.isVisible()
                                || list.getModel().getSize() == 0) {
                            return;
                        }

                        int index =
                                list.getSelectedIndex();

                        if (index
                                < list.getModel().getSize() - 1) {

                            list.setSelectedIndex(
                                    index + 1
                            );
                        }
                    }
                }
        );

        field.getInputMap(
                JComponent.WHEN_FOCUSED
        ).put(
                KeyStroke.getKeyStroke("UP"),
                "previousSuggestion"
        );

        field.getActionMap().put(
                "previousSuggestion",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(
                            java.awt.event.ActionEvent e
                    ) {

                        if (!popup.isVisible()
                                || list.getModel().getSize() == 0) {
                            return;
                        }

                        int index =
                                list.getSelectedIndex();

                        if (index > 0) {

                            list.setSelectedIndex(
                                    index - 1
                            );
                        }
                    }
                }
        );

        field.getInputMap(
                JComponent.WHEN_FOCUSED
        ).put(
                KeyStroke.getKeyStroke("ENTER"),
                "selectSuggestion"
        );

        field.getActionMap().put(
                "selectSuggestion",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(
                            java.awt.event.ActionEvent e
                    ) {

                        if (!popup.isVisible()
                                || list.getModel().getSize() == 0) {
                            return;
                        }

                        String selected =
                                list.getSelectedValue();

                        if (selected != null) {

                            String city =
                                    selected.substring(
                                            0,
                                            selected.lastIndexOf(" (")
                                    );

                            popup.setVisible(false);

                            field.setText(city);

                            field.setCaretPosition(
                                    city.length()
                            );

                            SwingUtilities.invokeLater(() ->
                                    field.requestFocusInWindow()
                            );
                        }
                    }
                }
        );

        field.getInputMap(
                JComponent.WHEN_FOCUSED
        ).put(
                KeyStroke.getKeyStroke("ESCAPE"),
                "closeSuggestion"
        );

        field.getActionMap().put(
                "closeSuggestion",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(
                            java.awt.event.ActionEvent e
                    ) {

                        popup.setVisible(false);

                        field.requestFocusInWindow();
                    }
                }
        );

        list.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e
                    ) {

                        if (e.getClickCount() == 1) {

                            String selected =
                                    list.getSelectedValue();

                            if (selected != null) {

                                String city =
                                        selected.substring(
                                                0,
                                                selected.lastIndexOf(" (")
                                        );

                                popup.setVisible(false);

                                field.setText(city);

                                field.setCaretPosition(
                                        city.length()
                                );

                                SwingUtilities.invokeLater(() ->
                                        field.requestFocusInWindow()
                                );
                            }
                        }
                    }
                }
        );

        field.addFocusListener(
                new java.awt.event.FocusAdapter() {

                    @Override
                    public void focusLost(
                            java.awt.event.FocusEvent e
                    ) {

                        SwingUtilities.invokeLater(() -> {

                            if (!field.hasFocus()) {
                                popup.setVisible(false);
                            }
                        });
                    }
                }
        );
    }

    private void setupCalendar(JTextField field) {

        calendarPopup = new JPopupMenu();

        calendarPopup.setFocusable(false);

        JPanel calendarPanel =
                new JPanel(new BorderLayout());

        calendarPanel.setPreferredSize(
                new Dimension(320, 330)
        );

        calendarPanel.setBackground(Color.WHITE);

        JPanel header =
                new JPanel(new BorderLayout());

        header.setBackground(
                new Color(20, 55, 100)
        );

        JButton previousButton =
                new JButton("‹");

        JButton nextButton =
                new JButton("›");

        previousButton.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        nextButton.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        previousButton.setForeground(Color.WHITE);
        nextButton.setForeground(Color.WHITE);

        previousButton.setBackground(
                new Color(20, 55, 100)
        );

        nextButton.setBackground(
                new Color(20, 55, 100)
        );

        previousButton.setBorderPainted(false);
        nextButton.setBorderPainted(false);

        previousButton.setFocusPainted(false);
        nextButton.setFocusPainted(false);

        monthYearLabel =
                new JLabel(
                        "",
                        SwingConstants.CENTER
                );

        monthYearLabel.setForeground(Color.WHITE);

        monthYearLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        17
                )
        );

        header.add(
                previousButton,
                BorderLayout.WEST
        );

        header.add(
                monthYearLabel,
                BorderLayout.CENTER
        );

        header.add(
                nextButton,
                BorderLayout.EAST
        );

        calendarPanel.add(
                header,
                BorderLayout.NORTH
        );

        JPanel weekPanel =
                new JPanel(
                        new GridLayout(1, 7)
                );

        weekPanel.setBackground(Color.WHITE);

        String[] days = {
                "Sun",
                "Mon",
                "Tue",
                "Wed",
                "Thu",
                "Fri",
                "Sat"
        };

        for (String day : days) {

            JLabel label =
                    new JLabel(
                            day,
                            SwingConstants.CENTER
                    );

            label.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            12
                    )
            );

            weekPanel.add(label);
        }

        JPanel topPanel =
                new JPanel(
                        new BorderLayout()
                );

        topPanel.setBackground(Color.WHITE);

        topPanel.add(
                weekPanel,
                BorderLayout.CENTER
        );

        calendarPanel.add(
                topPanel,
                BorderLayout.CENTER
        );

        calendarDaysPanel =
                new JPanel(
                        new GridLayout(6, 7, 3, 3)
                );

        calendarDaysPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        8,
                        8,
                        8
                )
        );

        calendarDaysPanel.setBackground(
                Color.WHITE
        );

        calendarPanel.add(
                calendarDaysPanel,
                BorderLayout.SOUTH
        );

        calendarPopup.add(calendarPanel);

        currentMonth =
                YearMonth.now();

        previousButton.addActionListener(e -> {

            YearMonth minimumMonth =
                    YearMonth.now();

            if (currentMonth.isAfter(minimumMonth)) {

                currentMonth =
                        currentMonth.minusMonths(1);

                updateCalendar();
            }
        });

        nextButton.addActionListener(e -> {

            currentMonth =
                    currentMonth.plusMonths(1);

            updateCalendar();
        });

        field.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e
                    ) {

                        currentMonth =
                                YearMonth.now();

                        updateCalendar();

                        SwingUtilities.invokeLater(() -> {

                            calendarPopup.show(
                                    field,
                                    0,
                                    field.getHeight()
                            );

                            field.requestFocusInWindow();
                        });
                    }
                }
        );

        field.addFocusListener(
                new java.awt.event.FocusAdapter() {

                    @Override
                    public void focusGained(
                            java.awt.event.FocusEvent e
                    ) {

                        currentMonth =
                                YearMonth.now();

                        updateCalendar();

                        SwingUtilities.invokeLater(() -> {

                            if (!calendarPopup.isVisible()) {

                                calendarPopup.show(
                                        field,
                                        0,
                                        field.getHeight()
                                );

                                field.requestFocusInWindow();
                            }
                        });
                    }
                }
        );
    }

    private void updateCalendar() {

        if (calendarDaysPanel == null
                || monthYearLabel == null) {
            return;
        }

        calendarDaysPanel.removeAll();

        monthYearLabel.setText(
                currentMonth.getMonth().toString()
                        + " "
                        + currentMonth.getYear()
        );

        LocalDate today =
                LocalDate.now();

        LocalDate firstDay =
                currentMonth.atDay(1);

        int startDay =
                convertDayOfWeek(
                        firstDay.getDayOfWeek()
                );

        int daysInMonth =
                currentMonth.lengthOfMonth();

        for (int i = 0; i < 42; i++) {

            if (i < startDay
                    || i >= startDay + daysInMonth) {

                JLabel empty =
                        new JLabel("");

                empty.setOpaque(true);

                empty.setBackground(Color.WHITE);

                calendarDaysPanel.add(empty);

                continue;
            }

            int day =
                    i - startDay + 1;

            LocalDate selectedDate =
                    currentMonth.atDay(day);

            JButton dayButton =
                    new JButton(
                            String.valueOf(day)
                    );

            dayButton.setFocusPainted(false);

            dayButton.setFont(
                    new Font(
                            "Arial",
                            Font.PLAIN,
                            13
                    )
            );

            dayButton.setBorder(
                    BorderFactory.createEmptyBorder()
            );

            if (selectedDate.isBefore(today)) {

                dayButton.setEnabled(false);

                dayButton.setForeground(
                        new Color(170, 170, 170)
                );

            } else if (selectedDate.equals(today)) {

                dayButton.setBackground(
                        new Color(20, 55, 100)
                );

                dayButton.setForeground(
                        Color.WHITE
                );

            } else {

                dayButton.setBackground(
                        Color.WHITE
                );

                dayButton.setForeground(
                        Color.BLACK
                );
            }

            dayButton.addMouseListener(
                    new MouseAdapter() {

                        @Override
                        public void mouseEntered(
                                MouseEvent e
                        ) {

                            if (dayButton.isEnabled()
                                    && !selectedDate.equals(today)) {

                                dayButton.setBackground(
                                        new Color(220, 235, 250)
                                );
                            }
                        }

                        @Override
                        public void mouseExited(
                                MouseEvent e
                        ) {

                            if (dayButton.isEnabled()
                                    && !selectedDate.equals(today)) {

                                dayButton.setBackground(
                                        Color.WHITE
                                );
                            }
                        }
                    }
            );

            dayButton.addActionListener(e -> {

                dateField.setText(
                        selectedDate.format(
                                dateFormatter
                        )
                );

                calendarPopup.setVisible(false);

                dateField.requestFocusInWindow();
            });

            calendarDaysPanel.add(dayButton);
        }

        calendarDaysPanel.revalidate();
        calendarDaysPanel.repaint();
    }

    private int convertDayOfWeek(
            DayOfWeek dayOfWeek
    ) {

        switch (dayOfWeek) {

            case SUNDAY:
                return 0;

            case MONDAY:
                return 1;

            case TUESDAY:
                return 2;

            case WEDNESDAY:
                return 3;

            case THURSDAY:
                return 4;

            case FRIDAY:
                return 5;

            case SATURDAY:
                return 6;

            default:
                return 0;
        }
    }

    private JPanel createUpcomingCard() {

        JPanel card =
                new JPanel(new BorderLayout());

        card.setBorder(
                BorderFactory.createTitledBorder(
                        "Upcoming Flight"
                )
        );

        JLabel message =
                new JLabel(
                        "No upcoming booking found.",
                        SwingConstants.CENTER
                );

        message.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        16
                )
        );

        card.add(
                message,
                BorderLayout.CENTER
        );

        return card;
    }

    private JPanel createRecentBookingsCard() {

        JPanel card =
                new JPanel(new BorderLayout());

        card.setBorder(
                BorderFactory.createTitledBorder(
                        "Recent Bookings"
                )
        );

        JLabel message =
                new JLabel(
                        "No bookings yet.",
                        SwingConstants.CENTER
                );

        message.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        16
                )
        );

        card.add(
                message,
                BorderLayout.CENTER
        );

        return card;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new UserDashboard(
                    1,
                    "Satyam"
            ).setVisible(true);

        });
    }
}
