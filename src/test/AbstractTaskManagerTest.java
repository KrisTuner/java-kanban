package test;

import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractTaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    abstract void setUp() throws IOException;

    @Test
    @DisplayName("Создание и получение задачи")
    void shouldCreateAndGetTask() {
        Task task = new Task("Задача", "Описание", now, Duration.ofMinutes(30));
        manager.createTask(task);

        assertEquals(task, manager.getTask(task.getId()), "Задача должна быть найдена");
    }

    @Test
    @DisplayName("Обновление задачи")
    void shouldUpdateTask() {
        Task task = new Task("Задача", "Описание", now, Duration.ofHours(1));
        manager.createTask(task);
        Task updatedTask = new Task("Обновлённая задача", "Новое описание",
                now.plusHours(2), Duration.ofMinutes(45));
        updatedTask.setId(task.getId());
        manager.updateTask(updatedTask);

        assertEquals("Обновлённая задача", manager.getTask(task.getId()).getName(),
                "Название задачи должно обновиться");
        assertEquals("Новое описание", manager.getTask(task.getId()).getDescription(),
                "Описание задачи должно обновиться");
    }

    @Test
    @DisplayName("Удаление задачи")
    void shouldDeleteTask() {
        Task task = new Task("Задача", "Описание", now, Duration.ofMinutes(45));
        manager.createTask(task);
        manager.deleteTaskById(task.getId());

        assertNull(manager.getTask(task.getId()), "Задача должна удалиться");
    }

    @Test
    @DisplayName("Создание эпика с подзадачами")
    void shouldCreateEpicWithSubtasks() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", epic.getId(),
                now, Duration.ofHours(2));
        manager.createSubtask(subtask);

        assertEquals(1, manager.getEpicSubtasks(epic.getId()).size(),
                "Эпик должен содержать подзадачи");
    }

    @Test
    @DisplayName("Проверка пересечения времени задач")
    void shouldDetectTimeOverlaps() {
        Task task1 = new Task("Задача 1", "Описание", now, Duration.ofHours(1));
        manager.createTask(task1);

        Task task2 = new Task("Задача 2", "Описание", now.plusMinutes(30), Duration.ofHours(1));

        assertThrows(RuntimeException.class, () -> manager.createTask(task2),
                "Должна быть ошибка пересечения");
    }
}