import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsWindow {
    private JFrame frame;
    private BackgroundPanel backgroundPanel;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField ageField;
    private JTextField heightField;
    private JTextField weightField;
    private String currentUsername;
    private String currentEmail;
    private int currentAge;
    private Double currentHeight; // can be null
    private Double currentWeight; // can be null

    public SettingsWindow(JFrame existingFrame) {
        this.frame = existingFrame; // Reuse existing frame
        this.backgroundPanel = new BackgroundPanel("background.jpg");

        loadCurrentData();
        setupUI();

        frame.setContentPane(backgroundPanel); // Set new content
        frame.revalidate(); // Refresh the frame
        frame.repaint();
    }

    private void loadCurrentData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("name.txt"))) {
            currentUsername = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to load user data.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String url = "jdbc:postgresql://localhost:5432/fit_database";
        String dbUser = "gymuser";
        String dbPassword = "password123";

        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String query = "SELECT email, age, height, weight FROM users WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, currentUsername);
                var resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    currentEmail = resultSet.getString("email");
                    currentAge = resultSet.getInt("age");
                    currentHeight = resultSet.getObject("height") != null ? resultSet.getDouble("height") : null;
                    currentWeight = resultSet.getObject("weight") != null ? resultSet.getDouble("weight") : null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupUI() {
        backgroundPanel.setLayout(new BorderLayout());

        // Info Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username, Email, Age, Height, and Weight Fields
        usernameField = createTextField(currentUsername, 15);
        emailField = createTextField(currentEmail, 15);
        ageField = createTextField(String.valueOf(currentAge), 15);
        heightField = createTextField(currentHeight != null ? String.valueOf(currentHeight) : "", 15);
        weightField = createTextField(currentWeight != null ? String.valueOf(currentWeight) : "", 15);

        addLabelAndField(infoPanel, gbc, "Username:", usernameField, 0);
        addLabelAndField(infoPanel, gbc, "Email:", emailField, 1);
        addLabelAndField(infoPanel, gbc, "Age:", ageField, 2);
        addLabelAndField(infoPanel, gbc, "Height (cm):", heightField, 3);
        addLabelAndField(infoPanel, gbc, "Weight (kg):", weightField, 4);

        backgroundPanel.add(infoPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);

        // Save Button
        JButton saveButton = createButton("Save");
        saveButton.addActionListener(e -> saveChanges());
        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        savePanel.setOpaque(false);
        savePanel.add(saveButton);
        buttonPanel.add(savePanel, BorderLayout.NORTH);

        // Back Button
        JButton backButton = createButton("Back");
        backButton.addActionListener(e -> new Profile(frame));
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backPanel.setOpaque(false);
        backPanel.add(backButton);
        buttonPanel.add(backPanel, BorderLayout.SOUTH);

        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveChanges() {
        String newUsername = usernameField.getText().trim();
        String newEmail = emailField.getText().trim();
        int newAge;
        Double newHeight = null;
        Double newWeight = null;

        try {
            newAge = Integer.parseInt(ageField.getText().trim());
            if (newAge < 0) {
                JOptionPane.showMessageDialog(frame, "Age cannot be less than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Age must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (!heightField.getText().trim().isEmpty()) {
                newHeight = Double.parseDouble(heightField.getText().trim());
                if (newHeight < 0) {
                    JOptionPane.showMessageDialog(frame, "Height cannot be less than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            if (!weightField.getText().trim().isEmpty()) {
                newWeight = Double.parseDouble(weightField.getText().trim());
                if (newWeight < 0) {
                    JOptionPane.showMessageDialog(frame, "Weight cannot be less than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Height and Weight must be valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isEmailValid(newEmail)) {
            JOptionPane.showMessageDialog(frame, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String url = "jdbc:postgresql://localhost:5432/fit_database";
        String dbUser = "gymuser";
        String dbPassword = "password123";

        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String query = "UPDATE users SET username = ?, email = ?, age = ?, height = ?, weight = ? WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, newUsername);
                statement.setString(2, newEmail);
                statement.setInt(3, newAge);
                if (newHeight != null) statement.setDouble(4, newHeight);
                else statement.setNull(4, java.sql.Types.DOUBLE);
                if (newWeight != null) statement.setDouble(5, newWeight);
                else statement.setNull(5, java.sql.Types.DOUBLE);
                statement.setString(6, currentUsername);
                statement.executeUpdate();

                if (!newUsername.equals(currentUsername)) {
                    rewriteUsernameInFile(newUsername);
                }

                currentUsername = newUsername;
                currentEmail = newEmail;
                currentAge = newAge;
                currentHeight = newHeight;
                currentWeight = newWeight;

                JOptionPane.showMessageDialog(frame, "Changes saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to save changes.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rewriteUsernameInFile(String newUsername) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("name.txt"))) {
            writer.write(newUsername);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isEmailValid(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Cooper Black", Font.BOLD, 16));
        return button;
    }

    private JTextField createTextField(String text, int columns) {
        JTextField textField = new JTextField(text, columns);
        textField.setFont(new Font("Cooper Black", Font.PLAIN, 16));
        return textField;
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;

        // Create label with the same style as the text fields and set color to orange
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Cooper Black", Font.PLAIN, 16));

        panel.add(label, gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
}
