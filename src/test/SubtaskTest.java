package test;

import model.Subtask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для Subtask")
class SubtaskTest {
    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("Сравнение подзадач по ID и epicId")
    void subtasksWithSameIdAndEpicIdShouldBeEqual() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description", 1, now, Duration.ofHours(1));
        Subtask subtask2 = new Subtask("Subtask 2", "Different description", 1, now.plusHours(1), Duration.ofMinutes(30));

        subtask1.setId(1);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым ID и epicId должны быть равны");
    }

    @Test
    @DisplayName("Получение epicId")
    void shouldReturnCorrectEpicId() {
        int epicId = 5;
        Subtask subtask = new Subtask("Test", "Desc", epicId, now, Duration.ofMinutes(15));

        assertEquals(epicId, subtask.getEpicId(),
                "Должен возвращаться корректный epicId");
    }

    @Test
    @DisplayName("Проверка наследования от Task")
    void shouldInheritFromTask() {
        Subtask subtask = new Subtask("Test", "Desc", 1, now, Duration.ofHours(2));
        assertInstanceOf(Task.class, subtask,
                "Subtask должен наследоваться от Task");
    }

    @Test
    @DisplayName("Проверка временных параметров")
    void shouldHandleTimeParameters() {
        Subtask subtask = new Subtask("Test", "Desc", 1, now, Duration.ofHours(3));
        assertEquals(now, subtask.getStartTime(), "Должно сохраняться время начала");
        assertEquals(Duration.ofHours(3), subtask.getDuration(), "Должна сохраняться продолжительность");
    }
}