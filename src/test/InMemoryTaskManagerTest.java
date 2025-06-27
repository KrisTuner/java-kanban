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
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты InMemoryTaskManager")
class InMemoryTaskManagerTest {
    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
    }

    @Test
    @DisplayName("Создание и поиск задачи")
    void shouldCreateAndFindTask() {
        Task task = new Task("Задача", "Описание");
        manager.createTask(task);

        assertEquals(task, manager.getTask(task.getId()),
                "Созданная задача должна находиться по ID");
    }

    @Test
    @DisplayName("Обновление статуса задачи")
    void shouldUpdateTaskStatus() {
        Task task = new Task("Задача", "Описание");
        manager.createTask(task);
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);

        assertEquals(Status.IN_PROGRESS, manager.getTask(task.getId()).getStatus(),
                "Статус задачи должен обновляться");
    }

    @Test
    @DisplayName("Удаление задачи")
    void shouldDeleteTask() {
        Task task = new Task("Задача", "Описание");
        manager.createTask(task);
        manager.deleteTaskById(task.getId());

        assertNull(manager.getTask(task.getId()),
                "Задача должна удаляться из менеджера");
    }

    @Test
    @DisplayName("Создание эпика с подзадачами")
    void shouldCreateEpicWithSubtasks() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", epic.getId());
        manager.createSubtask(subtask);

        assertEquals(1, manager.getEpic(epic.getId()).getSubtaskIds().size(),
                "Эпик должен содержать подзадачи");
    }

    @Test
    @DisplayName("Обновление статуса эпика")
    void shouldUpdateEpicStatusWhenSubtasksChange() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", epic.getId());
        manager.createSubtask(subtask);

        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);

        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus(),
                "Статус эпика должен обновляться");
    }

    @Test
    @DisplayName("Проверка уникальности ID")
    void shouldGenerateUniqueIds() {
        Task task1 = new Task("Задача 1", "Описание");
        Task task2 = new Task("Задача 2", "Описание");
        manager.createTask(task1);
        manager.createTask(task2);

        assertNotEquals(task1.getId(), task2.getId(),
                "ID задач должны быть уникальными");
    }

    @Test
    @DisplayName("Добавление в историю")
    void shouldAddTasksToHistory() {
        Task task = new Task("Задача", "Описание");
        manager.createTask(task);
        manager.getTask(task.getId());

        assertEquals(1, manager.getHistory().size(),
                "История должна содержать просмотренные задачи");
    }

    @Test
    @DisplayName("Смешанные статусы подзадач")
    void shouldSetInProgressForMixedSubtasks() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание", epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание", epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus(),
                "При разных статусах подзадач статус эпика должен быть IN_PROGRESS");
    }
}