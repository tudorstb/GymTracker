import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class Application extends JPanel {
    private Image backgroundImage;
    private JFrame frame;
    private Connection connection;

    public class UsernameMissingException extends Exception {
        public UsernameMissingException(String message) {
            super(message);
        }
    }

    public class PasswordMissingException extends Exception {
        public PasswordMissingException(String message) {
            super(message);
        }
    }

    public Application() {
        try {
            backgroundImage = ImageIO.read(new File("background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame = createFrame("GYM TRACKER", "icon.png", 880, 590);
        frame.setContentPane(this);

        connection = DatabaseConnection.getConnection(); // Use the external connection class
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private JFrame createFrame(String title, String iconPath, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            Image icon = ImageIO.read(new File(iconPath));
            frame.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return frame;
    }


    public void run() {
        createUIComponents();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private void createUIComponents() {
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel loginLabel = createLabel("Log in", 24, SwingConstants.CENTER);
        titlePanel.add(loginLabel, BorderLayout.CENTER);

        JButton exitButton = createButton("Exit");
        exitButton.setBackground(new Color(178, 34, 34));
        exitButton.addActionListener(e -> System.exit(0));
        titlePanel.add(exitButton, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField userInput = createTextField(10);
        JPasswordField passwordField = createPasswordField(10);

        addLabelAndField(inputPanel, gbc, "User:", userInput, 0);
        addLabelAndField(inputPanel, gbc, "Password:", passwordField, 1);

        JButton loginButton = createButton("Log in");
        loginButton.addActionListener(e -> {
            String username = userInput.getText();
            String password = new String(passwordField.getPassword());

            try {
                if (username.isEmpty()) {
                    throw new UsernameMissingException("Username is missing.");
                }
                if (password.isEmpty()) {
                    throw new PasswordMissingException("Password is missing.");
                }
                // Log in admin

                if (username.equals("admin") && password.equals("admin")) {
                    new Admin();
                    frame.dispose();}
                else if (authenticateUser(username, password)) {
                    saveUsernameToFile(username);
                    new MainMenuWindow();
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (UsernameMissingException | PasswordMissingException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        inputPanel.add(loginButton, gbc);

        add(inputPanel, BorderLayout.CENTER);

        JButton createAccountButton = createButton("Create Account");
        createAccountButton.setBackground(new Color(70, 130, 180));
        createAccountButton.addActionListener(e -> new CreateAccountWindow("BackgroundImageForLogIn.png"));
        add(createAccountButton, BorderLayout.SOUTH);
    }

    private boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveUsernameToFile(String username) {
        File file = new File("name.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JLabel createLabel(String text, int fontSize, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(new Font("Cooper Black", Font.PLAIN, fontSize));
        return label;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Cooper Black", Font.BOLD, 21));
        return button;
    }

    private JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(new Font("Cooper Black", Font.PLAIN, 21));
        return textField;
    }

    private JPasswordField createPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(new Font("Cooper Black", Font.PLAIN, 21));
        passwordField.setEchoChar('*');
        return passwordField;
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(createLabel(labelText, 21, SwingConstants.RIGHT), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
}
