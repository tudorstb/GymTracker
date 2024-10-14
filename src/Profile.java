import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Profile {
    private JFrame frame;
    private BackgroundPanel backgroundPanel;

    // Constructor
    public Profile() {
        frame = createFrame("Profile", "icon.png", 340, 590);
        backgroundPanel = new BackgroundPanel("profile.jpg");
        frame.setContentPane(backgroundPanel);
        createUIComponents();
    }

    // Run the Profile window
    public void show() {
        frame.setVisible(true);
    }

    // Create and setup the frame
    private JFrame createFrame(String title, String iconPath, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only the profile window
        setIcon(frame, iconPath);
        return frame;
    }

    // Set the application icon
    private void setIcon(JFrame frame, String iconPath) {
        try {
            Image icon = ImageIO.read(new File(iconPath));
            frame.setIconImage(icon);
        } catch (IOException e) {
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

        // Profile information (example: Name, Email, etc.)
        JLabel nameLabel = createLabel("Name: John Doe", 16, SwingConstants.LEFT);
        JLabel emailLabel = createLabel("Email: john@example.com", 16, SwingConstants.LEFT);

        // Add profile information to the info panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(nameLabel, gbc);

        gbc.gridy = 1;
        infoPanel.add(emailLabel, gbc);

        backgroundPanel.add(infoPanel, BorderLayout.CENTER);

        // Back Button
        JButton backButton = createButton("Back");
        backButton.addActionListener(e -> frame.dispose()); // Close the profile window
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
        button.setFont(new Font("Cooper Black", Font.BOLD, 16));
        return button;
    }
}
