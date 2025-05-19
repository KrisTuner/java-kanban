package test;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import model.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void shouldReturnInitializedTaskManager() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Менеджер задач не должен быть null");

        Task task = new Task("Test", "Description");
        manager.createTask(task);
        assertEquals(task, manager.getTask(task.getId()));
    }

    @Test
    void shouldReturnInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Менеджер истории не должен быть null");

        Task task = new Task("Test", "Description");
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
    }
}