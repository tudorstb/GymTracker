import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountWindow {
    private JFrame createAccountFrame;
    private BackgroundPanel createAccountPanel;
    private Connection connection;

    // Constructor
    public CreateAccountWindow(String backgroundImagePath) {
        createAccountFrame = createFrame("Create Account", "icon.png", 520, 450);
        createAccountPanel = new BackgroundPanel(backgroundImagePath);
        createAccountPanel.setLayout(new BorderLayout());

        // Initialize database connection
        initializeDatabaseConnection();

        setupUIComponents();

        createAccountFrame.setContentPane(createAccountPanel);
        createAccountFrame.setVisible(true);
    }

    // Initialize the database connection
    private void initializeDatabaseConnection() {
        try {
            String url = "jdbc:postgresql://localhost:5432/fit_database";
            String user = "gymuser";
            String password = "password123";
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(createAccountFrame, "Failed to connect to the database.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Setup UI Components
    private void setupUIComponents() {
        // Title Label
        JLabel titleLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Cooper Black", Font.BOLD, 24));
        createAccountPanel.add(titleLabel, BorderLayout.NORTH);

        // Center Panel for input fields
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Input Fields
        JTextField usernameField = createTextField(15);
        addLabelAndField(inputPanel, gbc, "Username:", usernameField, 0);

        JTextField firstNameField = createTextField(15);
        addLabelAndField(inputPanel, gbc, "First Name:", firstNameField, 1);

        JTextField lastNameField = createTextField(15);
        addLabelAndField(inputPanel, gbc, "Last Name:", lastNameField, 2);

        JTextField ageField = createTextField(15);
        addLabelAndField(inputPanel, gbc, "Age:", ageField, 3);

        JTextField emailField = createTextField(15);
        addLabelAndField(inputPanel, gbc, "Email:", emailField, 4);

        // Password Input
        JPasswordField passwordField = createPasswordField(15);
        JButton togglePasswordButton = createToggleButton(passwordField);
        addLabelFieldAndButton(inputPanel, gbc, "Password:", passwordField, togglePasswordButton, 5);

        // Re-enter Password Input
        JPasswordField rePasswordField = createPasswordField(15);
        JButton toggleRePasswordButton = createToggleButton(rePasswordField);
        addLabelFieldAndButton(inputPanel, gbc, "Re-enter Password:", rePasswordField, toggleRePasswordButton, 6);

        // Gender Selection
        String[] genders = {"Male", "Female", "Other"};
        JComboBox<String> genderComboBox = new JComboBox<>(genders);
        addLabelAndField(inputPanel, gbc, "Gender:", genderComboBox, 7);

        // Add inputPanel to the center of the main panel
        createAccountPanel.add(inputPanel, BorderLayout.CENTER);

        // Create Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton createButton = createButton("Create");
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(createButton);

        // Add action to create account
        createButton.addActionListener(e -> {
            String username = usernameField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String ageText = ageField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String rePassword = new String(rePasswordField.getPassword());
            String gender = (String) genderComboBox.getSelectedItem();

            try {
                // Validate input
                if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || ageText.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(createAccountFrame, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                } else if (!password.equals(rePassword)) {
                    JOptionPane.showMessageDialog(createAccountFrame, "The passwords don't match.", "Input Error", JOptionPane.ERROR_MESSAGE);
                } else if (!isEmailValid(email)) {
                    JOptionPane.showMessageDialog(createAccountFrame, "The email is not valid.", "Input Error", JOptionPane.ERROR_MESSAGE);
                } else if (!isPasswordValid(password)) {
                    JOptionPane.showMessageDialog(createAccountFrame, "Password does not meet the requirements ,You need at least 8 elements and a number.", "Input Error", JOptionPane.WARNING_MESSAGE);
                } else {
                    int age = Integer.parseInt(ageText);
                    if (age < 0) {
                        throw new NegativeAgeException("Age cannot be negative.");
                    }
                    createUser(username, firstName, lastName, age, email, password, gender);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(createAccountFrame, "Age must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (NegativeAgeException ex) {
                JOptionPane.showMessageDialog(createAccountFrame, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        createAccountPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    // Create a new user in the database
    private void createUser(String username, String firstName, String lastName, int age, String email, String password, String gender) {
        // Check if username or email already exists in the database
        if (isUsernameTaken(username)) {
            JOptionPane.showMessageDialog(createAccountFrame, "Username is already taken. Please choose a different username.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;  // Stop the account creation process
        }

        if (isEmailTaken(email)) {
            JOptionPane.showMessageDialog(createAccountFrame, "Email is already associated with another account. Please use a different email.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;  // Stop the account creation process
        }

        // If no conflicts, proceed to insert the new user
        String query = "INSERT INTO users (username, first_name, last_name, age, email, password, gender) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setInt(4, age);
            statement.setString(5, email);
            statement.setString(6, password);
            statement.setString(7, gender);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(createAccountFrame, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            createAccountFrame.dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(createAccountFrame, "Failed to create account: " + e.getMessage(), "Account Creation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Check if the username already exists in the database
    private boolean isUsernameTaken(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;  // Returns true if the username exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if the email already exists in the database
    private boolean isEmailTaken(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;  // Returns true if the email exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to validate the password
    private boolean isPasswordValid(String password) {
        return password.length() >= 8 && password.matches(".*\\d.*");
    }

    // Helper method to validate the email format
    private boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Method to create a JTextField with a specified length
    private JTextField createTextField(int length) {
        JTextField textField = new JTextField(length);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        return textField;
    }

    // Method to create a JPasswordField with a specified length
    private JPasswordField createPasswordField(int length) {
        JPasswordField passwordField = new JPasswordField(length);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        return passwordField;
    }

    // Method to create a toggle button for password visibility
    private JButton createToggleButton(JPasswordField passwordField) {
        JButton toggleButton = new JButton("Show");
        toggleButton.setFont(new Font("Arial", Font.PLAIN, 12));
        toggleButton.addActionListener(e -> {
            if (passwordField.echoCharIsSet()) {
                passwordField.setEchoChar((char) 0);
                toggleButton.setText("Hide");
            } else {
                passwordField.setEchoChar('*');
                toggleButton.setText("Show");
            }
        });
        return toggleButton;
    }

    // Method to add a label and text field to the input panel
    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int gridy) {
        gbc.gridx = 0;
        gbc.gridy = gridy;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.gridy = gridy;
        panel.add(field, gbc);
    }

    // Method to add a label, text field, and toggle button for password fields
    private void addLabelFieldAndButton(JPanel panel, GridBagConstraints gbc, String labelText, JPasswordField passwordField, JButton toggleButton, int gridy) {
        gbc.gridx = 0;
        gbc.gridy = gridy;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.gridy = gridy;
        panel.add(passwordField, gbc);

        gbc.gridx = 2;
        gbc.gridy = gridy;
        panel.add(toggleButton, gbc);
    }

    // Method to create a JButton
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(0, 150, 255));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }

    // Method to create the JFrame
    private JFrame createFrame(String title, String iconPath, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);  // Center the frame
        frame.setUndecorated(true);  // Remove window border (title bar, minimize, maximize)
        try {
            frame.setIconImage(ImageIO.read(new File(iconPath)));  // Set icon image
        } catch (IOException e) {
            e.printStackTrace();
        }
        return frame;
    }

    // Custom exception for negative age
    private static class NegativeAgeException extends Exception {
        public NegativeAgeException(String message) {
            super(message);
        }
    }
}
