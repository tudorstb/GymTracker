import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GUI {
    private JLabel user_label;
    private JLabel pass_label;
    private JTextField userInput;
    private JPasswordField passwordField;
    private JFrame frame;
    private BackgroundPanel panel;

    public GUI() {
        frame = new JFrame();

        // Load the icon image
        try {
            Image icon = ImageIO.read(new File("icon.png")); // Update the path to your icon image
            frame.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        panel = new BackgroundPanel(); // Use the custom panel for background image

        // Create a "Log in" label with "Cooper Black" font
        JLabel loginLabel = new JLabel("Log in", SwingConstants.CENTER);
        loginLabel.setFont(new Font("Cooper Black", Font.BOLD, 24)); // Change font to Cooper Black, bold, size 24

        // Create labels for user and password with "Cooper Black" font
        user_label = new JLabel("User: ", SwingConstants.RIGHT);
        user_label.setFont(new Font("Cooper Black", Font.PLAIN, 16)); // Change font to Cooper Black, plain, size 16

        pass_label = new JLabel("Password: ", SwingConstants.RIGHT);
        pass_label.setFont(new Font("Cooper Black", Font.PLAIN, 16)); // Change font to Cooper Black, plain, size 16

        // Create text field for user input with "Cooper Black" font
        userInput = new JTextField(10);  // A text field with 10 columns
        userInput.setFont(new Font("Cooper Black", Font.PLAIN, 16)); // Change font for the text field
        userInput.setForeground(Color.BLACK); // Set text color for user input to black

        // Create password field with "Cooper Black" font
        passwordField = new JPasswordField(10);
        passwordField.setFont(new Font("Cooper Black", Font.PLAIN, 16)); // Change font for the password field
        passwordField.setForeground(Color.BLACK); // Set text color for password input to black
        passwordField.setEchoChar('*');  // Set '*' as the placeholder character

        // Create a "Log in" button with "Cooper Black" font
        JButton loginButton = new JButton("Log in");
        loginButton.setFont(new Font("Cooper Black", Font.BOLD, 16)); // Change font for the button

        // Create a "Create Account" button
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setFont(new Font("Cooper Black", Font.BOLD, 16)); // Change font for the create account button
        createAccountButton.setBackground(new Color(70, 130, 180)); // Set a darker shade of blue for the button

        // Use BorderLayout for the main panel
        panel.setLayout(new BorderLayout());

        // Create a panel for the user and password input with GridBagLayout for centering
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false); // Make the input panel transparent
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding between components

        // Add user label and text field
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(user_label, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(userInput, gbc);

        // Add password label and password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(pass_label, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(passwordField, gbc);

        // Add the login button below the input fields with 50 pixels of space
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span the button across both columns
        gbc.insets = new Insets(50, 5, 5, 5); // Add 50 pixels of space above the button
        inputPanel.add(loginButton, gbc);

        // Add the input panel to the main panel
        panel.add(loginLabel, BorderLayout.NORTH); // Add login label at the top
        panel.add(inputPanel, BorderLayout.CENTER); // Add the user and password input panel at the center

        // Add the "Create Account" button to the bottom of the main panel
        panel.add(createAccountButton, BorderLayout.SOUTH); // Add create account button at the bottom

        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("GYM TRACKER");

        // Set the size to 340x590 pixels
        frame.setSize(340, 590);
        frame.setVisible(true);
    }

    // Custom panel to handle background image
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                // Load the background image
                backgroundImage = ImageIO.read(new File("background.jpg")); // Ensure this is the correct path to your image
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the background image
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        new GUI();
    }
}
