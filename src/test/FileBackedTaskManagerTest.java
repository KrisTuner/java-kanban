package test;

import manager.FileBackedTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты FileBackedTaskManager")
class FileBackedTaskManagerTest extends AbstractTaskManagerTest<FileBackedTaskManager> {
    private File tempFile;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    @Override
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    @DisplayName("Сохранение и загрузка пустого менеджера")
    void shouldSaveAndLoadEmptyManager() {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Загруженный менеджер должен быть пустым");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Загруженный менеджер должен быть пустым");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Загруженный менеджер должен быть пустым");
    }

    @Test
    @DisplayName("Сохранение и загрузка задач")
    void shouldSaveAndLoadTasks() {
        Task task = new Task("Задача", "Описание", now, Duration.ofHours(1));
        manager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loadedManager.getTask(task.getId());

        assertNotNull(loadedTask, "Задача должна загрузиться из файла");
        assertEquals(task.getName(), loadedTask.getName(), "Название задачи должно сохраниться");
        assertEquals(task.getStartTime(), loadedTask.getStartTime(), "Время начала должно сохраниться");
    }

    @Test
    @DisplayName("Сохранение и загрузка эпика с подзадачами")
    void shouldSaveAndLoadEpicWithSubtasks() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание",
                epic.getId(), now.plusHours(1), Duration.ofMinutes(30));
        manager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Epic loadedEpic = loadedManager.getEpic(epic.getId());
        Subtask loadedSubtask = loadedManager.getSubtask(subtask.getId());

        assertNotNull(loadedEpic, "Эпик должен загрузиться из файла");
        assertNotNull(loadedSubtask, "Подзадача должна загрузиться из файла");
        assertEquals(1, loadedEpic.getSubtaskIds().size(), "Эпик должен содержать подзадачу");
        assertEquals(epic.getId(), loadedSubtask.getEpicId(), "Связь эпик-подзадача должна сохраниться");
    }

    @Test
    @DisplayName("Обработка несуществующего файла")
    void shouldThrowExceptionWhenFileNotFound() {
        File invalidFile = new File("nonexistent_file.csv");
        assertThrows(RuntimeException.class,
                () -> FileBackedTaskManager.loadFromFile(invalidFile),
                "Должно выбрасываться исключение при загрузке из несуществующего файла");
    }

    @Test
    @DisplayName("Обработка пустого файла")
    void shouldHandleEmptyFile() throws IOException {
        Files.writeString(tempFile.toPath(), "");
        assertDoesNotThrow(
                () -> FileBackedTaskManager.loadFromFile(tempFile),
                "Пустой файл не должен вызывать ошибок"
        );
    }

    @Test
    @DisplayName("Проверка содержимого файла")
    void shouldWriteCorrectDataToFile() throws IOException {
        Task task = new Task("Тест", "Описание", now, Duration.ofMinutes(45));
        manager.createTask(task);

        String content = Files.readString(tempFile.toPath());
        assertTrue(content.contains("id,type,name,status,description,startTime,duration,epic"),
                "Файл должен содержать заголовок");
        assertTrue(content.contains("Тест"),
                "Файл должен содержать данные задачи");
    }
}