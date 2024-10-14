import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

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

        // Edit Profile Button in Top-Right Corner with Icon
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.setOpaque(false); // Transparent background

        JButton editProfileButton = createIconButton("settingsButton.png");
        editProfileButton.addActionListener(e -> {
            // Logic for editing profile can be added here
            JOptionPane.showMessageDialog(frame, "Edit Profile button clicked!");
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
        button.setFont(new Font("Cooper Black", Font.BOLD, 16));
        return button;
    }

    // Create Icon Button Helper
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
