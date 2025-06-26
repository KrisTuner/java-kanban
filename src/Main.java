import manager.FileBackedTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
    }

    public static void testBasicFunctionality() {
        File tempFile = createTempFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task task = new Task("Тестовая задача", "Описание");
        manager.createTask(task);

        Epic epic = new Epic("Тестовый эпик", "Описание эпика");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Тестовая подзадача", "Описание", epic.getId());
        manager.createSubtask(subtask);
        System.out.println("Все задачи: " + manager.getAllTasks());
        System.out.println("Все эпики: " + manager.getAllEpics());
        System.out.println("Все подзадачи: " + manager.getAllSubtasks());
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
        Task task1 = new Task("Задача 1", "Описание 1");
        Task task2 = new Task("Задача 2", "Описание 2");
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", epic.getId());
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
        Task task = new Task("Задача", "Описание");
        manager.createTask(task);

        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание", epic.getId());
        manager.createSubtask(subtask);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        System.out.println("Загруженные задачи: " + loadedManager.getAllTasks());
        System.out.println("Загруженные эпики: " + loadedManager.getAllEpics());
        System.out.println("Загруженные подзадачи: " + loadedManager.getAllSubtasks());
    }

    private static File createTempFile() {
        try {
            return File.createTempFile("tasks", ".csv");
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл", e);
        }
    }
}