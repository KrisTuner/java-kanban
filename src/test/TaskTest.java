package test;

import model.Task;
import org.junit.jupiter.api.Test;
import util.Status;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void shouldBeEqualWhenIdsAreSame() {
        Task task1 = new Task("Task 1", "Description");
        Task task2 = new Task("Task 2", "Different description");
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны");
        assertEquals(task1.hashCode(), task2.hashCode(), "HashCode должен совпадать");
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        Task task1 = new Task("Task", "Description");
        Task task2 = new Task("Task", "Description");
        task1.setId(1);
        task2.setId(2);

        assertNotEquals(task1, task2, "Задачи с разными ID не должны быть равны");
    }

    @Test
    void shouldNotBeEqualWithDifferentClass() {
        Task task = new Task("Task", "Description");
        Object otherObject = new Object();
        assertNotEquals(task, otherObject, "Задача не должна быть равна объекту другого класса");
    }

    @Test
    void shouldReturnCorrectStringRepresentation() {
        Task task = new Task("Task", "Description");
        task.setId(1);
        task.setStatus(Status.IN_PROGRESS);

        String expected = "Task{id=1, name='Task', description='Description', status=IN_PROGRESS}";
        assertEquals(expected, task.toString(), "Метод toString() возвращает неверный формат");
    }

    @Test
    void shouldHandleNullInEquals() {
        Task task = new Task("Task", "Description");
        assertNotEquals(null, task, "Задача не должна быть равна null");
    }

    @Test
    void shouldChangeStatus() {
        Task task = new Task("Task", "Description");
        task.setStatus(Status.DONE);
        assertEquals(Status.DONE, task.getStatus(), "Статус задачи не изменился");
    }

    @Test
    void shouldChangeId() {
        Task task = new Task("Task", "Description");
        task.setId(999);
        assertEquals(999, task.getId(), "ID задачи не изменился");
    }

    @Test
    void shouldReturnInitialStatusAsNew() {
        Task task = new Task("Task", "Description");
        assertEquals(Status.NEW, task.getStatus(), "Начальный статус должен быть NEW");
    }

    @Test
    void shouldNotBeEqualWithSameIdButDifferentFields() {
        Task task1 = new Task("Task 1", "Desc 1");
        Task task2 = new Task("Task 2", "Desc 2");
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Equals() должен учитывать только ID");
    }

    @Test
    void shouldMaintainConsistentHashCode() {
        Task task = new Task("Task", "Description");
        task.setId(1);
        int initialHash = task.hashCode();

        task.setStatus(Status.DONE);
        assertEquals(initialHash, task.hashCode(),
                "HashCode не должен меняться при изменении статуса");
    }
}