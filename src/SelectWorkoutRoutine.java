//import javax.swing.*;
//import java.awt.*;
//import java.io.*;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//import javax.swing.table.DefaultTableModel;
//
//public class CreateWorkoutRoutine extends JPanel {
//    private JFrame frame;
//    private List<String> selectedExercises;
//    private JPanel exercisesPanel;
//    private static final String FILE_NAME = "workout_routines.txt";
//
//    public CreateWorkoutRoutine(JFrame existingFrame) {
//        this.frame = existingFrame;
//        this.selectedExercises = new ArrayList<>();
//
//        setupUI();
//
//        frame.setContentPane(this);
//        frame.revalidate();
//        frame.repaint();
//    }
//
//    private void setupUI() {
//        setLayout(new BorderLayout());
//
//        // Title
//        JLabel titleLabel = new JLabel("Create New Workout Routine", SwingConstants.CENTER);
//        titleLabel.setFont(new Font("Cooper Black", Font.PLAIN, 24));
//        add(titleLabel, BorderLayout.NORTH);
//
//        // Panel to display selected exercises
//        exercisesPanel = new JPanel();
//        exercisesPanel.setLayout(new BoxLayout(exercisesPanel, BoxLayout.Y_AXIS));
//        JScrollPane scrollPane = new JScrollPane(exercisesPanel);
//        add(scrollPane, BorderLayout.CENTER);
//
//        // Button to add new exercise
//        JButton addExerciseButton = new JButton("+");
//        addExerciseButton.setFont(new Font("Cooper Black", Font.BOLD, 20));
//        addExerciseButton.addActionListener(e -> openExerciseSearchDialog());
//        exercisesPanel.add(addExerciseButton);
//
//        // Save button
//        JButton saveButton = new JButton("Save Routine");
//        saveButton.setFont(new Font("Cooper Black", Font.BOLD, 20));
//        saveButton.addActionListener(e -> saveRoutine());
//        add(saveButton, BorderLayout.SOUTH);
//    }
//
//    private void openExerciseSearchDialog() {
//        // Dialog for searching exercises
//        JDialog searchDialog = new JDialog(frame, "Search Exercises", true);
//        searchDialog.setSize(400, 300);
//        searchDialog.setLayout(new BorderLayout());
//
//        // Search bar
//        JTextField searchField = new JTextField();
//        searchField.setFont(new Font("SansSerif", Font.PLAIN, 16));
//        searchDialog.add(searchField, BorderLayout.NORTH);
//
//        // List of exercises from the database
//        DefaultListModel<String> exerciseListModel = new DefaultListModel<>();
//        try (Connection connection = DatabaseConnection.getConnection();
//             PreparedStatement statement = connection.prepareStatement("SELECT name FROM exercises");
//             ResultSet resultSet = statement.executeQuery()) {
//
//            while (resultSet.next()) {
//                exerciseListModel.addElement(resultSet.getString("name"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(searchDialog, "Failed to load exercises from the database.", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        JList<String> exerciseList = new JList<>(exerciseListModel);
//        exerciseList.setFont(new Font("SansSerif", Font.PLAIN, 16));
//        exerciseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        JScrollPane listScrollPane = new JScrollPane(exerciseList);
//        searchDialog.add(listScrollPane, BorderLayout.CENTER);
//
//        // Select button
//        JButton selectButton = new JButton("Select");
//        selectButton.setFont(new Font("Cooper Black", Font.BOLD, 16));
//        selectButton.addActionListener(e -> {
//            String selectedExercise = exerciseList.getSelectedValue();
//            if (selectedExercise != null) {
//                addExerciseToRoutine(selectedExercise);
//                searchDialog.dispose();
//            } else {
//                JOptionPane.showMessageDialog(searchDialog, "Please select an exercise.", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//        searchDialog.add(selectButton, BorderLayout.SOUTH);
//
//        searchDialog.setLocationRelativeTo(frame);
//        searchDialog.setVisible(true);
//    }
//
//    private void addExerciseToRoutine(String exercise) {
//        selectedExercises.add(exercise);
//
//        // Remove existing add button
//        Component addButton = exercisesPanel.getComponent(exercisesPanel.getComponentCount() - 1);
//        exercisesPanel.remove(addButton);
//
//        // Add selected exercise to the panel
//        JPanel exerciseRow = createExerciseRow(exercise);
//        exercisesPanel.add(exerciseRow);
//
//        // Re-add the add button
//        exercisesPanel.add(addButton);
//
//        // Refresh panel
//        exercisesPanel.revalidate();
//        exercisesPanel.repaint();
//    }
//
//    private JPanel createExerciseRow(String exercise) {
//        JPanel rowPanel = new JPanel(new BorderLayout());
//
//        // Exercise name label
//        JLabel exerciseLabel = new JLabel(exercise);
//        exerciseLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
//        rowPanel.add(exerciseLabel, BorderLayout.NORTH);
//
//        // Table for exercise details
//        String[] columnNames = {"Set", "Reps", "Weight"};
//        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0); // Start with no rows
//        JTable table = new JTable(tableModel);
//
//        // Add the first row
//        tableModel.addRow(new Object[]{1, "", ""});
//
//        JScrollPane tableScrollPane = new JScrollPane(table);
//        rowPanel.add(tableScrollPane, BorderLayout.CENTER);
//
//        // Add row button
//        JButton addRowButton = new JButton("Add Set");
//        addRowButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
//        addRowButton.addActionListener(e -> {
//            tableModel.addRow(new Object[]{tableModel.getRowCount() + 1, "", ""});
//        });
//        rowPanel.add(addRowButton, BorderLayout.SOUTH);
//
//        return rowPanel;
//    }
//
//    private void saveRoutine() {
//        if (selectedExercises.isEmpty()) {
//            JOptionPane.showMessageDialog(frame, "Please add at least one exercise.", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
//            int routineId = getLastRoutineId() + 1;
//            writer.write("Routine ID: " + routineId + "\n");
//            for (String exercise : selectedExercises) {
//                writer.write(exercise + "\n");
//            }
//            writer.write("---\n"); // Separator between routines
//
//            JOptionPane.showMessageDialog(frame, "Routine saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
//
//            // Go back to TrackWorkout
//            new TrackWorkout(frame);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(frame, "Failed to save routine.", "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//
//    private int getLastRoutineId() {
//        int lastId = 0;
//        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                if (line.startsWith("Routine ID: ")) {
//                    lastId = Integer.parseInt(line.substring(11));
//                }
//            }
//        } catch (IOException | NumberFormatException e) {
//            // File may not exist yet or may not contain valid data, return default ID
//        }
//        return lastId;
//    }
//}
