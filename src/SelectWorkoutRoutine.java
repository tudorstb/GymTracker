import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SelectWorkoutRoutine extends JPanel {
    private JFrame frame;
    private List<String> routines;
    private Image backgroundImage;
    private Connection connection;

    public SelectWorkoutRoutine(JFrame existingFrame) {
        this.frame = existingFrame;
        this.routines = new ArrayList<>();
        this.connection = DatabaseConnection.getConnection(); // Establish database connection
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
        String username;
        // Load the logged-in user's username from a local file
        try (BufferedReader reader = new BufferedReader(new FileReader("name.txt"))) {
            username = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to load username.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Fetch routines for the current user from the database
        String query = "SELECT routine_name FROM routines WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                routines.add(resultSet.getString("routine_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to load workout routines from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Title Label
        JLabel titleLabel = new JLabel("Select Workout Routine", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Cooper Black", Font.PLAIN, 24));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        // List of Routines
        JList<String> routineList = new JList<>(routines.toArray(new String[0]));
        routineList.setFont(new Font("SansSerif", Font.PLAIN, 18));
        routineList.setBackground(new Color(255, 255, 255, 200));
        JScrollPane listScrollPane = new JScrollPane(routineList);
        listScrollPane.setOpaque(false);
        listScrollPane.getViewport().setOpaque(false);
        add(listScrollPane, BorderLayout.CENTER);

        // Select Button
        JButton selectButton = new JButton("Select Workout Routine");
        selectButton.setFont(new Font("Cooper Black", Font.BOLD, 18));
        selectButton.addActionListener(e -> {
            String selectedRoutine = routineList.getSelectedValue();
            if (selectedRoutine != null) {
                saveSelectedRoutine(selectedRoutine);
                new DisplayRoutineExercises(frame, selectedRoutine);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a routine.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Cooper Black", Font.BOLD, 18));
        backButton.addActionListener(e -> {
            new TrackWorkout(frame);
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
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
