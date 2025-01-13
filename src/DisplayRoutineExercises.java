import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DisplayRoutineExercises extends JPanel {
    private JFrame frame;
    private String routineName;
    private List<String> exercises;
    private Connection connection;
    private Image backgroundImage;
    private LocalDateTime startTime;

    public DisplayRoutineExercises(JFrame existingFrame, String routineName) {
        this.frame = existingFrame;
        this.routineName = routineName;
        this.exercises = new ArrayList<>();
        this.connection = DatabaseConnection.getConnection(); // Use persistent database connection
        this.startTime = LocalDateTime.now(); // Start the timer when this class is initialized
        loadBackgroundImage();
        loadExercises();
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

    private void loadExercises() {
        String query = "SELECT exercises FROM routines WHERE username = ? AND routine_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            String username;
            try (BufferedReader reader = new BufferedReader(new FileReader("name.txt"))) {
                username = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to load username.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            statement.setString(1, username);
            statement.setString(2, routineName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String exercisesString = resultSet.getString("exercises");
                for (String exercise : exercisesString.split(",")) {
                    exercises.add(exercise.trim());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to load exercises from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel(routineName, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Cooper Black", Font.PLAIN, 24));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        // Center panel for tables
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Create a table for each exercise
        for (String exercise : exercises) {
            JPanel exercisePanel = createExercisePanel(exercise);
            centerPanel.add(exercisePanel, gbc);
            gbc.gridy++;
        }

        // Save Button
        JButton saveButton = new JButton("Save Workout");
        saveButton.setFont(new Font("Cooper Black", Font.BOLD, 18));
        saveButton.addActionListener(e -> saveWorkout(centerPanel));
        add(saveButton, BorderLayout.SOUTH);
    }

    private JPanel createExercisePanel(String exercise) {
        JPanel exercisePanel = new JPanel(new BorderLayout());
        exercisePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                exercise,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Cooper Black", Font.PLAIN, 18),
                Color.WHITE
        ));
        exercisePanel.setOpaque(false);

        String[] columnNames = {"Set Number", "Weight (kg)", "Repetitions", "Notes"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Disable editing for set number
            }
        };

        JTable exerciseTable = new JTable(tableModel);
        exerciseTable.setFont(new Font("SansSerif", Font.PLAIN, 16));
        exerciseTable.setRowHeight(30);
        exerciseTable.getTableHeader().setReorderingAllowed(false);

        // Input validation to allow only numbers for Weight and Repetitions
        exerciseTable.getDefaultEditor(Object.class).addCellEditorListener(new javax.swing.event.CellEditorListener() {
            @Override
            public void editingStopped(javax.swing.event.ChangeEvent e) {
                int editingColumn = exerciseTable.getEditingColumn();
                if (editingColumn == 1 || editingColumn == 2) { // Weight and Repetitions columns
                    String value = (String) exerciseTable.getValueAt(exerciseTable.getEditingRow(), editingColumn);
                    if (!value.matches("\\d*")) {
                        JOptionPane.showMessageDialog(frame, "Please enter only numeric values for Weight and Repetitions.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        exerciseTable.setValueAt("", exerciseTable.getEditingRow(), editingColumn);
                    }
                }
            }

            @Override
            public void editingCanceled(javax.swing.event.ChangeEvent e) {
                // No action needed
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(exerciseTable);
        exerciseTable.setPreferredScrollableViewportSize(
                new Dimension(
                        exerciseTable.getPreferredSize().width,
                        exerciseTable.getRowHeight() * Math.max(1, tableModel.getRowCount())
                )
        );

        exercisePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Add "Add Set" button
        JButton addRowButton = new JButton("+ Add Set");
        addRowButton.setFont(new Font("Cooper Black", Font.BOLD, 16));
        addRowButton.addActionListener(e -> {
            addRow(tableModel);
            exerciseTable.setPreferredScrollableViewportSize(
                    new Dimension(
                            exerciseTable.getPreferredSize().width,
                            exerciseTable.getRowHeight() * tableModel.getRowCount()
                    )
            );
            exercisePanel.revalidate();
            exercisePanel.repaint();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(addRowButton);
        exercisePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Start with one row
        addRow(tableModel);

        return exercisePanel;
    }

    private void addRow(DefaultTableModel tableModel) {
        int rowNumber = tableModel.getRowCount() + 1;
        tableModel.addRow(new Object[]{rowNumber, "", "", ""});
    }

    private void saveWorkout(JPanel centerPanel) {
        try {
            String workoutInsertQuery = "INSERT INTO workouts (user_id, date, duration, notes) VALUES ((SELECT user_id FROM users WHERE username = ?), CURRENT_DATE, CAST(? AS INTERVAL), ?) RETURNING workout_id";
            String workoutExercisesInsertQuery = "INSERT INTO workout_exercises (workout_id, exercise_id, sets, reps, weight) VALUES (?, (SELECT exercise_id FROM exercises WHERE name = ?), ?, ?, ?)";

            connection.setAutoCommit(false);

            int workoutId;
            Duration duration = Duration.between(startTime, LocalDateTime.now()); // Calculate duration
            String intervalDuration = String.format("%d:%d:%d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
            try (PreparedStatement workoutStatement = connection.prepareStatement(workoutInsertQuery)) {
                String username;
                try (BufferedReader reader = new BufferedReader(new FileReader("name.txt"))) {
                    username = reader.readLine();
                }
                workoutStatement.setString(1, username);
                workoutStatement.setString(2, intervalDuration); // Send as INTERVAL
                workoutStatement.setString(3, "Workout notes"); // Placeholder for workout notes
                ResultSet rs = workoutStatement.executeQuery();
                if (rs.next()) {
                    workoutId = rs.getInt("workout_id");
                } else {
                    throw new SQLException("Failed to retrieve workout_id.");
                }
            }

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
                                String weight = (String) model.getValueAt(row, 1);
                                String repetitions = (String) model.getValueAt(row, 2);
                                if (weight == null || weight.isEmpty() || repetitions == null || repetitions.isEmpty()) {
                                    JOptionPane.showMessageDialog(frame, "Please fill out all fields before saving.", "Input Error", JOptionPane.ERROR_MESSAGE);
                                    connection.rollback();
                                    return;
                                }
                                try (PreparedStatement exerciseStatement = connection.prepareStatement(workoutExercisesInsertQuery)) {
                                    exerciseStatement.setInt(1, workoutId);
                                    exerciseStatement.setString(2, exerciseName);
                                    exerciseStatement.setInt(3, 1); // Assuming 1 set as a placeholder
                                    exerciseStatement.setInt(4, Integer.parseInt(repetitions));
                                    exerciseStatement.setDouble(5, Double.parseDouble(weight));
                                    exerciseStatement.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }

            connection.commit();
            JOptionPane.showMessageDialog(frame, "Workout saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            new MainMenuWindow(frame).show(); // Redirect to Main Menu
        } catch (SQLException | IOException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to save workout.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
