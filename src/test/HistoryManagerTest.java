package test;

import manager.HistoryManager;
import manager.Managers;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты HistoryManager")
class HistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Задача 1", "Описание", now, Duration.ofMinutes(30));
        task2 = new Task("Задача 2", "Описание", now.plusHours(1), Duration.ofHours(1));
        task3 = new Task("Задача 3", "Описание", now.plusHours(2), Duration.ofMinutes(45));
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
    }

    @Test
    @DisplayName("Пустая история")
    void shouldReturnEmptyHistoryWhenNoTasksViewed() {
        assertTrue(historyManager.getHistory().isEmpty(), "История должна быть пустой");
    }

    @Test
    @DisplayName("Дублирование задачи в истории")
    void shouldNotDuplicateTasksInHistory() {
        historyManager.add(task1);
        historyManager.add(task1);

        assertEquals(1, historyManager.getHistory().size(), "Дубликаты не должны добавляться");
    }

    @Test
    @DisplayName("Удаление из начала истории")
    void shouldRemoveFromHistoryStart() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());

        assertEquals(List.of(task2, task3), historyManager.getHistory(), "Задача в начале должна удалиться");
    }

    @Test
    @DisplayName("Удаление из середины истории")
    void shouldRemoveFromHistoryMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());

        assertEquals(List.of(task1, task3), historyManager.getHistory(), "Задача в середине должна удалиться");
    }

    @Test
    @DisplayName("Удаление из конца истории")
    void shouldRemoveFromHistoryEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());

        assertEquals(List.of(task1, task2), historyManager.getHistory(), "Задача в конце должна удалиться");
    }

    @Test
    @DisplayName("История должна быть пустой после удаления единственной задачи")
    void shouldBeEmptyAfterSingleTaskRemoval() {
        historyManager.add(task1);
        historyManager.remove(task1.getId());
        assertTrue(historyManager.getHistory().isEmpty());
    }
}