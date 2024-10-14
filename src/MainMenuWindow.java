import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainMenuWindow {
    private JFrame mainMenuFrame;

    public MainMenuWindow() {
        mainMenuFrame = createFrame("Main Menu", "icon.png", 400, 400);

        BackgroundPanel mainMenuPanel = new BackgroundPanel("background.jpg");
        mainMenuPanel.setLayout(new BoxLayout(mainMenuPanel, BoxLayout.Y_AXIS));

        // Buttons
        JButton viewProfileButton = createButton("View Profile");
        JButton trackWorkoutButton = createButton("Track Workout");
        JButton logOutButton = createButton("Log Out");

        logOutButton.addActionListener(e -> {
            mainMenuFrame.dispose();
            new Application().run();
        });

        // Add Buttons with Spacing
        addButtonWithSpacing(mainMenuPanel, viewProfileButton);
        addButtonWithSpacing(mainMenuPanel, trackWorkoutButton);
        addButtonWithSpacing(mainMenuPanel, logOutButton);

        mainMenuFrame.setContentPane(mainMenuPanel);
        mainMenuFrame.setVisible(true);
    }

    private JFrame createFrame(String title, String iconPath, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIcon(frame, iconPath);
        return frame;
    }

    private void setIcon(JFrame frame, String iconPath) {
        try {
            Image icon = ImageIO.read(new File(iconPath));
            frame.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Cooper Black", Font.BOLD, 16));
        return button;
    }

    private void addButtonWithSpacing(JPanel panel, JButton button) {
        panel.add(Box.createVerticalStrut(20));
        panel.add(button);
    }
}
