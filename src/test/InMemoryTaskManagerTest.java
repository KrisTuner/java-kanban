package test;

import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;
import util.Status;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private final TaskManager manager = Managers.getDefault();

    @Test
    void shouldCreateAndFindTask() {
        Task task = new Task("Покупки", "Купить молоко");
        manager.createTask(task);
        Task savedTask = manager.getTask(task.getId());

        assertNotNull(savedTask, "Задача должна сохраняться");
        assertEquals(task, savedTask, "Сохраненная задача должна соответствовать созданной");
    }

    @Test
    void shouldUpdateTaskStatus() {
        Task task = new Task("Уборка", "Помыть пол");
        manager.createTask(task);
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);

        assertEquals(Status.IN_PROGRESS, manager.getTask(task.getId()).getStatus(),
                "Статус задачи должен обновляться");
    }

    @Test
    void shouldDeleteTask() {
        Task task = new Task("Задача", "Описание");
        manager.createTask(task);
        manager.deleteTaskById(task.getId());

        assertNull(manager.getTask(task.getId()), "Задача должна удаляться");
    }

    @Test
    void shouldCreateEpicWithSubtasks() {
        Epic epic = new Epic("Ремонт", "Сделать ремонт");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Покраска", "Покрасить стены", epic.getId());
        manager.createSubtask(subtask);

        assertEquals(1, manager.getEpic(epic.getId()).getSubtaskIds().size(),
                "Эпик должен содержать подзадачи");
    }

    @Test
    void shouldUpdateEpicStatusWhenSubtasksChange() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", epic.getId());
        manager.createSubtask(subtask);

        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);

        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus(),
                "Статус эпика должен обновляться при изменении подзадач");
    }

    @Test
    void shouldNotAllowSubtaskToBeItsOwnEpic() {
        Subtask subtask = new Subtask("Подзадача", "Описание", 1);
        subtask.setId(1);

        assertThrows(IllegalArgumentException.class, () -> manager.createSubtask(subtask),
                "Подзадача не должна быть своим же эпиком");
    }

    @Test
    void shouldReturnEmptyListForNewManager() {
        assertTrue(manager.getAllTasks().isEmpty(), "Новый менеджер не должен содержать задач");
        assertTrue(manager.getAllEpics().isEmpty(), "Новый менеджер не должен содержать эпиков");
        assertTrue(manager.getAllSubtasks().isEmpty(), "Новый менеджер не должен содержать подзадач");
    }

    @Test
    void shouldGenerateUniqueIds() {
        Task task1 = new Task("Задача 1", "Описание");
        Task task2 = new Task("Задача 2", "Описание");
        manager.createTask(task1);
        manager.createTask(task2);

        assertNotEquals(task1.getId(), task2.getId(), "ID задач должны быть уникальными");
    }

    @Test
    void shouldDeleteAllSubtasksWithEpic() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", epic.getId());
        manager.createSubtask(subtask);
        manager.deleteEpicById(epic.getId());

        assertTrue(manager.getAllSubtasks().isEmpty(),
                "Удаление эпика должно удалять все его подзадачи");
    }

    @Test
    void shouldAddTasksToHistory() {
        Task task = new Task("Задача", "Описание");
        manager.createTask(task);
        manager.getTask(task.getId());

        assertEquals(1, manager.getHistory().size(),
                "Просмотр задачи должен добавлять её в историю");
    }
}