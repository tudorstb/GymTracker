import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.imageio.ImageIO;

class Application {
    private JFrame frame;
    private BackgroundPanel panel;
    private Connection connection;

    // Custom exception for missing username
    public class UsernameMissingException extends Exception {
        public UsernameMissingException(String message) {
            super(message);
        }
    }

    // Custom exception for missing password
    public class PasswordMissingException extends Exception {
        public PasswordMissingException(String message) {
            super(message);
        }
    }

    // Constructor
    public Application() {
        frame = createFrame("GYM TRACKER", "icon.png", 880, 590);
        panel = new BackgroundPanel("background.jpg");
        frame.setContentPane(panel);

        // Initialize database connection
        initializeDatabaseConnection();
    }

    // Initialize the database connection
    private void initializeDatabaseConnection() {
        try {
            // Update with your database URL, user, and password
            String url = "jdbc:postgresql://localhost:5432/fit_database";
            String user = "gymuser";
            String password = "password123";

            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to connect to the database.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Run the application
    public void run() {
        createUIComponents();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null); // Center the frame on the screen
    }

    // Create and setup the frame
    private JFrame createFrame(String title, String iconPath, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setUndecorated(true);  // Remove title bar and system buttons
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIcon(frame, iconPath);
        return frame;
    }

    // Set the application icon
    private void setIcon(JFrame frame, String iconPath) {
        try {
            Image icon = ImageIO.read(new File(iconPath));
            frame.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Create UI Components
    private void createUIComponents() {
        panel.setLayout(new BorderLayout());

        // Title Panel with Exit Button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false); // To maintain background visibility

        // Title Label
        JLabel loginLabel = createLabel("Log in", 24, SwingConstants.CENTER);
        titlePanel.add(loginLabel, BorderLayout.CENTER);

        // Exit Button
        JButton exitButton = createButton("Exit");
        exitButton.setBackground(new Color(178, 34, 34)); // Red color for emphasis
        exitButton.addActionListener(e -> System.exit(0)); // Exit the program when clicked
        titlePanel.add(exitButton, BorderLayout.EAST); // Align exit button to the right

        panel.add(titlePanel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // User Input
        JTextField userInput = createTextField(10);
        JPasswordField passwordField = createPasswordField(10);

        // Add User and Password Inputs
        addLabelAndField(inputPanel, gbc, "User:", userInput, 0);
        addLabelAndField(inputPanel, gbc, "Password:", passwordField, 1);

        // Log In Button
        JButton loginButton = createButton("Log in");
        loginButton.addActionListener(e -> {
            String username = userInput.getText();
            String password = new String(passwordField.getPassword());

            try {
                // Check if username or password is missing
                if (username.isEmpty()) {
                    throw new UsernameMissingException("Username is missing.");
                }
                if (password.isEmpty()) {
                    throw new PasswordMissingException("Password is missing.");
                }

                if (authenticateUser(username, password)) {
                    saveUsernameToFile(username);
                    new MainMenuWindow(); // Call MainMenuWindow without parameters
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (UsernameMissingException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            } catch (PasswordMissingException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        inputPanel.add(loginButton, gbc);

        panel.add(inputPanel, BorderLayout.CENTER);

        // Create Account Button
        JButton createAccountButton = createButton("Create Account");
        createAccountButton.setBackground(new Color(70, 130, 180));
        createAccountButton.addActionListener(e -> new CreateAccountWindow("BackgroundImageForLogIn.png"));
        panel.add(createAccountButton, BorderLayout.SOUTH);
    }

    // Method to authenticate user from the database
    private boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Return true if a match is found
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveUsernameToFile(String username) {
        File file = new File("name.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(username); // Write the username to the file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper to add labels and text fields
    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(createLabel(labelText, 21, SwingConstants.RIGHT), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JLabel createLabel(String text, int fontSize, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(new Font("Cooper Black", Font.PLAIN, fontSize));
        return label;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Cooper Black", Font.BOLD, 21));
        return button;
    }

    private JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Cooper Black", Font.PLAIN, 21));
        return textField;
    }

    private JPasswordField createPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(new Font("Cooper Black", Font.PLAIN, 21));
        passwordField.setEchoChar('*');
        return passwordField;
    }
}
