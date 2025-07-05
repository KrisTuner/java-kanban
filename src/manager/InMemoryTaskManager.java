package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import util.Status;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
    );
    protected int idCounter = 1;

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void createTask(Task task) {
        Objects.requireNonNull(task, "Задача не может быть null");
        if (task.getStartTime() == null || task.getDuration() == null) {
            throw new IllegalArgumentException("Задача должна иметь startTime и duration");
        }
        if (hasTimeOverlap(task)) {
            throw new ManagerSaveException("Задача пересекается по времени с существующей!", null);
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void createEpic(Epic epic) {
        Objects.requireNonNull(epic, "Эпик не может быть null");
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        Objects.requireNonNull(subtask, "Подзадача не может быть null");
        if (subtask.getStartTime() == null || subtask.getDuration() == null) {
            throw new IllegalArgumentException("Подзадача должна иметь startTime и duration");
        }
        if (subtask.getEpicId() == subtask.getId()) {
            throw new IllegalArgumentException("Подзадача не может быть своим же эпиком");
        }
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Эпик с id=" + subtask.getEpicId() + " не существует");
        }
        if (hasTimeOverlap(subtask)) {
            throw new ManagerSaveException("Подзадача пересекается по времени с существующей задачей!", null);
        }

        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic);
        updateEpicTime(epic);
        prioritizedTasks.add(subtask);
    }

    @Override
    public void updateTask(Task task) {
        Objects.requireNonNull(task, "Задача не может быть null");
        if (!tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Задача с id=" + task.getId() + " не существует");
        }
        if (task.getStartTime() == null || task.getDuration() == null) {
            throw new IllegalArgumentException("Задача должна иметь startTime и duration");
        }
        if (hasTimeOverlap(task)) {
            throw new ManagerSaveException("Обновлённая задача пересекается по времени с существующей!", null);
        }

        prioritizedTasks.removeIf(t -> t.getId() == task.getId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Objects.requireNonNull(subtask, "Подзадача не может быть null");
        if (!subtasks.containsKey(subtask.getId())) {
            throw new IllegalArgumentException("Подзадача с id=" + subtask.getId() + " не существует");
        }
        if (subtask.getStartTime() == null || subtask.getDuration() == null) {
            throw new IllegalArgumentException("Подзадача должна иметь startTime и duration");
        }
        if (hasTimeOverlap(subtask)) {
            throw new ManagerSaveException("Обновлённая подзадача пересекается по времени с существующей!", null);
        }

        Subtask savedSubtask = subtasks.get(subtask.getId());
        prioritizedTasks.removeIf(t -> t.getId() == subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);

        Epic epic = epics.get(savedSubtask.getEpicId());
        updateEpicStatus(epic);
        updateEpicTime(epic);
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            historyManager.remove(id);
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            getEpicSubtasks(id).forEach(subtask -> {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
            });
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtaskIds().remove((Integer) id);
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
            historyManager.remove(id);
            prioritizedTasks.remove(subtask);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с id=" + epicId + " не существует");
        }
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    protected int generateId() {
        return idCounter++;
    }

    protected void updateEpicStatus(Epic epic) {
        List<Subtask> subtasksList = getEpicSubtasks(epic.getId());
        if (subtasksList.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        long doneCount = subtasksList.stream()
                .filter(subtask -> subtask.getStatus() == Status.DONE)
                .count();

        long newCount = subtasksList.stream()
                .filter(subtask -> subtask.getStatus() == Status.NEW)
                .count();

        if (doneCount == subtasksList.size()) {
            epic.setStatus(Status.DONE);
        } else if (newCount == subtasksList.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    protected void updateEpicTime(Epic epic) {
        List<Subtask> subtasksList = getEpicSubtasks(epic.getId());
        if (subtasksList.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            return;
        }

        LocalDateTime earliest = null;
        LocalDateTime latest = null;
        Duration totalDuration = Duration.ZERO;

        for (Subtask subtask : subtasksList) {
            if (subtask.getStartTime() == null || subtask.getDuration() == null) {
                continue;
            }

            LocalDateTime start = subtask.getStartTime();
            LocalDateTime end = start.plus(subtask.getDuration());

            if (earliest == null || start.isBefore(earliest)) {
                earliest = start;
            }

            if (latest == null || end.isAfter(latest)) {
                latest = end;
            }

            totalDuration = totalDuration.plus(subtask.getDuration());
        }

        epic.setStartTime(earliest);
        epic.setDuration(totalDuration);
    }

    private boolean hasTimeOverlap(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return false;
        }
        return prioritizedTasks.stream()
                .filter(task -> task != null && task.getId() != newTask.getId())
                .filter(task -> task.getStartTime() != null && task.getDuration() != null)
                .anyMatch(task -> task.isTimeOverlapping(newTask));
    }
}