import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/fit_database";
    private static final String USER = "gymuser";
    private static final String PASSWORD = "password123";
    private static Connection connection;

    // Private constructor to prevent instantiation
    private DatabaseConnection() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to connect to the database.");
            }
        }
        return connection;
    }
}
