package gui;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/flight_booking";
    private static final String USER = "root";
    private static final String PASSWORD = "CHANGE_ME";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        try (Connection con = getConnection()) {
            if (con != null) {
                System.out.println("MySQL Connected Successfully!");
            } else {
                System.out.println("MySQL Connection Failed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
