package test;

import model.Epic;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void epicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic("Epic 1", "Description");
        epic1.setId(1);

        Epic epic2 = new Epic("Epic 2", "Different description");
        epic2.setId(1);

        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
    }

    @Test
    void epicCannotAddItselfAsSubtask() {
        Epic epic = new Epic("Epic", "Description");
        epic.setId(1);

        assertThrows(
                IllegalArgumentException.class,
                () -> epic.addSubtaskId(epic.getId()),
                "Эпик не должен добавлять себя в качестве подзадачи");
    }
}