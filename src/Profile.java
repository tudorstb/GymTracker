import javax.swing.*;
import java.awt.*;

public class Profile {
    private JFrame frame;
    private BackgroundPanel backgroundPanel;

    // Constructor: accepts an existing JFrame to modify
    public Profile(JFrame existingFrame) {
        this.frame = existingFrame; // Reuse the existing frame
        this.backgroundPanel = new BackgroundPanel("background.jpg");
        createUIComponents();
        frame.setContentPane(backgroundPanel); // Set new content
        frame.revalidate(); // Refresh the frame
        frame.repaint();
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
        button.setFont(new Font("Cooper Black", Font.BOLD, 16));
        return button;
    }
}
