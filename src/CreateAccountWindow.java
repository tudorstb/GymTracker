import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CreateAccountWindow extends JPanel {
    private Image backgroundImage;
    private JFrame createAccountFrame;
    private Connection connection;

    // Constructor
    public CreateAccountWindow(String backgroundImagePath) {

        try {
            backgroundImage = ImageIO.read(new File(backgroundImagePath));
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(createAccountFrame, "Background image not found. Please check the file path.", "File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(createAccountFrame, "Error loading the background image.", "File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }


        createAccountFrame = createFrame("Create Account", "icon.png", 520, 450);
        createAccountFrame.setContentPane(this);

        connection = DatabaseConnection.getConnection(); // Use the external connection class

        setupUIComponents();

        createAccountFrame.setVisible(true);
    }

    // Paint method to render the background image
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // Initialize the database connection



    // Setup UI Components
    private void setupUIComponents() {
        setLayout(new BorderLayout());

        // Title Label
        JLabel titleLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Cooper Black", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

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
        add(inputPanel, BorderLayout.CENTER);

        // Create Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        // Create Account Button (left side) with blue color
        JButton createButton = createButton("Create", Color.BLUE);
        createButton.setPreferredSize(new Dimension(150, 28));
        buttonPanel.add(createButton);
        buttonPanel.add(Box.createHorizontalStrut(10));

        // Cancel Button (right side)
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Cooper Black", Font.BOLD, 16));
        cancelButton.setBackground(new Color(255, 50, 50));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(150, 28));
        cancelButton.addActionListener(e -> createAccountFrame.dispose());  // Close the window when clicked

        // Create a panel for cancel button to align it to the right
        JPanel cancelButtonPanel = new JPanel();
        cancelButtonPanel.setOpaque(false);
        cancelButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        cancelButtonPanel.add(cancelButton);

        // Add the cancel button panel to the buttonPanel (right side)
        buttonPanel.add(cancelButtonPanel);

        add(buttonPanel, BorderLayout.SOUTH);

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
                    JOptionPane.showMessageDialog(createAccountFrame, "Password does not meet the requirements, you need at least 8 characters and a number.", "Input Error", JOptionPane.WARNING_MESSAGE);
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
            return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check if the email already exists in the database
    private boolean isEmailTaken(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Validate email format
    private boolean isEmailValid(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Validate password format
    private boolean isPasswordValid(String password) {
        return password.length() >= 8 && password.matches(".*\\d.*");
    }

    // Helper method to create a text field
    private JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Cooper Black", Font.PLAIN, 16));
        return textField;
    }

    // Helper method to create a password field
    private JPasswordField createPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(new Font("Cooper Black", Font.PLAIN, 16));
        return passwordField;
    }

    // Helper method to create a toggle password button
    private JButton createToggleButton(JPasswordField passwordField) {
        JButton toggleButton = new JButton("Show");
        toggleButton.setFont(new Font("Cooper Black", Font.PLAIN, 16));
        toggleButton.addActionListener(e -> {
            boolean isPasswordVisible = passwordField.echoCharIsSet();
            passwordField.setEchoChar(isPasswordVisible ? (char) 0 : '*');
            toggleButton.setText(isPasswordVisible ? "Show" : "Hide");
        });
        return toggleButton;
    }

    // Helper method to add labels and text fields to inputPanel
    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Cooper Black", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    // Helper method to add labels, text fields, and buttons to inputPanel
    private void addLabelFieldAndButton(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, JButton button, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Cooper Black", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);

        gbc.gridx = 2;
        panel.add(button, gbc);
    }

    // Helper method to create a button
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Cooper Black", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        return button;
    }

    // Helper method to create a JFrame with specific settings
    private JFrame createFrame(String title, String iconPath, int width, int height) {

        JFrame frame = new JFrame(title);
        frame.setIconImage(new ImageIcon(iconPath).getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setUndecorated(true);  // Removes title bar and window buttons
        return frame;
    }

    // Custom exception for negative age
    private class NegativeAgeException extends Exception {
        public NegativeAgeException(String message) {
            super(message);
        }
    }
}
