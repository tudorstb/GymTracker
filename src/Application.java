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

// Custom exception for missing username
class UsernameMissingException extends Exception {
    public UsernameMissingException(String message) {
        super(message);
    }
}

// Custom exception for missing password
class PasswordMissingException extends Exception {
    public PasswordMissingException(String message) {
        super(message);
    }
}

class Application {
    private JFrame frame;
    private BackgroundPanel panel;
    private Connection connection;

    // Constructor
    public Application() {
        frame = createFrame("GYM TRACKER", "icon.png", 340, 590);
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
    }

    // Create and setup the frame
    private JFrame createFrame(String title, String iconPath, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
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

        // Title Label
        JLabel loginLabel = createLabel("Log in", 24, SwingConstants.CENTER);
        panel.add(loginLabel, BorderLayout.NORTH);

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
        panel.add(createLabel(labelText, 16, SwingConstants.RIGHT), gbc);
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
        button.setFont(new Font("Cooper Black", Font.BOLD, 16));
        return button;
    }

    private JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Cooper Black", Font.PLAIN, 16));
        return textField;
    }

    private JPasswordField createPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(new Font("Cooper Black", Font.PLAIN, 16));
        passwordField.setEchoChar('*');
        return passwordField;
    }
}
