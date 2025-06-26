package test;

import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.Status;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для Task")
class TaskTest {
    @Test
    @DisplayName("Сравнение задач по ID")
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Task 1", "Description");
        Task task2 = new Task("Task 2", "Different Description");

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны");
    }

    @Test
    @DisplayName("Статус новой задачи")
    void newTaskShouldHaveNewStatus() {
        Task task = new Task("Task", "Description");
        assertEquals(Status.NEW, task.getStatus(),
                "Новая задача должна иметь статус NEW");
    }

    @Test
    @DisplayName("Строковое представление")
    void toStringShouldContainAllFields() {
        Task task = new Task("Test", "Description");
        task.setId(1);
        task.setStatus(Status.IN_PROGRESS);

        String expected = "Task{id=1, name='Test', description='Description', status=IN_PROGRESS}";
        assertEquals(expected, task.toString(),
                "toString() должен содержать все поля");
    }

    @Test
    @DisplayName("Изменение статуса")
    void shouldChangeStatus() {
        Task task = new Task("Task", "Desc");
        task.setStatus(Status.DONE);

        assertEquals(Status.DONE, task.getStatus(),
                "Статус задачи должен изменяться");
    }

    @Test
    @DisplayName("Проверка hashCode")
    void hashCodeShouldBeConsistentWithEquals() {
        Task task1 = new Task("Task 1", "Description");
        Task task2 = new Task("Task 2", "Description");

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1.hashCode(), task2.hashCode(),
                "hashCode должен быть одинаковым для задач с одинаковым ID");
    }
}