package test;

import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.Status;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тест статусов Epic")
class EpicStatusTest {
    private TaskManager manager;
    private Epic epic;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
        epic = new Epic("Эпик", "Тест статусов");
        manager.createEpic(epic);
    }

    @Test
    @DisplayName("Все подзадачи NEW → статус Epic NEW")
    void shouldBeNewWhenAllSubtasksNew() {
        Subtask subtask1 = new Subtask("Подзадача 1", "NEW", epic.getId(), now, Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Подзадача 2", "NEW", epic.getId(), now.plusHours(1), Duration.ofMinutes(45));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        assertEquals(Status.NEW, epic.getStatus(), "Эпик должен быть NEW");
    }

    @Test
    @DisplayName("Все подзадачи DONE → статус Epic DONE")
    void shouldBeDoneWhenAllSubtasksDone() {
        Subtask subtask1 = new Subtask("Подзадача 1", "DONE", epic.getId(), now, Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Подзадача 2", "DONE", epic.getId(), now.plusHours(1), Duration.ofMinutes(45));
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        assertEquals(Status.DONE, epic.getStatus(), "Эпик должен быть DONE");
    }

    @Test
    @DisplayName("Подзадачи NEW и DONE → статус Epic IN_PROGRESS")
    void shouldBeInProgressWhenMixedNewAndDone() {
        Subtask subtask1 = new Subtask("Подзадача 1", "NEW", epic.getId(), now, Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Подзадача 2", "DONE", epic.getId(), now.plusHours(1), Duration.ofMinutes(45));
        subtask2.setStatus(Status.DONE);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Эпик должен быть IN_PROGRESS");
    }

    @Test
    @DisplayName("Подзадачи IN_PROGRESS → статус Epic IN_PROGRESS")
    void shouldBeInProgressWhenAllSubtasksInProgress() {
        Subtask subtask1 = new Subtask("Подзадача 1", "IN_PROGRESS", epic.getId(), now, Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Подзадача 2", "IN_PROGRESS", epic.getId(), now.plusHours(1), Duration.ofMinutes(45));
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Эпик должен быть IN_PROGRESS");
    }

    @Test
    @DisplayName("Нет подзадач → статус Epic NEW")
    void shouldBeNewWhenNoSubtasks() {
        assertEquals(Status.NEW, epic.getStatus(), "Пустой эпик должен быть NEW");
    }
}
