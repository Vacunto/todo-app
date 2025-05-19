package com.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;

public class TodoApp extends Application {

    private static final String FILE_NAME = "tasks.txt";
    private ObservableList<Task> tasks = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        loadTasksFromFile(); // Загружаем задачи из файла

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        TextField input = new TextField();
        input.setPromptText("Введите новую задачу");

        Button addButton = new Button("Добавить");
        addButton.setOnAction(e -> {
            String text = input.getText();
            if (!text.isEmpty()) {
                tasks.add(new Task(text));
                input.clear();
                saveTasksToFile(); // Сохраняем после добавления
            }
        });

        ListView<Task> listView = new ListView<>(tasks);

        Button completeButton = new Button("Отметить как выполненное");
        completeButton.setOnAction(e -> {
            Task selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setCompleted(true);
                listView.refresh(); // Обновляем отображение
                saveTasksToFile();  // Сохраняем изменения
            }
        });

        HBox buttons = new HBox(10, completeButton); // Убрали deleteButton

        root.getChildren().addAll(input, addButton, listView, buttons);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Список задач");
        primaryStage.show();
    }

    // Метод для сохранения задач в файл
    private void saveTasksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Task task : tasks) {
                writer.write(task.isCompleted() + ";" + task.getDescription());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    // Метод для загрузки задач из файла
    private void loadTasksFromFile() {
        Path path = Paths.get(FILE_NAME);
        if (Files.exists(path)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";", 2);
                    if (parts.length == 2) {
                        boolean completed = Boolean.parseBoolean(parts[0]);
                        String description = parts[1];
                        Task task = new Task(description);
                        task.setCompleted(completed);
                        tasks.add(task);
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении файла: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}