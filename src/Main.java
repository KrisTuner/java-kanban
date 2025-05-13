import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import util.Status;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = new Task("Переезд", "Собрать вещи");
        Task task2 = new Task("Купить продукты", "Купить хлеб, молоко и яйца");

        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Ремонт", "Сделать ремонт в комнате");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Покраска стен", "Покрасить стены в белый цвет", epic1.getId());
        Subtask subtask2 = new Subtask("Поменять пол", "Уложить ламинат", epic1.getId());

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Epic epic2 = new Epic("Подготовка к отпуску", "Собрать чемодан");
        manager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Купить билеты", "Купить авиабилеты", epic2.getId());
        manager.createSubtask(subtask3);

        System.out.println("Все задачи:");
        System.out.println(manager.getAllTasks());

        System.out.println("Все эпики:");
        System.out.println(manager.getAllEpics());

        System.out.println("Все подзадачи:");
        System.out.println(manager.getAllSubtasks());

        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        subtask3.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask3);

        System.out.println("После обновления статусов:");
        System.out.println("Все эпики:");
        System.out.println(manager.getAllEpics());

        manager.deleteTaskById(task2.getId());
        manager.deleteEpicById(epic2.getId());

        System.out.println("После удаления:");
        System.out.println("Все задачи:");
        System.out.println(manager.getAllTasks());
        System.out.println("Все эпики:");
        System.out.println(manager.getAllEpics());
        System.out.println("Все подзадачи:");
        System.out.println(manager.getAllSubtasks());
    }
}
