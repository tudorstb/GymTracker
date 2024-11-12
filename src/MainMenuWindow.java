import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainMenuWindow extends JPanel {
    private Image backgroundImage;
    private JFrame mainMenuFrame;

    public MainMenuWindow(JFrame existingFrame) {
        this.mainMenuFrame = existingFrame;
        loadBackgroundImage(); // Ensure background is loaded
        setupUI();
    }

    public MainMenuWindow() {
        loadBackgroundImage(); // Ensure background is loaded

        mainMenuFrame = createFrame("Main Menu", "icon.png", 880, 590);
        mainMenuFrame.setContentPane(this);
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

    // Set up the UI components
    private void setupUI() {
        this.setLayout(new BorderLayout()); // Use this JPanel as the main container

        // Create Buttons
        JButton viewProfileButton = createStyledMenuButton("View Profile");
        JButton trackWorkoutButton = createStyledMenuButton("Track Workout");
        JButton statisticsButton = createStyledMenuButton("Statistics");
        JButton logOutButton = createStyledMenuButton("Log Out");

        // Action for View Profile Button
        viewProfileButton.addActionListener(e -> {
            new Profile(mainMenuFrame); // Modify the existing frame for the Profile screen
        });

        // Action for Statistics Button
        statisticsButton.addActionListener(e -> {
            new Statistics(mainMenuFrame); // Modify the existing frame for the Statistics screen
        });

        // Action for Log Out Button
        logOutButton.addActionListener(e -> {
            mainMenuFrame.dispose();
            new Application().run();
        });

        // Add View Profile, Statistics, and Track Workout Buttons to a Panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false); // Transparent to show background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing between buttons

        // Positioning of the Buttons
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(viewProfileButton, gbc);
        gbc.gridy = 1;
        buttonPanel.add(statisticsButton, gbc); // Added statistics button
        gbc.gridy = 2;
        buttonPanel.add(trackWorkoutButton, gbc);

        this.add(buttonPanel, BorderLayout.CENTER);

        // Create a panel to hold the Log Out button and position it at the bottom-right
        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomRightPanel.setOpaque(false); // Transparent background
        bottomRightPanel.add(logOutButton);
        this.add(bottomRightPanel, BorderLayout.SOUTH);

        // Set content and make the frame visible
        mainMenuFrame.setContentPane(this);
        mainMenuFrame.setVisible(true);
        mainMenuFrame.setLocationRelativeTo(null);  // Center the window
    }

    // Create and setup frame
    private JFrame createFrame(String title, String iconPath, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setUndecorated(true);  // Remove title bar and system buttons (close, maximize, etc.)
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIcon(frame, iconPath);
        return frame;
    }

    // Set icon for the frame
    private void setIcon(JFrame frame, String iconPath) {
        try {
            Image icon = ImageIO.read(new File(iconPath));
            frame.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Create and style menu buttons
    private JButton createStyledMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Cooper Black", Font.BOLD, 26));
        button.setFocusPainted(false); // Remove focus border
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Center alignment for consistency
        return button;
    }

    // Show the main menu frame
    public void show() {
        mainMenuFrame.setVisible(true);
    }
}
