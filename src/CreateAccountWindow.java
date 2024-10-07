import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class CreateAccountWindow {

    public CreateAccountWindow(String backgroundImagePath) {
        // Create a new frame for the "Create Account" window
        JFrame createAccountFrame = new JFrame("Create Account");

        // Set the icon image for the frame
        try {
            Image icon = ImageIO.read(new File("icon.png")); // Load the icon image (make sure it's in the correct path)
            createAccountFrame.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        createAccountFrame.setSize(450, 650);
        createAccountFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close, won't affect main window

        // Use the BackgroundPanel class with the specified image path
        BackgroundPanel createAccountPanel = new BackgroundPanel(backgroundImagePath);
        createAccountPanel.setLayout(new GridBagLayout()); // Set layout for positioning components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Create and add components for account creation with consistent font
        JLabel userLabel = createLabel("Username:");
        JTextField userField = createTextField(15);

        JLabel passLabel = createLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(userField.getFont()); // Match the font with userField

        JLabel rePassLabel = createLabel("Re-enter Password:");
        JPasswordField rePasswordField = new JPasswordField(15);
        rePasswordField.setFont(userField.getFont()); // Match the font with userField

        // Add components to the create account panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        createAccountPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        createAccountPanel.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        createAccountPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        createAccountPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        createAccountPanel.add(rePassLabel, gbc);
        gbc.gridx = 1;
        createAccountPanel.add(rePasswordField, gbc);

        // Add a button to submit account creation
        JButton createButton = new JButton("Create Account");
        createButton.setFont(userLabel.getFont()); // Match the font with userLabel
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        createAccountPanel.add(createButton, gbc);

        // Add the BackgroundPanel to the frame
        createAccountFrame.setContentPane(createAccountPanel);
        createAccountFrame.setVisible(true);
    }

    // Helper method to create JLabel with the desired font
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Cooper Black", Font.PLAIN, 16)); // Use the same font as in other panels
        return label;
    }

    // Helper method to create JTextField with the desired font
    private JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Cooper Black", Font.PLAIN, 16)); // Use the same font as in other panels
        return textField;
    }
}
