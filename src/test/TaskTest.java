package test;

import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.Status;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для Task")
class TaskTest {
    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("Сравнение задач по ID")
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Task 1", "Description", now, Duration.ofHours(1));
        Task task2 = new Task("Task 2", "Different Description", now.plusHours(2), Duration.ofMinutes(30));

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны");
    }

    @Test
    @DisplayName("Статус новой задачи")
    void newTaskShouldHaveNewStatus() {
        Task task = new Task("Task", "Description", now, Duration.ofMinutes(15));
        assertEquals(Status.NEW, task.getStatus(),
                "Новая задача должна иметь статус NEW");
    }

    @Test
    @DisplayName("Строковое представление")
    void toStringShouldContainAllFields() {
        Task task = new Task("Test", "Description", now, Duration.ofHours(1));
        task.setId(1);
        task.setStatus(Status.IN_PROGRESS);

        String expected = String.format(
                "Task{id=1, name='Test', description='Description', status=IN_PROGRESS, startTime=%s, duration=PT1H}",
                now);
        assertEquals(expected, task.toString(),
                "toString() должен содержать все поля");
    }

    @Test
    @DisplayName("Изменение статуса")
    void shouldChangeStatus() {
        Task task = new Task("Task", "Desc", now, Duration.ofMinutes(45));
        task.setStatus(Status.DONE);

        assertEquals(Status.DONE, task.getStatus(),
                "Статус задачи должен изменяться");
    }

    @Test
    @DisplayName("Проверка hashCode")
    void hashCodeShouldBeConsistentWithEquals() {
        Task task1 = new Task("Task 1", "Description", now, Duration.ofHours(2));
        Task task2 = new Task("Task 2", "Description", now.plusHours(3), Duration.ofMinutes(30));

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1.hashCode(), task2.hashCode(),
                "hashCode должен быть одинаковым для задач с одинаковым ID");
    }

    @Test
    @DisplayName("Проверка временных параметров")
    void shouldHandleTimeParameters() {
        Task task = new Task("Test", "Desc", now, Duration.ofHours(2));
        assertEquals(now, task.getStartTime(), "Должно сохраняться время начала");
        assertEquals(Duration.ofHours(2), task.getDuration(), "Должна сохраняться продолжительность");
        assertEquals(now.plusHours(2), task.getEndTime(), "Должно правильно вычисляться время окончания");
    }
}