import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        createAccountFrame.setSize(520, 270);
        createAccountFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close, won't affect main window

        // Use the BackgroundPanel class with the specified image path
        BackgroundPanel createAccountPanel = new BackgroundPanel(backgroundImagePath);
        createAccountPanel.setLayout(new GridBagLayout()); // Set layout for positioning components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5); // Reduced space between elements

        // Title label
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Cooper Black", Font.BOLD, 20)); // Set title font and style
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3; // Span all columns
        gbc.anchor = GridBagConstraints.CENTER; // Center the title horizontally
        gbc.weightx = 1.0; // Allow the title to take up horizontal space
        createAccountPanel.add(titleLabel, gbc);

        // Reset grid width for next components
        gbc.gridwidth = 1; // Reset grid width for input fields
        gbc.weighty = 1; // Allow the component to grow vertically
        gbc.gridy = 1; // Start positioning from row 1

        // Create and add components for account creation with consistent font
        JLabel userLabel = createLabel("Username:");
        JTextField userField = createTextField(15);

        JLabel passLabel = createLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(userField.getFont()); // Match the font with userField

        // Create a button to toggle password visibility
        JButton togglePasswordButton = createToggleButton(passwordField);

        JLabel rePassLabel = createLabel("Re-enter Password:");
        JPasswordField rePasswordField = new JPasswordField(15);
        rePasswordField.setFont(userField.getFont()); // Match the font with userField

        // Create a button to toggle re-enter password visibility
        JButton toggleRePasswordButton = createToggleButton(rePasswordField);

        // Add components to the create account panel
        gbc.gridy = 1; // Position for username
        createAccountPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        createAccountPanel.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2; // Move down for password
        createAccountPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        createAccountPanel.add(passwordField, gbc);
        gbc.gridx = 2; // Position the toggle button next to the password field
        createAccountPanel.add(togglePasswordButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3; // Move down for re-enter password
        createAccountPanel.add(rePassLabel, gbc);
        gbc.gridx = 1;
        createAccountPanel.add(rePasswordField, gbc);
        gbc.gridx = 2; // Position the toggle button next to the re-enter password field
        createAccountPanel.add(toggleRePasswordButton, gbc);

        // Add a button to submit account creation with new text
        JButton createButton = new JButton("Create");
        createButton.setFont(userLabel.getFont()); // Match the font with userLabel
        gbc.gridx = 0;
        gbc.gridy = 4; // Move down one row for the create button
        gbc.gridwidth = 3; // Make the create button span all columns
        createButton.setHorizontalAlignment(SwingConstants.CENTER);
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

    // Helper method to create a toggle button for password visibility
    private JButton createToggleButton(JPasswordField passwordField) {
        JButton toggleButton = new JButton("Show");
        toggleButton.setFont(new Font("Cooper Black", Font.PLAIN, 16)); // Set font style
        toggleButton.addActionListener(new ActionListener() {
            private boolean isVisible = false; // Track visibility state

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isVisible) {
                    passwordField.setEchoChar('*'); // Set back to asterisk
                    toggleButton.setText("Show"); // Change button text to "Show"
                } else {
                    passwordField.setEchoChar((char) 0); // Show actual characters
                    toggleButton.setText("Hide"); // Change button text to "Hide"
                }
                isVisible = !isVisible; // Toggle visibility state
            }
        });
        return toggleButton;
    }
}
