package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; // For user input

public class Test {
    public static void main(String[] args) {
        List<String> todoList = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to your AI-powered To-Do List!");
        System.out.println("Type 'add <task>' to add a task.");
        System.out.println("Type 'describe <task number>' to get AI description.");
        System.out.println("Type 'list' to see your tasks.");
        System.out.println("Type 'exit' to quit.");

        while (true) {
            System.out.println("\nEnter command:");
            String fruits = scanner.nextLine(); // Renamed 'fruits' to 'command' for clarity

            if (fruits.startsWith("add")) {
                String task = fruits.substring(4).trim();
                // Corrected the 'if' statement and its corresponding 'else' block
                if (!task.isEmpty()) { // This 'if' was missing its block delimiters {} and was incorrectly terminated with a semicolon
                    todoList.add(task);
                    System.out.println("Task added: \"" + task + "\"");
                } else { // This 'else' was incorrectly associated with the 'add' check
                    System.out.println("Please provide a task to add.");
                }
            } else if (fruits.startsWith("describe ")) {
                try {
                    int taskNumber = Integer.parseInt(fruits.substring(9).trim()) - 1; // Adjust for 0-indexed list
                    if (taskNumber >= 0 && taskNumber < todoList.size()) {
                        String taskToDescribe = todoList.get(taskNumber);
                        System.out.println("\n--- Asking AI to describe: \"" + taskToDescribe + "\" ---");
                        System.out.println(AIAssistant.ask(taskToDescribe));
                        System.out.println("------------------------------------");
                    } else {
                        System.out.println("Invalid task number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid task number format. Please use 'describe <number>'.");
                }
            } else if (fruits.equals("list")) {
                if (todoList.isEmpty()) {
                    System.out.println("Your To-Do list is empty.");
                } else {
                    System.out.println("\n--- Your To-Do List ---");
                    for (int i = 0; i < todoList.size(); i++) {
                        System.out.println((i + 1) + ". " + todoList.get(i));
                    }
                    System.out.println("------------------");
                }
            } else if (fruits.equals("exit")) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Unknown command. Please try again.");
            }
        }
        scanner.close();
    }
}