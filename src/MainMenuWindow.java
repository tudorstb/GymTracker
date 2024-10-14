import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainMenuWindow {
    private JFrame mainMenuFrame;

    public MainMenuWindow() {
        // Create the main menu frame
        mainMenuFrame = new JFrame("Main Menu");
        mainMenuFrame.setSize(400, 400);
        mainMenuFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close this window

        // Use BackgroundPanel with "pika.png" as background
        BackgroundPanel mainMenuPanel = new BackgroundPanel("background.jpg");
        mainMenuPanel.setLayout(new BoxLayout(mainMenuPanel, BoxLayout.Y_AXIS));

        // Create buttons for the main menu
        JButton viewProfileButton = new JButton("View Profile");
        JButton trackWorkoutButton = new JButton("Track Workout");
        JButton logOutButton = new JButton("Log Out");

        try {
            Image icon = ImageIO.read(new File("icon.png")); // Load the icon image (make sure it's in the correct path)
            mainMenuFrame.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add action to log out button
        logOutButton.addActionListener(e -> {
            // Close the main menu window
            mainMenuFrame.dispose();
            new Application().run(); // Reopen the login screen
        });

        // Add some spacing between the buttons and add them to the panel
        mainMenuPanel.add(Box.createVerticalStrut(20));
        mainMenuPanel.add(viewProfileButton);
        mainMenuPanel.add(Box.createVerticalStrut(20));
        mainMenuPanel.add(trackWorkoutButton);
        mainMenuPanel.add(Box.createVerticalStrut(20));
        mainMenuPanel.add(logOutButton);

        // Add the BackgroundPanel to the frame
        mainMenuFrame.setContentPane(mainMenuPanel);

        // Make the main menu frame visible
        mainMenuFrame.setVisible(true);
    }
}
