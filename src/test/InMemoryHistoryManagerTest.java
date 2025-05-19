package test;

import manager.HistoryManager;
import manager.Managers;
import model.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void shouldKeepTaskVersionInHistory() {
        Task task = new Task("Task", "Description");
        task.setId(1);

        historyManager.add(task);

        task.setName("Updated name");
        historyManager.add(task);

        assertEquals(2, historyManager.getHistory().size());
        assertEquals("Updated name", historyManager.getHistory().getLast().getName());
    }

    @Test
    void shouldNotExceedMaxHistorySize() {
        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Task " + i, "Description");
            task.setId(i);
            historyManager.add(task);
        }

        assertEquals(10, historyManager.getHistory().size());
        assertEquals("Task 6", historyManager.getHistory().getFirst().getName());
    }
}