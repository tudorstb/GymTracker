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
import java.util.ArrayList;
import java.util.List;

public class DisplayRoutineExercises extends JPanel {
    private JFrame frame;
    private String routineName;
    private List<String> exercises;
    private Connection connection;
    private Image backgroundImage;

    public DisplayRoutineExercises(JFrame existingFrame, String routineName) {
        this.frame = existingFrame;
        this.routineName = routineName;
        this.exercises = new ArrayList<>();
        this.connection = DatabaseConnection.getConnection(); // Use persistent database connection
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
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        // Create a table for each exercise
        for (String exercise : exercises) {
            JPanel exercisePanel = new JPanel();
            exercisePanel.setLayout(new BoxLayout(exercisePanel, BoxLayout.Y_AXIS));
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
            addRow(tableModel); // Start with one row

            JTable exerciseTable = new JTable(tableModel);
            exerciseTable.setFont(new Font("SansSerif", Font.PLAIN, 16));
            exerciseTable.setRowHeight(30);
            exerciseTable.getTableHeader().setReorderingAllowed(false);

            JScrollPane tableScrollPane = new JScrollPane(exerciseTable);
            exercisePanel.add(tableScrollPane);

            // Add "Add Set" button
            JButton addRowButton = new JButton("+ Add Set");
            addRowButton.setFont(new Font("Cooper Black", Font.BOLD, 16));
            addRowButton.addActionListener(e -> {
                addRow(tableModel);
                exercisePanel.revalidate();
                exercisePanel.repaint();
            });

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.setOpaque(false);
            buttonPanel.add(addRowButton);
            exercisePanel.add(buttonPanel);

            centerPanel.add(exercisePanel);
        }

        // Save Button
        JButton saveButton = new JButton("Save Workout");
        saveButton.setFont(new Font("Cooper Black", Font.BOLD, 18));
        saveButton.addActionListener(e -> saveWorkout(centerPanel));
        add(saveButton, BorderLayout.SOUTH);
    }

    private void addRow(DefaultTableModel tableModel) {
        int rowNumber = tableModel.getRowCount() + 1;
        tableModel.addRow(new Object[]{rowNumber, "", "", ""});
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
                                String weight = (String) model.getValueAt(row, 1);
                                String repetitions = (String) model.getValueAt(row, 2);
                                String notes = (String) model.getValueAt(row, 3);

                                if (weight != null && repetitions != null && !weight.isEmpty() && !repetitions.isEmpty()) {
                                    saveToDatabase(exerciseName, weight, repetitions, notes);
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

    private void saveToDatabase(String exerciseName, String weight, String repetitions, String notes) {
        String query = "INSERT INTO workout_entries (routine_name, exercise_name, weight, repetitions, notes) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, routineName);
            statement.setString(2, exerciseName);
            statement.setString(3, weight);
            statement.setString(4, repetitions);
            statement.setString(5, notes);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to save entry to database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
