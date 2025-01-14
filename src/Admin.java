import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class Admin extends JPanel {
    private Image backgroundImage;
    private JFrame adminFrame;
    private Connection connection;

    public Admin() {
        loadBackgroundImage();

        adminFrame = createFrame("Exercises List", "icon.png", 880, 590);
        adminFrame.setContentPane(this);
        connection = DatabaseConnection.getConnection(); // Use the external connection class

        setupUI();
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(new File("background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void setupUI() {
        this.setLayout(new BorderLayout());

        // Create text area to display exercises
        JTextArea exerciseListArea = new JTextArea();
        exerciseListArea.setEditable(false);
        exerciseListArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(exerciseListArea);
        add(scrollPane, BorderLayout.CENTER);

        // Retrieve exercises from database
        try (PreparedStatement statement = connection.prepareStatement("SELECT name, category, description FROM exercises")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                String description = resultSet.getString("description");
                exerciseListArea.append("Name: " + name + "\nCategory: " + category + "\nDescription: " + description + "\n\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve exercises.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // Panel for adding new exercise
        JPanel addExercisePanel = new JPanel();
        addExercisePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        addExercisePanel.setOpaque(false);

        JLabel nameLabel = new JLabel("Exercise Name:");
        JTextField nameField = new JTextField(10);
        JLabel categoryLabel = new JLabel("Category:");
        JTextField categoryField = new JTextField(10);
        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField(15);
        JButton addExerciseButton = createStyledButton("Add Exercise");

        addExerciseButton.addActionListener(e -> {
            String exerciseName = nameField.getText().trim();
            String category = categoryField.getText().trim();
            String description = descriptionField.getText().trim();

            if (exerciseName.isEmpty() || category.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            } else {
                try {
                    // Get the next available primary key
                    int nextId = getNextPrimaryKey();

                    try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO exercises (exercise_id, name, category, description) VALUES (?, ?, ?, ?)")) {
                        insertStatement.setInt(1, nextId);
                        insertStatement.setString(2, exerciseName);
                        insertStatement.setString(3, category);
                        insertStatement.setString(4, description);
                        insertStatement.executeUpdate();
                        exerciseListArea.append("Name: " + exerciseName + "\nCategory: " + category + "\nDescription: " + description + "\n\n");
                        nameField.setText("");
                        categoryField.setText("");
                        descriptionField.setText("");
                        JOptionPane.showMessageDialog(this, "Exercise added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to add exercise to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        addExercisePanel.add(nameLabel);
        addExercisePanel.add(nameField);
        addExercisePanel.add(categoryLabel);
        addExercisePanel.add(categoryField);
        addExercisePanel.add(descriptionLabel);
        addExercisePanel.add(descriptionField);
        addExercisePanel.add(addExerciseButton);
        add(addExercisePanel, BorderLayout.NORTH);

        // Create a Log Out button and position it at the bottom-right
        JButton logoutButton = createStyledButton("Log Out");
        logoutButton.addActionListener(e -> {
            adminFrame.dispose();
            new Application().run();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        adminFrame.setVisible(true);
        adminFrame.setLocationRelativeTo(null);
    }

    private int getNextPrimaryKey() throws SQLException {
        String query = "SELECT MAX(exercise_id) FROM exercises";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) + 1;
            } else {
                return 1; // Start from 1 if table is empty
            }
        }
    }

    private JFrame createFrame(String title, String iconPath, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIcon(frame, iconPath);
        return frame;
    }

    private void setIcon(JFrame frame, String iconPath) {
        try {
            Image icon = ImageIO.read(new File(iconPath));
            frame.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Cooper Black", Font.BOLD, 18));
        button.setFocusPainted(false);
        return button;
    }
}
