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

public class Profile {
    private JFrame frame;
    private BackgroundPanel backgroundPanel;
    private String username;
    private String email;
    private int age;

    // Constructor: accepts an existing JFrame to modify
    public Profile(JFrame existingFrame) {
        this.frame = existingFrame; // Reuse the existing frame
        this.backgroundPanel = new BackgroundPanel("background.jpg");

        // Load username from file and fetch email & age from the database
        loadUserData();

        createUIComponents();
        frame.setContentPane(backgroundPanel); // Set new content
        frame.revalidate(); // Refresh the frame
        frame.repaint();
    }

    // Load username from name.txt and fetch the corresponding email and age from the database
    private void loadUserData() {
        // Read username from name.txt
        try (BufferedReader reader = new BufferedReader(new FileReader("name.txt"))) {
            username = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to load user data.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Fetch email and age from the database
        String url = "jdbc:postgresql://localhost:5432/fit_database";
        String dbUser = "gymuser";
        String dbPassword = "password123";

        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String query = "SELECT email, age FROM users WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    email = resultSet.getString("email");
                    age = resultSet.getInt("age");
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
        JLabel profileLabel = createLabel("Profile", 24, SwingConstants.CENTER);
        backgroundPanel.add(profileLabel, BorderLayout.NORTH);

        // Info Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Profile information
        JLabel nameLabel = createLabel("Name: " + (username != null ? username : "Unknown"), 21, SwingConstants.LEFT);
        JLabel emailLabel = createLabel("Email: " + (email != null ? email : "Unknown"), 21, SwingConstants.LEFT);
        JLabel ageLabel = createLabel("Age: " + (age > 0 ? age : "Unknown"), 21, SwingConstants.LEFT);

        // Add profile information to the info panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(nameLabel, gbc);

        gbc.gridy = 1;
        infoPanel.add(emailLabel, gbc);

        gbc.gridy = 2;
        infoPanel.add(ageLabel, gbc);

        backgroundPanel.add(infoPanel, BorderLayout.CENTER);

        // Edit Profile Button in Top-Right Corner with Icon
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.setOpaque(false); // Transparent background

        JButton editProfileButton = createIconButton("settingsButton.png");
        editProfileButton.addActionListener(e -> {
            // Change frame to Settings screen
            new SettingsWindow(frame); // Replace current content with settings screen
        });

        topRightPanel.add(editProfileButton);
        backgroundPanel.add(topRightPanel, BorderLayout.NORTH);

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
        button.setFont(new Font("Cooper Black", Font.BOLD, 21));
        button.setForeground(Color.BLACK); // Set button text color to black
        return button;
    }

    // Create Icon Button Helper
    private JButton createIconButton(String iconPath) {
        JButton button = new JButton();
        try {
            Image img = ImageIO.read(new File(iconPath));

            // Adjust the size as needed
            int width = 35;
            int height = 35;

            // Create a high-quality icon
            ImageIcon icon = new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));

            button.setIcon(icon);
            button.setPreferredSize(new Dimension(width + 10, height + 10)); // Add padding
            button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Set padding to improve spacing
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.setOpaque(false);

            // Add hover effect (optional)
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return button;
    }
}
