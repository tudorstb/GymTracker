import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        inputPanel.setOpaque(false); // Transparent background to show main background image
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Consistent spacing

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
        buttonPanel.add(Box.createVerticalStrut(10)); // Add spacing above the button
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

            // Validate input
            if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || ageText.isEmpty() || email.isEmpty() || password.isEmpty() || !password.equals(rePassword)) {
                JOptionPane.showMessageDialog(createAccountFrame, "Please fill all fields correctly.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                createUser(username, firstName, lastName, Integer.parseInt(ageText), email, password, gender);
            }
        });

        createAccountPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    // Create a new user in the database
    private void createUser(String username, String firstName, String lastName, int age, String email, String password, String gender) {
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

    // Create and setup the frame
    private JFrame createFrame(String title, String iconPath, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIcon(frame, iconPath);
        return frame;
    }

    // Set the frame icon
    private void setIcon(JFrame frame, String iconPath) {
        try {
            Image icon = ImageIO.read(new File(iconPath));
            frame.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to add labels and fields
    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(createLabel(labelText, 16, SwingConstants.RIGHT), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    // Helper method to add labels, fields, and toggle buttons
    private void addLabelFieldAndButton(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, JButton button, int row) {
        addLabelAndField(panel, gbc, labelText, field, row);
        gbc.gridx = 2;
        panel.add(button, gbc);
    }

    // Create Label Helper
    private JLabel createLabel(String text, int fontSize, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(new Font("Cooper Black", Font.PLAIN, fontSize));
        return label;
    }

    // Create TextField Helper
    private JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Cooper Black", Font.PLAIN, 16));
        return textField;
    }

    // Create PasswordField Helper
    private JPasswordField createPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(new Font("Cooper Black", Font.PLAIN, 16));
        passwordField.setEchoChar('*');
        return passwordField;
    }

    // Create Toggle Button for Password Visibility
    private JButton createToggleButton(JPasswordField passwordField) {
        JButton toggleButton = createButton("Show");
        toggleButton.setFont(new Font("Cooper Black", Font.PLAIN, 16));
        toggleButton.addActionListener(e -> {
            boolean isVisible = passwordField.getEchoChar() == (char) 0;
            passwordField.setEchoChar(isVisible ? '*' : (char) 0);
            toggleButton.setText(isVisible ? "Show" : "Hide");
        });
        return toggleButton;
    }

    // Create Button Helper
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Cooper Black", Font.BOLD, 16));
        button.setBackground(new Color(70, 130, 180));
        return button;
    }
}
