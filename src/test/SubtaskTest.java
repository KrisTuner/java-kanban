package test;

import model.Subtask;
import org.junit.jupiter.api.Test;

class SubtaskTest {
    @Test
    void subtasksWithSameIdShouldBeEqual() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description", 1);
        subtask1.setId(1);

        Subtask subtask2 = new Subtask("Subtask 2", "Different description", 2);
        subtask2.setId(1);
    }
}
