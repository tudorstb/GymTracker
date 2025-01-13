import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DisplayRoutineExercises extends JPanel {
    private JFrame frame;
    private String routineName;
    private List<String> exercises;
    private Connection connection;

    public DisplayRoutineExercises(JFrame existingFrame, String routineName) {
        this.frame = existingFrame;
        this.routineName = routineName;
        this.exercises = new ArrayList<>();
        this.connection = DatabaseConnection.getConnection();
        loadExercises();
        setupUI();

        frame.setContentPane(this);
        frame.revalidate();
        frame.repaint();
    }

    private void loadExercises() {
        try (BufferedReader reader = new BufferedReader(new FileReader("selected_routine.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                exercises.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to load exercises for the selected routine.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel(routineName, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Cooper Black", Font.PLAIN, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Center panel for tables
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Create a table for each exercise
        for (String exercise : exercises) {
            JPanel exercisePanel = new JPanel(new BorderLayout());
            TitledBorder border = BorderFactory.createTitledBorder(exercise);
            exercisePanel.setBorder(border);

            String[] columnNames = {"Weight (kg)", "Repetitions"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
            JTable exerciseTable = new JTable(tableModel);
            exerciseTable.setFont(new Font("SansSerif", Font.PLAIN, 16));
            exerciseTable.setRowHeight(30);
            JScrollPane tableScrollPane = new JScrollPane(exerciseTable);
            exercisePanel.add(tableScrollPane, BorderLayout.CENTER);

            // Add "plus" button to add rows
            JButton addRowButton = new JButton("+ Add Set");
            addRowButton.setFont(new Font("Cooper Black", Font.BOLD, 16));
            addRowButton.addActionListener(e -> tableModel.addRow(new Object[]{"", ""}));
            exercisePanel.add(addRowButton, BorderLayout.SOUTH);

            centerPanel.add(exercisePanel);
        }

        // Save Button
        JButton saveButton = new JButton("Save Workout");
        saveButton.setFont(new Font("Cooper Black", Font.BOLD, 18));
        saveButton.addActionListener(e -> saveWorkout(centerPanel));
        add(saveButton, BorderLayout.SOUTH);
    }

    private void saveWorkout(JPanel centerPanel) {
        try {
            for (Component component : centerPanel.getComponents()) {
                if (component instanceof JPanel) {
                    JPanel exercisePanel = (JPanel) component;
                    TitledBorder border = (TitledBorder) exercisePanel.getBorder();
                    String exerciseName = border.getTitle();

                    Component[] panelComponents = exercisePanel.getComponents();
                    for (Component subComponent : panelComponents) {
                        if (subComponent instanceof JScrollPane) {
                            JScrollPane scrollPane = (JScrollPane) subComponent;
                            JTable table = (JTable) scrollPane.getViewport().getView();
                            DefaultTableModel model = (DefaultTableModel) table.getModel();

                            for (int row = 0; row < model.getRowCount(); row++) {
                                String weight = (String) model.getValueAt(row, 0);
                                String repetitions = (String) model.getValueAt(row, 1);

                                if (weight != null && repetitions != null && !weight.isEmpty() && !repetitions.isEmpty()) {
                                    saveToDatabase(exerciseName, weight, repetitions);
                                }
                            }
                        }
                    }
                }
            }

            JOptionPane.showMessageDialog(frame, "Workout saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Failed to save workout.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void saveToDatabase(String exerciseName, String weight, String repetitions) {
        String query = "INSERT INTO workout_entries (routine_name, exercise_name, weight, repetitions) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, routineName);
            statement.setString(2, exerciseName);
            statement.setString(3, weight);
            statement.setString(4, repetitions);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to save entry to database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
