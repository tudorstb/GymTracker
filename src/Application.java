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

        // Database connection (example, update it accordingly)
        String url = "jdbc:postgresql://localhost:3306/fitDataBase";
        String username = "postgres";
        String password = "1234";

        try {
            Connection con = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            System.out.println(e);
        }

        // Pass the path of the background image
        panel = new BackgroundPanel("background.jpg"); // Specify your background image path here
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

        // Create labels for user and password
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

        // Add ActionListener to open the "Create Account" window
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the new "Create Account" window with a specific background image
                new CreateAccountWindow("BackgroundImageForLogIn.jpg"); // Specify your background image path here
            }
        });

        // Use BorderLayout for the main panel
        panel.setLayout(new BorderLayout());

        // Create a panel for user and password input
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

}
