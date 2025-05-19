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
    void shouldAddAndFindDifferentTaskTypes() {
        Task task = new Task("Task", "Description");
        manager.createTask(task);

        Epic epic = new Epic("Epic", "Description");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description", epic.getId());
        manager.createSubtask(subtask);

        assertEquals(task, manager.getTask(task.getId()));
        assertEquals(epic, manager.getEpic(epic.getId()));
        assertEquals(subtask, manager.getSubtask(subtask.getId()));
    }

    @Test
    void generatedIdShouldNotConflictWithAssignedId() {
        Task taskWithId = new Task("Task with ID", "Description");
        taskWithId.setId(100);
        manager.createTask(taskWithId);

        Task taskWithoutId = new Task("Task without ID", "Description");
        manager.createTask(taskWithoutId);

        assertNotEquals(taskWithId.getId(), taskWithoutId.getId(),
                "ID должны быть уникальными, независимо от способа назначения");
    }

    @Test
    void epicStatusShouldUpdateWhenSubtasksChange() {
        Epic epic = new Epic("Epic", "Description");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Sub 1", "Desc", epic.getId());
        manager.createSubtask(subtask1);
        assertEquals(Status.NEW, epic.getStatus());

        subtask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());

        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void subtaskCannotBeItsOwnEpic() {
        Subtask subtask = new Subtask("Subtask", "Description", 1);
        subtask.setId(1);

        assertThrows(
                IllegalArgumentException.class,
                () -> manager.createSubtask(subtask),
                "Подзадача не должна быть своим же эпиком"
        );
    }
}
