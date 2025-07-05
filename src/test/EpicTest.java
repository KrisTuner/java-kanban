package test;

import manager.Managers;
import manager.TaskManager;
import model.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для Epic")
class EpicTest {
    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
    }

    @Test
    @DisplayName("Сравнение эпиков по ID")
    void epicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic("Epic 1", "Description");
        Epic epic2 = new Epic("Epic 2", "Different description");

        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
    }

    @Test
    @DisplayName("Статус нового эпика без подзадач")
    void newEpicWithoutSubtasksShouldHaveNewStatus() {
        Epic epic = new Epic("Epic", "Description");
        manager.createEpic(epic);

        assertEquals("NEW", manager.getEpic(epic.getId()).getStatus().name(),
                "Новый эпик без подзадач должен иметь статус NEW");
    }
}