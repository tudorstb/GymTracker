import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.imageio.ImageIO;

class Application {
    private JFrame frame;
    private BackgroundPanel panel;

    // Constructor
    public Application() {
        frame = createFrame("GYM TRACKER", "icon.png", 340, 590);
        panel = new BackgroundPanel("background.jpg");
        frame.setContentPane(panel);
    }

    // Run the application
    public void run() {
        createUIComponents();
        frame.setVisible(true);
    }

    // Create and setup the frame
    private JFrame createFrame(String title, String iconPath, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        panel.setLayout(new BorderLayout());

        // Title Label
        JLabel loginLabel = createLabel("Log in", 24, SwingConstants.CENTER);
        panel.add(loginLabel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // User Input
        JTextField userInput = createTextField(10);
        JPasswordField passwordField = createPasswordField(10);

        // Add User and Password Inputs
        addLabelAndField(inputPanel, gbc, "User:", userInput, 0);
        addLabelAndField(inputPanel, gbc, "Password:", passwordField, 1);

        // Log In Button
        JButton loginButton = createButton("Log in");
        loginButton.addActionListener(e -> {
            new MainMenuWindow();
            frame.dispose();
        });

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        inputPanel.add(loginButton, gbc);

        panel.add(inputPanel, BorderLayout.CENTER);

        // Create Account Button
        JButton createAccountButton = createButton("Create Account");
        createAccountButton.setBackground(new Color(70, 130, 180));
        createAccountButton.addActionListener(e -> new CreateAccountWindow("BackgroundImageForLogIn.jpg"));
        panel.add(createAccountButton, BorderLayout.SOUTH);
    }

    // Helper to add labels and text fields
    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(createLabel(labelText, 16, SwingConstants.RIGHT), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
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
}
