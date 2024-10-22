import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    // Method to connect to PostgreSQL database
    public Connection connectToDatabase() {
        String url = "jdbc:postgresql://localhost:5432/fit";
        String user = "your_username";
        String password = "your_password";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
