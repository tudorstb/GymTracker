import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class CreateAccountWindow {
    private JFrame createAccountFrame;
    private BackgroundPanel createAccountPanel;

    // Constructor
    public CreateAccountWindow(String backgroundImagePath) {
        createAccountFrame = createFrame("Create Account", "icon.png", 520, 270);
        createAccountPanel = new BackgroundPanel(backgroundImagePath);
        createAccountPanel.setLayout(new BorderLayout());

        setupUIComponents();

        createAccountFrame.setContentPane(createAccountPanel);
        createAccountFrame.setVisible(true);
    }

    // Setup UI Components
    private void setupUIComponents() {
        // Title Label (similar to the login label in Application class)
        JLabel titleLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Cooper Black", Font.BOLD, 24));
        createAccountPanel.add(titleLabel, BorderLayout.NORTH);

        // Center Panel for input fields
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false); // Transparent background to show main background image
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Consistent spacing

        // Username Input
        JTextField userField = createTextField(15);
        addLabelAndField(inputPanel, gbc, "Username:", userField, 0);

        // Password Input
        JPasswordField passwordField = createPasswordField(15);
        JButton togglePasswordButton = createToggleButton(passwordField);
        addLabelFieldAndButton(inputPanel, gbc, "Password:", passwordField, togglePasswordButton, 1);

        // Re-enter Password Input
        JPasswordField rePasswordField = createPasswordField(15);
        JButton toggleRePasswordButton = createToggleButton(rePasswordField);
        addLabelFieldAndButton(inputPanel, gbc, "Re-enter Password:", rePasswordField, toggleRePasswordButton, 2);

        // Add inputPanel to the center of the main panel
        createAccountPanel.add(inputPanel, BorderLayout.CENTER);

        // Create Button Panel (similar to how buttons are aligned in Application class)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton createButton = createButton("Create");
        buttonPanel.add(Box.createVerticalStrut(10)); // Add spacing above the button
        buttonPanel.add(createButton);

        createAccountPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    // Create and setup the frame
    private JFrame createFrame(String title, String iconPath, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIcon(frame, iconPath);
        return frame;
    }

    // Set the frame icon
    private void setIcon(JFrame frame, String iconPath) {
        try {
            Image icon = ImageIO.read(new File(iconPath));
            frame.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to add labels and fields
    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(createLabel(labelText, 16, SwingConstants.RIGHT), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    // Helper method to add labels, fields, and toggle buttons
    private void addLabelFieldAndButton(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, JButton button, int row) {
        addLabelAndField(panel, gbc, labelText, field, row);
        gbc.gridx = 2;
        panel.add(button, gbc);
    }

    // Create Label Helper
    private JLabel createLabel(String text, int fontSize, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(new Font("Cooper Black", Font.PLAIN, fontSize));
        return label;
    }

    // Create TextField Helper
    private JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Cooper Black", Font.PLAIN, 16));
        return textField;
    }

    // Create PasswordField Helper
    private JPasswordField createPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(new Font("Cooper Black", Font.PLAIN, 16));
        passwordField.setEchoChar('*');
        return passwordField;
    }

    // Create Toggle Button for Password Visibility
    private JButton createToggleButton(JPasswordField passwordField) {
        JButton toggleButton = createButton("Show");
        toggleButton.setFont(new Font("Cooper Black", Font.PLAIN, 16));
        toggleButton.addActionListener(e -> {
            boolean isVisible = passwordField.getEchoChar() == (char) 0;
            passwordField.setEchoChar(isVisible ? '*' : (char) 0);
            toggleButton.setText(isVisible ? "Show" : "Hide");
        });
        return toggleButton;
    }

    // Create Button Helper
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Cooper Black", Font.BOLD, 16));
        button.setBackground(new Color(70, 130, 180));
        return button;
    }
}
