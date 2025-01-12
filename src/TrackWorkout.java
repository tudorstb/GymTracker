import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TrackWorkout extends JPanel {
    private Image backgroundImage;
    private JFrame frame;

    public TrackWorkout(JFrame existingFrame) {
        this.frame = existingFrame;

        try {
            backgroundImage = ImageIO.read(new File("background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    private void createUIComponents() {
        setLayout(new BorderLayout());

        // Title Label
        JLabel titleLabel = createLabel("Track Workout", 24, SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create New Workout Routine Button
        JButton createRoutineButton = createButton("Create New Workout Routine");
        createRoutineButton.addActionListener(e -> {
            new CreateWorkoutRoutine(frame);
        });

        // Select Workout Routine Button
        JButton selectRoutineButton = createButton("Select Workout Routine");
        selectRoutineButton.addActionListener(e -> {
            new SelectWorkoutRoutine(frame);
        });

        // Start Custom Workout Button
        JButton startCustomWorkoutButton = createButton("Start Custom Workout");
        startCustomWorkoutButton.addActionListener(e -> {
           // new StartCustomWorkout(frame);
        });

        // Add Buttons to Panel
        gbc.gridy = 0;
        buttonPanel.add(createRoutineButton, gbc);
        gbc.gridy = 1;
        buttonPanel.add(selectRoutineButton, gbc);
        gbc.gridy = 2;
        buttonPanel.add(startCustomWorkoutButton, gbc);

        add(buttonPanel, BorderLayout.CENTER);

        // Back Button
        JButton backButton = createButton("Back");
        backButton.addActionListener(e -> {
            MainMenuWindow mainMenu = new MainMenuWindow(frame);
            mainMenu.show();
        });
        add(backButton, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text, int fontSize, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(new Font("Cooper Black", Font.PLAIN, fontSize));
        return label;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Cooper Black", Font.BOLD, 21));
        button.setForeground(Color.BLACK);
        return button;
    }
}
