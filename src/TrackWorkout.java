import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.FileReader;


public class TrackWorkout extends JPanel {
    private Image backgroundImage;
    private JFrame frame;
    private String username;

    // Constructor: accepts an existing JFrame to modify
    public TrackWorkout(JFrame existingFrame) {
        this.frame = existingFrame;

        try {
            backgroundImage = ImageIO.read(new File("background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadUserData();
        createUIComponents();

        frame.setContentPane(this); // Set new content
        frame.revalidate(); // Refresh the frame
        frame.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // Load username from name.txt
    private void loadUserData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("name.txt"))) {
            username = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to load user data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Create UI Components
    private void createUIComponents() {
        setLayout(new BorderLayout());

        // Title Label
        JLabel trackWorkoutLabel = createLabel("Track Workout", 24, SwingConstants.CENTER);
        add(trackWorkoutLabel, BorderLayout.NORTH);

        // Placeholder Panel for Tracking Workouts
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add UI elements here
        JLabel placeholderLabel = createLabel("Feature to log workouts will be implemented here.", 21, SwingConstants.LEFT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(placeholderLabel, gbc);

        add(infoPanel, BorderLayout.CENTER);

        // Back Button
        JButton backButton = createButton("Back");
        backButton.addActionListener(e -> {
            MainMenuWindow mainMenu = new MainMenuWindow(frame); // Go back to Main Menu
            mainMenu.show();
        });
        add(backButton, BorderLayout.SOUTH);
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
        button.setFont(new Font("Cooper Black", Font.BOLD, 21));
        button.setForeground(Color.BLACK); // Set button text color to black
        return button;
    }
}
