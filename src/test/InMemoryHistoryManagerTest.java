package test;

import manager.HistoryManager;
import manager.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты InMemoryHistoryManager")
class InMemoryHistoryManagerTest {
    private HistoryManager history;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        history = Managers.getDefaultHistory();
    }

    @Test
    @DisplayName("Добавление задачи в историю")
    void shouldAddTasksToHistory() {
        Task task = new Task("Task", "Description", now, Duration.ofMinutes(30));
        task.setId(1);
        history.add(task);

        assertEquals(1, history.getHistory().size(),
                "История должна содержать 1 задачу");
    }

    @Test
    @DisplayName("Отсутствие дубликатов в истории")
    void shouldNotContainDuplicates() {
        Task task = new Task("Task", "Description", now, Duration.ofHours(1));
        task.setId(1);

        history.add(task);
        history.add(task);

        assertEquals(1, history.getHistory().size(),
                "История должна содержать только уникальные задачи");
    }

    @Test
    @DisplayName("Удаление задачи из истории")
    void shouldRemoveTaskFromHistory() {
        Task task1 = new Task("Task 1", "Desc", now, Duration.ofMinutes(15));
        Task task2 = new Task("Task 2", "Desc", now.plusHours(1), Duration.ofMinutes(45));
        task1.setId(1);
        task2.setId(2);

        history.add(task1);
        history.add(task2);
        history.remove(task1.getId());

        assertEquals(List.of(task2), history.getHistory(),
                "История должна содержать только оставшиеся задачи");
    }

    @Test
    @DisplayName("Порядок задач в истории")
    void shouldMaintainInsertionOrder() {
        Task task1 = new Task("Task 1", "Desc", now, Duration.ofHours(2));
        Task task2 = new Task("Task 2", "Desc", now.plusHours(3), Duration.ofMinutes(30));
        task1.setId(1);
        task2.setId(2);

        history.add(task1);
        history.add(task2);

        assertEquals(List.of(task1, task2), history.getHistory(),
                "Порядок задач должен соответствовать порядку добавления");
    }

    @Test
    @DisplayName("Работа с разными типами задач")
    void shouldHandleMixedTaskTypes() {
        Task task = new Task("Task", "Desc", now, Duration.ofMinutes(20));
        Epic epic = new Epic("Epic", "Desc");
        Subtask subtask = new Subtask("Subtask", "Desc", 2, now.plusHours(1), Duration.ofHours(1));
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
    @DisplayName("Пустая история")
    void shouldReturnEmptyListForEmptyHistory() {
        assertTrue(history.getHistory().isEmpty(),
                "Для пустой истории должен возвращаться пустой список");
    }

    @Test
    @DisplayName("Удаление из середины истории")
    void shouldRemoveFromMiddleOfHistory() {
        Task task1 = new Task("Task 1", "Desc", now, Duration.ofMinutes(10));
        Task task2 = new Task("Task 2", "Desc", now.plusMinutes(15), Duration.ofMinutes(5));
        Task task3 = new Task("Task 3", "Desc", now.plusHours(1), Duration.ofMinutes(30));
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);

        history.add(task1);
        history.add(task2);
        history.add(task3);
        history.remove(task2.getId());

        assertEquals(List.of(task1, task3), history.getHistory(),
                "После удаления должны остаться задачи по краям");
    }
}