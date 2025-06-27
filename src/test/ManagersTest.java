package test;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты утилитного класса Managers")
class ManagersTest {
    @Test
    @DisplayName("Создание TaskManager")
    void shouldReturnInitializedTaskManager() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Должен возвращаться инициализированный менеджер");
    }

    @Test
    @DisplayName("Создание HistoryManager")
    void shouldReturnInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Должен возвращаться инициализированный менеджер истории");
    }
}