import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class CreateAccountWindow {

    public CreateAccountWindow() {
        // Create a new frame for the "Create Account" window
        JFrame createAccountFrame = new JFrame("Create Account");

        // Set the icon image for the frame
        try {
            Image icon = ImageIO.read(new File("icon.png")); // Load the icon image (make sure it's in the correct path)
            createAccountFrame.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        createAccountFrame.setSize(400, 400);
        createAccountFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close, won't affect main window

        // Use the BackgroundPanel class for setting the background
        BackgroundPanel createAccountPanel = new BackgroundPanel();
        createAccountPanel.setLayout(new GridBagLayout()); // Set layout for positioning components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Create and add components for account creation
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);

        JLabel rePassLabel = new JLabel("Re-enter Password:");
        JPasswordField rePasswordField = new JPasswordField(15);

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
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        createAccountPanel.add(createButton, gbc);

        // Add the BackgroundPanel to the frame
        createAccountFrame.setContentPane(createAccountPanel);
        createAccountFrame.setVisible(true);
    }
}
