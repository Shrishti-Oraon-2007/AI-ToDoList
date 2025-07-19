package com.example;

import javax.swing.*;           // For JFrame, JPanel, JButton, JList, etc.
import javax.swing.event.ListSelectionEvent; // For ListSelectionEvent
import javax.swing.event.ListSelectionListener; // For ListSelectionListener
import java.awt.*;              // For BorderLayout, FlowLayout, etc.
import java.awt.event.ActionEvent; // For ActionEvent
import java.awt.event.ActionListener; // For ActionListener
import java.util.ArrayList;     // For ArrayList
import java.util.List;          // For List interface

public class ToDoListGUI extends JFrame {

    private List<String> todoList;
    private DefaultListModel<String> listModel;
    private JList<String> taskJList;
    private JTextField taskInputField;
    private JTextArea descriptionArea;

    // Declare buttons as fields so they can be accessed in addListeners
    private JButton addButton;
    private JButton describeButton;

    // Correct Constructor: No return type, just the class name
    public ToDoListGUI() {
        // Initialize data structures
        todoList = new ArrayList<>();
        listModel = new DefaultListModel<>();

        // --- JFrame Setup ---
        setTitle("AI-Powered To-Do List");
        setSize(750, 550); // Adjusted size for better layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation
        setLocationRelativeTo(null); // Center the window on the screen

        // Initialize and arrange components
        initComponents();
        // Add event listeners to components
        addListeners();
    }

    private void initComponents() {
        // --- Main Panel with BorderLayout ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); // 10px horizontal and vertical gaps
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 10px padding all around
        add(mainPanel); // Add the main panel to the JFrame

        // --- Top Panel: Add Task Input ---
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5)); // 5px gaps
        taskInputField = new JTextField(40); // Increased width for text field
        addButton = new JButton("Add Task"); // Initialize the field
        inputPanel.add(new JLabel("New Task: "), BorderLayout.WEST); // Label to the left
        inputPanel.add(taskInputField, BorderLayout.CENTER); // Input field in the center
        inputPanel.add(addButton, BorderLayout.EAST); // Add button to the right
        mainPanel.add(inputPanel, BorderLayout.NORTH); // Place at the top of mainPanel

        // --- Center Panel: Split Pane for List and Description ---
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplitPane.setResizeWeight(0.4); // Left side (list) gets 40% initial space

        // --- Left Side of Split Pane: Task List ---
        taskJList = new JList<>(listModel); // JList uses the listModel for data
        taskJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only one task can be selected at a time
        JScrollPane listScrollPane = new JScrollPane(taskJList); // Make the list scrollable
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Your Tasks")); // Add a titled border
        centerSplitPane.setLeftComponent(listScrollPane);

        // --- Right Side of Split Pane: AI Description Area ---
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5)); // Panel for description and button
        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false); // Make it read-only
        descriptionArea.setLineWrap(true);    // Wrap text at word boundaries
        descriptionArea.setWrapStyleWord(true); // Ensure whole words are wrapped
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea); // Make description area scrollable
        descriptionScrollPane.setBorder(BorderFactory.createTitledBorder("AI Description")); // Add a titled border

        describeButton = new JButton("Describe Selected Task (AI)"); // Initialize the field
        rightPanel.add(describeButton, BorderLayout.NORTH); // Button at the top of right panel
        rightPanel.add(descriptionScrollPane, BorderLayout.CENTER); // Description area in the center
        centerSplitPane.setRightComponent(rightPanel);

        mainPanel.add(centerSplitPane, BorderLayout.CENTER); // Place in the center of mainPanel

        // --- Bottom Panel: Status/Info Label ---
        JLabel infoLabel = new JLabel("Welcome to your AI-powered To-Do List! Add tasks and get AI insights.");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the text
        mainPanel.add(infoLabel, BorderLayout.SOUTH); // Place at the bottom of mainPanel
    }

    private void addListeners() {
        // --- Listener for "Add Task" button and Enter key in input field ---
        ActionListener addTaskAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String task = taskInputField.getText().trim();
                if (!task.isEmpty()) {
                    todoList.add(task); // Add to our internal list
                    listModel.addElement(task); // Add to the JList's model (updates the display)
                    taskInputField.setText(""); // Clear the input field
                    descriptionArea.setText("Task added: \"" + task + "\""); // Provide feedback

                    // --- NEW/UPDATED: Automatically select the newly added item ---
                    // This makes it easier for the user to immediately describe a new task.
                    int newIndex = listModel.getSize() - 1;
                    if (newIndex >= 0) { // Ensure there's at least one item
                        taskJList.setSelectedIndex(newIndex);
                        taskJList.ensureIndexIsVisible(newIndex); // Scroll to the new item if list is long
                    }

                } else {
                    // Show a warning dialog if the input is empty
                    JOptionPane.showMessageDialog(ToDoListGUI.this,
                            "Please enter a task to add.",
                            "Empty Task Field",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        addButton.addActionListener(addTaskAction);
        taskInputField.addActionListener(addTaskAction);

        // --- Listener for "Describe Selected Task (AI)" button ---
        describeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = taskJList.getSelectedIndex(); // Get the index of the selected task

                // Debugging line (you can remove this after confirming it works)
                System.out.println("Describe button clicked. Selected index: " + selectedIndex);

                if (selectedIndex != -1) { // Check if a task is actually selected
                    String taskToDescribe = todoList.get(selectedIndex);
                    descriptionArea.setText("--- Asking AI to describe: \"" + taskToDescribe + "\" ---\n");

                    // Use SwingWorker for long-running AI operations to keep GUI responsive
                    new SwingWorker<String, Void>() {
                        @Override
                        protected String doInBackground() throws Exception {
                            // This code runs on a background thread.
                            // Call your actual AI method here.
                            return AIAssistant.ask(taskToDescribe);
                        }

                        @Override
                        protected void done() {
                            // This code runs on the Event Dispatch Thread (EDT) after doInBackground completes.
                            try {
                                String aiResponse = get(); // Get the result from doInBackground
                                descriptionArea.append(aiResponse); // Append AI's response
                                descriptionArea.append("\n------------------------------------");
                            } catch (Exception ex) {
                                // Handle any exceptions that occurred in doInBackground
                                descriptionArea.append("Error getting AI description: " + ex.getMessage());
                                JOptionPane.showMessageDialog(ToDoListGUI.this,
                                        "Error fetching AI description: " + ex.getMessage(),
                                        "AI Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }.execute(); // Start the SwingWorker
                } else {
                    JOptionPane.showMessageDialog(ToDoListGUI.this,
                            "Please select a task from the list to describe.",
                            "No Task Selected",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // --- NEW: Add a ListSelectionListener to JList itself ---
        // This listener fires when the selection changes in the JList.
        // It's crucial for the JList to properly register and update its internal
        // selection state, ensuring getSelectedIndex() returns the correct value.
        taskJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // `getValueIsAdjusting()` is true while the selection is still changing (e.g., during a drag)
                // We typically only care about the final selection.
                if (!e.getValueIsAdjusting()) {
                    // Debugging line to see if selection events are firing
                    // System.out.println("JList selection changed. Selected index: " + taskJList.getSelectedIndex());
                    // You could potentially enable/disable the 'describeButton' here
                    // based on whether taskJList.getSelectedIndex() is -1 or not.
                    describeButton.setEnabled(taskJList.getSelectedIndex() != -1);
                }
            }
        });

        // Initially disable the describe button if no task is selected by default
        describeButton.setEnabled(false);
    }
}