import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.imageio.ImageIO;

class Application {
    private JLabel userLabel;
    private JLabel passLabel;
    private JTextField userInput;
    private JPasswordField passwordField;
    private JFrame frame;
    private BackgroundPanel panel;

    public Application() {
        frame = new JFrame();

        try {
            Image icon = ImageIO.read(new File("icon.png")); // Update the path to your icon image
            frame.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Database
        String sgl = "select name from product where id=8";
        String url = "jdbc:postgresql://localhost:3306/fitDataBase";
        String username = "postgres";
        String password = "1234";

        try {
            Connection con = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            System.out.println(e);
        }

        panel = new BackgroundPanel(); // Use the custom panel for background image
        frame.setContentPane(panel); // Set the content pane to the BackgroundPanel
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("GYM TRACKER");
        frame.setSize(340, 590);
        frame.setVisible(true);
    }

    public void run() {
        createUIComponents(); // Create and add UI components
    }

    private void createUIComponents() {
        // Create a "Log in" label with "Cooper Black" font
        JLabel loginLabel = new JLabel("Log in", SwingConstants.CENTER);
        loginLabel.setFont(new Font("Cooper Black", Font.BOLD, 24));

        // Create labels for user and password with "Cooper Black" font
        userLabel = new JLabel("User: ", SwingConstants.RIGHT);
        userLabel.setFont(new Font("Cooper Black", Font.PLAIN, 16));

        passLabel = new JLabel("Password: ", SwingConstants.RIGHT);
        passLabel.setFont(new Font("Cooper Black", Font.PLAIN, 16));

        // Create text field for user input
        userInput = new JTextField(10);
        userInput.setFont(new Font("Cooper Black", Font.PLAIN, 16));

        // Create password field
        passwordField = new JPasswordField(10);
        passwordField.setFont(new Font("Cooper Black", Font.PLAIN, 16));
        passwordField.setEchoChar('*');

        // Create "Log in" button
        JButton loginButton = new JButton("Log in");
        loginButton.setFont(new Font("Cooper Black", Font.BOLD, 16));

        // Create "Create Account" button
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setFont(new Font("Cooper Black", Font.BOLD, 16));
        createAccountButton.setBackground(new Color(70, 130, 180));

        // Add ActionListener to open a new window on button click
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCreateAccountWindow(); // Call the method to open new window
            }
        });

        // Use BorderLayout for the main panel
        panel.setLayout(new BorderLayout());

        // Create a panel for the user and password input
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false); // Transparent panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add user label and text field
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(userInput, gbc);

        // Add password label and password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(passwordField, gbc);

        // Add the login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 5, 5, 5);
        inputPanel.add(loginButton, gbc);

        // Add components to the main panel
        panel.add(loginLabel, BorderLayout.NORTH); // Add login label at the top
        panel.add(inputPanel, BorderLayout.CENTER); // Add input panel at the center
        panel.add(createAccountButton, BorderLayout.SOUTH); // Add create account button at the bottom

        frame.revalidate(); // Refresh the frame
    }

    // Method to open a new window for "Create Account"
    private void openCreateAccountWindow() {
        // Create a new frame for the account creation window
        JFrame createAccountFrame = new JFrame("Create Account");
        createAccountFrame.setSize(400, 400);
        createAccountFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close, won't affect main window

        // Create a panel for user, password, and re-enter password fields
        JPanel createAccountPanel = new JPanel(new GridBagLayout());
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

        // Add the panel to the new frame
        createAccountFrame.add(createAccountPanel);
        createAccountFrame.setVisible(true);
    }

    public static void main(String[] args) {
        Application app = new Application();
        app.run();
    }
}
