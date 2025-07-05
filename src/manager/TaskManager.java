package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    List<Task> getHistory();

    void deleteSubtaskById(int id);

    List<Task> getPrioritizedTasks();

    List<Subtask> getEpicSubtasks(int epicId);
}