import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CreateWorkoutRoutine extends JPanel {
    private JFrame frame;
    private List<String> selectedExercises;
    private JPanel exercisesPanel;
    private JTextField routineNameField;
    private static final String FILE_NAME = "workout_routines.txt";
    private Image backgroundImage;
    private Connection connection;

    public CreateWorkoutRoutine(JFrame existingFrame) {
        this.frame = existingFrame;
        this.selectedExercises = new ArrayList<>();
        this.connection = DatabaseConnection.getConnection(); // Open connection
        loadBackgroundImage();
        setupUI();

        frame.setContentPane(this);
        frame.revalidate();
        frame.repaint();
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close(); // Explicitly close connection
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private void setupUI() {
        setLayout(new BorderLayout());
        setOpaque(false); // Ensure transparency for background image

        // Title
        JLabel titleLabel = new JLabel("Create New Workout Routine", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Cooper Black", Font.PLAIN, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Routine Name Input
        JPanel namePanel = new JPanel();
        namePanel.setOpaque(false);
        JLabel nameLabel = new JLabel("Routine Name: ");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        routineNameField = new JTextField(20);
        routineNameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        namePanel.add(nameLabel);
        namePanel.add(routineNameField);
        add(namePanel, BorderLayout.NORTH);

        // Panel to display selected exercises
        exercisesPanel = new JPanel();
        exercisesPanel.setOpaque(false);
        exercisesPanel.setLayout(new BoxLayout(exercisesPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(exercisesPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        // Button to add new exercise
        JButton addExerciseButton = new JButton("+");
        addExerciseButton.setFont(new Font("Cooper Black", Font.BOLD, 20));
        addExerciseButton.addActionListener(e -> openExerciseSearchDialog());
        exercisesPanel.add(addExerciseButton);

        // Save button
        JButton saveButton = new JButton("Save Routine");
        saveButton.setFont(new Font("Cooper Black", Font.BOLD, 20));
        saveButton.addActionListener(e -> saveRoutine());
        add(saveButton, BorderLayout.SOUTH);
    }

    private void openExerciseSearchDialog() {
        JDialog searchDialog = new JDialog(frame, "Search Exercises", true);
        searchDialog.setSize(400, 300);
        searchDialog.setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Cooper Black", Font.BOLD, 14));
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        searchDialog.add(searchPanel, BorderLayout.NORTH);

        DefaultListModel<String> exerciseListModel = new DefaultListModel<>();
        JList<String> exerciseList = new JList<>(exerciseListModel);
        exerciseList.setFont(new Font("SansSerif", Font.PLAIN, 16));
        exerciseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(exerciseList);
        searchDialog.add(listScrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            exerciseListModel.clear();

            try (PreparedStatement statement = connection.prepareStatement("SELECT name FROM exercises WHERE name ILIKE ?")) {
                statement.setString(1, "%" + searchText + "%");
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    exerciseListModel.addElement(resultSet.getString("name"));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(searchDialog, "Failed to search exercises.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton selectButton = new JButton("Select");
        selectButton.setFont(new Font("Cooper Black", Font.BOLD, 16));
        selectButton.addActionListener(e -> {
            String selectedExercise = exerciseList.getSelectedValue();
            if (selectedExercise != null) {
                addExerciseToRoutine(selectedExercise);
                searchDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(searchDialog, "Please select an exercise.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        searchDialog.add(selectButton, BorderLayout.SOUTH);

        searchDialog.setLocationRelativeTo(frame);
        searchDialog.setVisible(true);
    }

    private void addExerciseToRoutine(String exercise) {
        selectedExercises.add(exercise);

        Component addButton = exercisesPanel.getComponent(exercisesPanel.getComponentCount() - 1);
        exercisesPanel.remove(addButton);

        JLabel exerciseLabel = new JLabel(exercise);
        exerciseLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        exercisesPanel.add(exerciseLabel);

        exercisesPanel.add(addButton);

        exercisesPanel.revalidate();
        exercisesPanel.repaint();
    }

    private void saveRoutine() {
        String routineName = routineNameField.getText().trim();
        if (routineName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a routine name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedExercises.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please add at least one exercise.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!new File(FILE_NAME).exists()) {
            try {
                new File(FILE_NAME).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to create file for saving routines.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Check for duplicate routine name
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Routine Name: ") && line.substring(14).equalsIgnoreCase(routineName)) {
                    JOptionPane.showMessageDialog(frame, "A routine with this name already exists. Please choose a different name.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write("Routine Name: " + routineName + "\n");
            for (String exercise : selectedExercises) {
                writer.write(exercise + "\n");
            }
            writer.write("---\n");

            JOptionPane.showMessageDialog(frame, "Routine saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Go back to Track Workouts screen
            new TrackWorkout(frame);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to save routine.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}