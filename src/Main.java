import manager.FileBackedTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Тест базовой функциональности ===");
        testBasicFunctionality();

        System.out.println("\n=== Тест пустого файла ===");
        testEmptyFile();

        System.out.println("\n=== Тест сохранения нескольких задач ===");
        testSaveMultipleTasks();

        System.out.println("\n=== Тест загрузки нескольких задач ===");
        testLoadMultipleTasks();

        System.out.println("\n=== Тест пересечений задач ===");
        testTimeOverlaps();
    }

    public static void testBasicFunctionality() {
        File tempFile = createTempFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task task = new Task("Тестовая задача", "Описание",
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(30));
        manager.createTask(task);

        Epic epic = new Epic("Тестовый эпик", "Описание эпика");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Тестовая подзадача", "Описание",
                epic.getId(), LocalDateTime.of(2023, 1, 1, 11, 0), Duration.ofMinutes(45));
        manager.createSubtask(subtask);
        System.out.println("Все задачи: " + manager.getAllTasks());
        System.out.println("Все эпики: " + manager.getAllEpics());
        System.out.println("Все подзадачи: " + manager.getAllSubtasks());
        System.out.println("Приоритетные задачи: " + manager.getPrioritizedTasks());
        manager.getTask(task.getId());
        manager.getEpic(epic.getId());
        manager.getSubtask(subtask.getId());
        System.out.println("История: " + manager.getHistory());
    }

    public static void testEmptyFile() {
        File tempFile = createTempFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        System.out.println("Загруженные задачи: " + loadedManager.getAllTasks());
        System.out.println("Загруженные эпики: " + loadedManager.getAllEpics());
        System.out.println("Загруженные подзадачи: " + loadedManager.getAllSubtasks());
    }

    public static void testSaveMultipleTasks() {
        File tempFile = createTempFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Задача 1", "Описание 1",
                LocalDateTime.of(2023, 1, 1, 9, 0), Duration.ofMinutes(60));
        Task task2 = new Task("Задача 2", "Описание 2",
                LocalDateTime.of(2023, 1, 1, 14, 0), Duration.ofMinutes(90));
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание",
                epic.getId(), LocalDateTime.of(2023, 1, 1, 11, 0), Duration.ofMinutes(120));
        manager.createSubtask(subtask);

        try {
            String content = Files.readString(tempFile.toPath());
            System.out.println("Содержимое файла:\n" + content);
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    }

    public static void testLoadMultipleTasks() {
        File tempFile = createTempFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task = new Task("Задача", "Описание",
                LocalDateTime.of(2023, 1, 2, 10, 0), Duration.ofMinutes(30));
        manager.createTask(task);

        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание",
                epic.getId(), LocalDateTime.of(2023, 1, 2, 11, 0), Duration.ofMinutes(45));
        manager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        System.out.println("Загруженные задачи: " + loadedManager.getAllTasks());
        System.out.println("Загруженные эпики: " + loadedManager.getAllEpics());
        System.out.println("Загруженные подзадачи: " + loadedManager.getAllSubtasks());
        System.out.println("Приоритетные задачи: " + loadedManager.getPrioritizedTasks());
    }

    public static void testTimeOverlaps() {
        File tempFile = createTempFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task task1 = new Task("Задача 1", "Описание",
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(60));
        manager.createTask(task1);
        System.out.println("Задача 1 создана: " + task1);
        Task task2 = new Task("Задача 2", "Описание",
                LocalDateTime.of(2023, 1, 1, 10, 30), Duration.ofMinutes(30));
        try {
            manager.createTask(task2);
            System.err.println("Ошибка: пересечение не обнаружено!");
        } catch (RuntimeException e) {
            System.out.println("Успех: " + e.getMessage());
        }
        Task task3 = new Task("Задача 3", "Описание",
                LocalDateTime.of(2023, 1, 1, 12, 0), Duration.ofMinutes(30));
        manager.createTask(task3);
        System.out.println("Задача 3 создана (без пересечений): " + task3);
    }

    private static File createTempFile() {
        try {
            return File.createTempFile("tasks", ".csv");
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл", e);
        }
    }
}