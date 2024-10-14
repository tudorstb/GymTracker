import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainMenuWindow {
    private JFrame mainMenuFrame;

    public MainMenuWindow(JFrame existingFrame) {
        this.mainMenuFrame = existingFrame;
        setupUI();
    }

    // To initialize from scratch or when returning from another screen
    public MainMenuWindow() {
        this.mainMenuFrame = createFrame("Main Menu", "icon.png", 400, 400);
        setupUI();
    }

    // Set up the UI components
    private void setupUI() {
        BackgroundPanel mainMenuPanel = new BackgroundPanel("background.jpg");
        mainMenuPanel.setLayout(new BorderLayout());

        // Create Buttons with Style Matching the Log In Button
        JButton viewProfileButton = createStyledLoginButton("View Profile");
        JButton trackWorkoutButton = createStyledLoginButton("Track Workout");
        JButton logOutButton = createStyledLoginButton("Log Out");

        // Action for View Profile Button
        viewProfileButton.addActionListener(e -> {
            new Profile(mainMenuFrame); // Modify the existing frame for the Profile screen
        });

        // Action for Log Out Button
        logOutButton.addActionListener(e -> {
            mainMenuFrame.dispose();
            new Application().run();
        });

        // Add Buttons to Panel using GridBagLayout for Consistency
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
        buttonPanel.add(trackWorkoutButton, gbc);
        gbc.gridy = 2;
        buttonPanel.add(logOutButton, gbc);

        mainMenuPanel.add(buttonPanel, BorderLayout.CENTER);

        mainMenuFrame.setContentPane(mainMenuPanel);
        mainMenuFrame.setVisible(true);
    }

    private JFrame createFrame(String title, String iconPath, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
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

    private JButton createStyledLoginButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Cooper Black", Font.BOLD, 16));
        button.setFocusPainted(false); // Remove focus border
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Center alignment for consistency
        return button;
    }

    public void show() {
    }
}