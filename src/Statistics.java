import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.imageio.ImageIO;

public class Statistics {
    private JFrame frame;
    private BackgroundPanel backgroundPanel;
    private String username;
    private int totalWorkouts;
    private double averageWorkoutDuration;

    // Constructor: accepts an existing JFrame to modify
    public Statistics(JFrame existingFrame) {
        this.frame = existingFrame; // Reuse the existing frame
        this.backgroundPanel = new BackgroundPanel("background.jpg");

        // Load username from file and fetch statistics from the database
        loadUserData();
        fetchStatistics();

        createUIComponents();
        frame.setContentPane(backgroundPanel); // Set new content
        frame.revalidate(); // Refresh the frame
        frame.repaint();
    }

    // Load username from name.txt
    private void loadUserData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("name.txt"))) {
            username = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to load user data.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    // Fetch statistics from the database
    private void fetchStatistics() {
        String url = "jdbc:postgresql://localhost:5432/fit_database";
        String dbUser = "gymuser";
        String dbPassword = "password123";

        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword)) {
            // Get total workouts
            String workoutCountQuery = "SELECT COUNT(*) FROM workouts WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
            try (PreparedStatement statement = connection.prepareStatement(workoutCountQuery)) {
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    totalWorkouts = resultSet.getInt(1);
                }
            }

            // Get average workout duration
            String averageDurationQuery = "SELECT AVG(EXTRACT(EPOCH FROM duration)) / 60 FROM workouts WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
            try (PreparedStatement statement = connection.prepareStatement(averageDurationQuery)) {
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    averageWorkoutDuration = resultSet.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create UI Components
    private void createUIComponents() {
        backgroundPanel.setLayout(new BorderLayout());

        // Title Label
        JLabel statisticsLabel = createLabel("Statistics", 24, SwingConstants.CENTER);
        backgroundPanel.add(statisticsLabel, BorderLayout.NORTH);

        // Info Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Display statistics
        JLabel totalWorkoutsLabel = createLabel("Total Workouts: " + totalWorkouts, 16, SwingConstants.LEFT);
        JLabel averageDurationLabel = createLabel("Average Workout Duration: " + (averageWorkoutDuration > 0 ? String.format("%.2f", averageWorkoutDuration) + " minutes" : "No workouts yet"), 16, SwingConstants.LEFT);

        // Add statistics to the info panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(totalWorkoutsLabel, gbc);

        gbc.gridy = 1;
        infoPanel.add(averageDurationLabel, gbc);

        backgroundPanel.add(infoPanel, BorderLayout.CENTER);

        // Back Button
        JButton backButton = createButton("Back");
        backButton.addActionListener(e -> {
            MainMenuWindow mainMenu = new MainMenuWindow(frame); // Go back to Main Menu
            mainMenu.show();
        });
        backgroundPanel.add(backButton, BorderLayout.SOUTH);
    }

    // Create Label Helper
    private JLabel createLabel(String text, int fontSize, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(new Font("Cooper Black", Font.PLAIN, fontSize));
        return label;
    }

    // Create Button Helper
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Cooper Black", Font.BOLD, 16));
        button.setForeground(Color.BLACK); // Set button text color to black
        return button;
    }
}
