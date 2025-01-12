import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SelectWorkoutRoutine extends JPanel {
    private JFrame frame;
    private List<String> routines;
    private Image backgroundImage;

    public SelectWorkoutRoutine(JFrame existingFrame) {
        this.frame = existingFrame;
        routines = new ArrayList<>();
        loadBackgroundImage();
        loadRoutines();
        setupUI();

        frame.setContentPane(this);
        frame.revalidate();
        frame.repaint();
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(new File("background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void loadRoutines() {
        try (BufferedReader reader = new BufferedReader(new FileReader("workout_routines.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Routine Name:")) {
                    String routineName = line.replace("Routine Name:", "").trim();
                    routines.add(routineName);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to load workout routines.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Title Label
        JLabel titleLabel = new JLabel("Select Workout Routine", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Cooper Black", Font.PLAIN, 24));
        add(titleLabel, BorderLayout.NORTH);

        // List of Routines
        JList<String> routineList = new JList<>(routines.toArray(new String[0]));
        routineList.setFont(new Font("SansSerif", Font.PLAIN, 18));
        JScrollPane listScrollPane = new JScrollPane(routineList);
        add(listScrollPane, BorderLayout.CENTER);

        // Select Button
        JButton selectButton = new JButton("Select Workout Routine");
        selectButton.setFont(new Font("Cooper Black", Font.BOLD, 18));
        selectButton.addActionListener(e -> {
            String selectedRoutine = routineList.getSelectedValue();
            if (selectedRoutine != null) {
                saveSelectedRoutine(selectedRoutine);
                //new DisplayRoutineExercises(frame, selectedRoutine);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a routine.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        add(selectButton, BorderLayout.SOUTH);

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Cooper Black", Font.BOLD, 18));
        backButton.addActionListener(e -> {
            new TrackWorkout(frame);
        });
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(selectButton);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void saveSelectedRoutine(String routineName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("selected_routine.txt"))) {
            writer.write(routineName);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to save selected routine.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
