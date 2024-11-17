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
        initializeDatabaseConnection();
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
    private void initializeDatabaseConnection() {
        try {
            String url = "jdbc:postgresql://localhost:5432/fit_database";
            String user = "gymuser";
            String password = "password123";
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(adminFrame, "Failed to connect to the database.", "Connection Error", JOptionPane.ERROR_MESSAGE);
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
        try (PreparedStatement statement = connection.prepareStatement("SELECT name FROM exercises")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                exerciseListArea.append("Name: " + name + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve exercises.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

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
