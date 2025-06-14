package test;

import manager.HistoryManager;
import manager.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private final HistoryManager history = Managers.getDefaultHistory();

    @Test
    void shouldAddTasksToHistory() {
        Task task = new Task("Task", "Description");
        task.setId(1);
        history.add(task);

        assertFalse(history.getHistory().isEmpty(), "История не должна быть пустой");
        assertEquals(1, history.getHistory().size(), "История должна содержать 1 задачу");
    }

    @Test
    void shouldNotContainDuplicates() {
        Task task = new Task("Task", "Description");
        task.setId(1);

        history.add(task);
        history.add(task); // Добавляем дубликат

        assertEquals(1, history.getHistory().size(),
                "История должна содержать только последнюю версию задачи");
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        Task task1 = new Task("Task 1", "Desc");
        Task task2 = new Task("Task 2", "Desc");
        task1.setId(1);
        task2.setId(2);

        history.add(task1);
        history.add(task2);
        history.remove(task1.getId());

        assertEquals(1, history.getHistory().size(),
                "После удаления в истории должна остаться 1 задача");
        assertEquals(task2, history.getHistory().getFirst(),
                "Оставшаяся задача должна быть task2");
    }

    @Test
    void shouldMaintainInsertionOrder() {
        Task task1 = new Task("Task 1", "Desc");
        Task task2 = new Task("Task 2", "Desc");
        task1.setId(1);
        task2.setId(2);

        history.add(task1);
        history.add(task2);

        List<Task> expectedOrder = List.of(task1, task2);
        assertEquals(expectedOrder, history.getHistory(),
                "Порядок задач в истории должен соответствовать порядку добавления");
    }

    @Test
    void shouldHandleMixedTaskTypes() {
        Task task = new Task("Task", "Desc");
        Epic epic = new Epic("Epic", "Desc");
        Subtask subtask = new Subtask("Subtask", "Desc", 2);
        task.setId(1);
        epic.setId(2);
        subtask.setId(3);

        history.add(task);
        history.add(epic);
        history.add(subtask);

        assertEquals(3, history.getHistory().size(),
                "История должна содержать все типы задач");
    }

    @Test
    void shouldReturnEmptyListForEmptyHistory() {
        assertTrue(history.getHistory().isEmpty(),
                "Для пустой истории должен возвращаться пустой список");
    }

    @Test
    void shouldRemoveFromMiddleOfHistory() {
        Task task1 = new Task("Task 1", "Desc");
        Task task2 = new Task("Task 2", "Desc");
        Task task3 = new Task("Task 3", "Desc");
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);

        history.add(task1);
        history.add(task2);
        history.add(task3);
        history.remove(task2.getId()); // Удаляем из середины

        List<Task> expected = List.of(task1, task3);
        assertEquals(expected, history.getHistory(),
                "После удаления из середины должны остаться task1 и task3");
    }
}