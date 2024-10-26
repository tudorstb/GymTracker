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
    private String currentUsername;
    private String currentEmail;
    private int currentAge;

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
            String query = "SELECT email, age FROM users WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, currentUsername);
                var resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    currentEmail = resultSet.getString("email");
                    currentAge = resultSet.getInt("age");
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

        // Username field
        usernameField = new JTextField(currentUsername, 15);
        emailField = new JTextField(currentEmail, 15);
        ageField = new JTextField(String.valueOf(currentAge), 15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(ageField, gbc);

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
        backButton.addActionListener(e -> {
            new Profile(frame); // Go back to Profile
        });
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

        try {
            newAge = Integer.parseInt(ageField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Age must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
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
            String query = "UPDATE users SET username = ?, email = ?, age = ? WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, newUsername);
                statement.setString(2, newEmail);
                statement.setInt(3, newAge);
                statement.setString(4, currentUsername);
                statement.executeUpdate();

                // Update name.txt if username changes
                if (!newUsername.equals(currentUsername)) {
                    rewriteUsernameInFile(newUsername);
                }

                // Update current values
                currentUsername = newUsername;
                currentEmail = newEmail;
                currentAge = newAge;

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
}
