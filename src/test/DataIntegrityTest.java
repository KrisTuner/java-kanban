package test;

import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class DataIntegrityTest {
    private final TaskManager manager = Managers.getDefault();
    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("Удаление ID подзадачи из эпика при удалении подзадачи")
    void shouldRemoveSubtaskIdsFromEpicWhenSubtaskDeleted() {
        Epic epic = new Epic("Ремонт", "Сделать ремонт в комнате");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Покраска стен", "Купить краску",
                epic.getId(), now, Duration.ofHours(2));
        manager.createSubtask(subtask);
        manager.deleteSubtaskById(subtask.getId());
        assertFalse(epic.getSubtaskIds().contains(subtask.getId()),
                "Эпик не должен содержать ID удалённой подзадачи");
    }

    @Test
    @DisplayName("Игнорирование ручного изменения ID задачи")
    void shouldIgnoreManualIdChanges() {
        Task task = new Task("Уборка", "Помыть пол", now, Duration.ofMinutes(30));
        manager.createTask(task);
        int originalId = task.getId();
        task.setId(999);
        assertEquals(task, manager.getTask(originalId),
                "Менеджер должен игнорировать изменение ID через сеттер");
    }

    @Test
    @DisplayName("Удаление задачи из истории при её удалении")
    void shouldRemoveTaskFromHistoryWhenDeleted() {
        Task task = new Task("Покупки", "Молоко, хлеб", now, Duration.ofMinutes(15));
        manager.createTask(task);
        manager.getTask(task.getId());
        manager.deleteTaskById(task.getId());
        assertFalse(manager.getHistory().contains(task),
                "История не должна содержать удалённые задачи");
    }
}