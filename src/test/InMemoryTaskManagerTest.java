package test;

import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.Status;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты InMemoryTaskManager")
class InMemoryTaskManagerTest extends AbstractTaskManagerTest<TaskManager> {
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
    }

    @Test
    @DisplayName("Проверка генерации уникального ID")
    void shouldGenerateUniqueIds() {
        Task task1 = new Task("Задача 1", "Описание", now, Duration.ofMinutes(30));
        Task task2 = new Task("Задача 2", "Описание", now.plusHours(1), Duration.ofHours(2));
        manager.createTask(task1);
        manager.createTask(task2);

        assertNotEquals(task1.getId(), task2.getId(), "ID задач должны быть уникальными");
    }

    @Test
    @DisplayName("Обновление статуса подзадачи и эпика")
    void shouldUpdateEpicStatusWhenSubtaskChanges() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", epic.getId(), now, Duration.ofHours(1));
        manager.createSubtask(subtask);

        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);

        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus(),
                "Статус эпика должен обновиться после изменения подзадачи");
    }

    @Test
    @DisplayName("Удаление всех задач")
    void shouldDeleteAllTasks() {
        Task task1 = new Task("Задача 1", "Описание", now, Duration.ofMinutes(15));
        Task task2 = new Task("Задача 2", "Описание", now.plusHours(1), Duration.ofMinutes(45));
        manager.createTask(task1);
        manager.createTask(task2);

        manager.deleteTaskById(task1.getId());
        manager.deleteTaskById(task2.getId());

        assertTrue(manager.getAllTasks().isEmpty(), "Все задачи должны быть удалены");
    }

    @Test
    @DisplayName("Получение истории после удаления задачи")
    void shouldRemoveTaskFromHistoryWhenDeleted() {
        Task task = new Task("Задача", "Описание", now, Duration.ofMinutes(20));
        manager.createTask(task);
        manager.getTask(task.getId());
        manager.deleteTaskById(task.getId());

        assertFalse(manager.getHistory().contains(task),
                "История не должна содержать удалённую задачу");
    }

    @Test
    @DisplayName("Проверка приоритетного списка задач")
    void shouldReturnTasksInPriorityOrder() {
        Task task1 = new Task("Задача 1", "Описание", now.plusHours(2), Duration.ofMinutes(30));
        Task task2 = new Task("Задача 2", "Описание", now, Duration.ofHours(1));
        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(task2, prioritized.getFirst(), "Первой должна быть задача с более ранним стартом");
    }

    @Test
    @DisplayName("Проверка пересечения времени задач")
    void shouldThrowExceptionOnTimeOverlap() {
        Task task1 = new Task("Задача 1", "Описание", now, Duration.ofHours(1));
        manager.createTask(task1);

        Task task2 = new Task("Задача 2", "Описание", now.plusMinutes(30), Duration.ofHours(1));

        assertThrows(RuntimeException.class, () -> manager.createTask(task2),
                "Должна быть ошибка при пересечении времени задач");
    }

    @Test
    @DisplayName("Удаление всех подзадач эпика → статус NEW")
    void shouldResetEpicStatusWhenAllSubtasksDeleted() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", epic.getId(), now, Duration.ofMinutes(30));
        manager.createSubtask(subtask);
        manager.deleteSubtaskById(subtask.getId());

        assertEquals(Status.NEW, epic.getStatus());
    }
}