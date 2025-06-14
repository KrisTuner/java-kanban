package test;

import model.Task;
import org.junit.jupiter.api.Test;
import util.Status;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void equalsShouldReturnTrueForSameId() {
        Task task1 = new Task("Task 1", "Description");
        Task task2 = new Task("Task 2", "Different Description");
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны");
    }

    @Test
    void equalsShouldReturnFalseForDifferentIds() {
        Task task1 = new Task("Task", "Description");
        Task task2 = new Task("Task", "Description");
        task1.setId(1);
        task2.setId(2);

        assertNotEquals(task1, task2, "Задачи с разными ID не должны быть равны");
    }

    @Test
    void equalsShouldReturnFalseForDifferentClass() {
        Task task = new Task("Task", "Description");
        assertNotEquals(new Object(), task, "Задача не должна быть равна объекту другого класса");
    }

    @Test
    void equalsShouldReturnFalseForNull() {
        Task task = new Task("Task", "Description");
        assertNotEquals(null, task, "Задача не должна быть равна null");
    }

    @Test
    void hashCodeShouldBeEqualForSameId() {
        Task task1 = new Task("Task 1", "Desc 1");
        Task task2 = new Task("Task 2", "Desc 2");
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1.hashCode(), task2.hashCode(), "HashCode должен совпадать для одинаковых ID");
    }

    @Test
    void toStringShouldContainAllFields() {
        Task task = new Task("Test Task", "Test Description");
        task.setId(1);
        task.setStatus(Status.IN_PROGRESS);

        String expected = "Task{"
                + "id=1"
                + ", name='Test Task'"
                + ", description='Test Description'"
                + ", status=IN_PROGRESS"
                + "}";
        assertEquals(expected, task.toString(), "toString() должен содержать все поля");
    }

    @Test
    void newTaskShouldHaveNewStatus() {
        Task task = new Task("Task", "Description");
        assertEquals(Status.NEW, task.getStatus(), "Новая задача должна иметь статус NEW");
    }
}