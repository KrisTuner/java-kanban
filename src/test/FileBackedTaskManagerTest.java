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
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты FileBackedTaskManager")
class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    @DisplayName("Сохранение и загрузка пустого менеджера")
    void shouldSaveAndLoadEmptyManager() {
        manager.save();
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loaded.getAllTasks().isEmpty(), "Должен загружаться пустой менеджер");
    }

    @Test
    @DisplayName("Сохранение и загрузка задач")
    void shouldSaveAndLoadTasks() {
        Task task = new Task("Task", "Description");
        manager.createTask(task);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loaded.getAllTasks().size(),
                "Должна загружаться сохранённая задача");
    }

    @Test
    @DisplayName("Сохранение и загрузка эпиков с подзадачами")
    void shouldSaveAndLoadEpicsWithSubtasks() {
        Epic epic = new Epic("Epic", "Description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", epic.getId());
        manager.createSubtask(subtask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loaded.getEpic(epic.getId()).getSubtaskIds().size(),
                "Должны загружаться связи эпик-подзадача");
    }

    @Test
    @DisplayName("Ошибка сохранения в несуществующий файл")
    void shouldThrowExceptionWhenSavingToInvalidFile() {
        File invalidFile = new File("/invalid/path/tasks.csv");
        FileBackedTaskManager invalidManager = new FileBackedTaskManager(invalidFile);

        Task task = new Task("Task", "Description");
        assertThrows(RuntimeException.class, () -> invalidManager.createTask(task),
                "Должна выбрасываться ошибка при сохранении в невалидный файл");
    }

    @Test
    @DisplayName("Удаление задач и проверка их отсутствия после загрузки")
    void shouldNotLoadDeletedTasks() {
        Task task = new Task("Task", "Description");
        manager.createTask(task);
        manager.deleteTaskById(task.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertNull(loaded.getTask(task.getId()),
                "Удалённая задача не должна загружаться");
    }
}