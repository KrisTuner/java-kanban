package test;

import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DataIntegrityTest {
    private final TaskManager manager = Managers.getDefault();

    @Test
    void shouldRemoveSubtaskIdsFromEpicWhenSubtaskDeleted() {
        Epic epic = new Epic("Ремонт", "Сделать ремонт в комнате");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Покраска стен", "Купить краску", epic.getId());
        manager.createSubtask(subtask);
        manager.deleteSubtaskById(subtask.getId());
        assertFalse(epic.getSubtaskIds().contains(subtask.getId()),
                "Эпик не должен содержать ID удалённой подзадачи");
    }

    @Test
    void shouldRemoveSubtasksWhenEpicDeleted() {
        Epic epic = new Epic("Подготовка к отпуску", "Собрать чемодан");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Купить билеты", "Авиабилеты", epic.getId());
        manager.createSubtask(subtask);
        manager.deleteEpicById(epic.getId());
        assertNull(manager.getSubtask(subtask.getId()),
                "Подзадача должна удаляться вместе с эпиком");
    }

    @Test
    void shouldIgnoreManualIdChanges() {
        Task task = new Task("Уборка", "Помыть пол");
        manager.createTask(task);
        int originalId = task.getId();
        task.setId(999);
        assertEquals(task, manager.getTask(originalId),
                "Менеджер должен игнорировать изменение ID через сеттер");
    }

    @Test
    void shouldRemoveTaskFromHistoryWhenDeleted() {
        Task task = new Task("Покупки", "Молоко, хлеб");
        manager.createTask(task);
        manager.getTask(task.getId());
        manager.deleteTaskById(task.getId());
        assertFalse(manager.getHistory().contains(task),
                "История не должна содержать удалённые задачи");
    }
}