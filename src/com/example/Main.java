package com.example;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ToDoListGUI().setVisible(true); // Create and display the main GUI window
            }
        });
    }
}
