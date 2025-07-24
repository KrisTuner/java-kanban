package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Subtask;
import model.Task;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.taskManager = manager;
        this.gson = Managers.getGson();
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", new RootHandler());
        server.createContext("/tasks", new TasksHandler());
        server.createContext("/subtasks", new SubtasksHandler());
        server.createContext("/epics", new EpicsHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritized", new PrioritizedHandler());
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту");
    }

    @SuppressWarnings("unused")
    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }

    private class BaseHttpHandler {
        protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(statusCode, resp.length);
            exchange.getResponseBody().write(resp);
            exchange.close();
        }

        protected void sendSuccess(HttpExchange exchange, String text) throws IOException {
            sendText(exchange, text, 200);
        }

        protected void sendCreated(HttpExchange exchange, String text) throws IOException {
            sendText(exchange, text, 201);
        }

        protected void sendNotFound(HttpExchange exchange) throws IOException {
            sendText(exchange, "Объект не найден", 404);
        }

        protected void sendHasInteractions(HttpExchange exchange) throws IOException {
            sendText(exchange, "Задача пересекается по времени с существующей", 406);
        }

        protected void sendInternalError(HttpExchange exchange) throws IOException {
            sendText(exchange, "Внутренняя ошибка сервера", 500);
        }

        protected <T> Optional<T> parseRequestBody(HttpExchange exchange, Class<T> clazz) {
            try (InputStream inputStream = exchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                return Optional.ofNullable(gson.fromJson(body, clazz));
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }

    private class TasksHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String[] pathParts = path.split("/");

                switch (method) {
                    case "GET":
                        if (pathParts.length == 2) {
                            String response = gson.toJson(taskManager.getAllTasks());
                            sendSuccess(exchange, response);
                        } else if (pathParts.length == 3) {
                            try {
                                int id = Integer.parseInt(pathParts[2]);
                                Task task = taskManager.getTask(id);
                                if (task != null) {
                                    sendSuccess(exchange, gson.toJson(task));
                                } else {
                                    sendNotFound(exchange);
                                }
                            } catch (NumberFormatException e) {
                                sendNotFound(exchange);
                            }
                        } else {
                            sendNotFound(exchange);
                        }
                        break;
                    case "POST":
                        Optional<Task> taskOptional = parseRequestBody(exchange, Task.class);
                        if (taskOptional.isPresent()) {
                            Task task = taskOptional.get();
                            try {
                                if (task.getId() == 0) {
                                    taskManager.createTask(task);
                                    sendCreated(exchange, "Задача создана");
                                } else {
                                    taskManager.updateTask(task);
                                    sendCreated(exchange, "Задача обновлена");
                                }
                            } catch (IllegalArgumentException e) {
                                sendHasInteractions(exchange);
                            }
                        } else {
                            sendNotFound(exchange);
                        }
                        break;
                    case "DELETE":
                        if (pathParts.length == 3) {
                            try {
                                int id = Integer.parseInt(pathParts[2]);
                                taskManager.deleteTaskById(id);
                                sendSuccess(exchange, "Задача удалена");
                            } catch (NumberFormatException e) {
                                sendNotFound(exchange);
                            }
                        } else {
                            sendNotFound(exchange);
                        }
                        break;
                    default:
                        sendNotFound(exchange);
                        break;
                }
            } catch (Exception e) {
                sendInternalError(exchange);
            }
        }
    }

    private class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String[] pathParts = path.split("/");

                switch (method) {
                    case "GET":
                        if (pathParts.length == 2) {
                            String response = gson.toJson(taskManager.getAllSubtasks());
                            sendSuccess(exchange, response);
                        } else if (pathParts.length == 3) {
                            try {
                                int id = Integer.parseInt(pathParts[2]);
                                Subtask subtask = taskManager.getSubtask(id);
                                if (subtask != null) {
                                    sendSuccess(exchange, gson.toJson(subtask));
                                } else {
                                    sendNotFound(exchange);
                                }
                            } catch (NumberFormatException e) {
                                sendNotFound(exchange);
                            }
                        } else {
                            sendNotFound(exchange);
                        }
                        break;
                    case "POST":
                        Optional<Subtask> subtaskOptional = parseRequestBody(exchange, Subtask.class);
                        if (subtaskOptional.isPresent()) {
                            Subtask subtask = subtaskOptional.get();
                            try {
                                if (subtask.getId() == 0) {
                                    taskManager.createSubtask(subtask);
                                    sendCreated(exchange, "Подзадача создана");
                                } else {
                                    taskManager.updateSubtask(subtask);
                                    sendCreated(exchange, "Подзадача обновлена");
                                }
                            } catch (IllegalArgumentException e) {
                                sendHasInteractions(exchange);
                            }
                        } else {
                            sendNotFound(exchange);
                        }
                        break;
                    case "DELETE":
                        if (pathParts.length == 3) {
                            try {
                                int id = Integer.parseInt(pathParts[2]);
                                taskManager.deleteSubtaskById(id);
                                sendSuccess(exchange, "Подзадача удалена");
                            } catch (NumberFormatException e) {
                                sendNotFound(exchange);
                            }
                        } else {
                            sendNotFound(exchange);
                        }
                        break;
                    default:
                        sendNotFound(exchange);
                        break;
                }
            } catch (Exception e) {
                sendInternalError(exchange);
            }
        }
    }

    private class EpicsHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String[] pathParts = path.split("/");

                switch (method) {
                    case "GET":
                        if (pathParts.length == 2) {
                            String response = gson.toJson(taskManager.getAllEpics());
                            sendSuccess(exchange, response);
                        } else if (pathParts.length == 3) {
                            if (pathParts[2].equals("subtasks")) {
                                String response = gson.toJson(taskManager.getAllSubtasks());
                                sendSuccess(exchange, response);
                            } else {
                                try {
                                    int id = Integer.parseInt(pathParts[2]);
                                    Epic epic = taskManager.getEpic(id);
                                    if (epic != null) {
                                        sendSuccess(exchange, gson.toJson(epic));
                                    } else {
                                        sendNotFound(exchange);
                                    }
                                } catch (NumberFormatException e) {
                                    sendNotFound(exchange);
                                }
                            }
                        } else if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                            try {
                                int epicId = Integer.parseInt(pathParts[2]);
                                String response = gson.toJson(taskManager.getEpicSubtasks(epicId));
                                sendSuccess(exchange, response);
                            } catch (NumberFormatException e) {
                                sendNotFound(exchange);
                            }
                        } else {
                            sendNotFound(exchange);
                        }
                        break;
                    case "POST":
                        Optional<Epic> epicOptional = parseRequestBody(exchange, Epic.class);
                        if (epicOptional.isPresent()) {
                            Epic epic = epicOptional.get();
                            if (epic.getId() == 0) {
                                taskManager.createEpic(epic);
                                sendCreated(exchange, "Эпик создан");
                            } else {
                                taskManager.updateEpic(epic);
                                sendCreated(exchange, "Эпик обновлен");
                            }
                        } else {
                            sendNotFound(exchange);
                        }
                        break;
                    case "DELETE":
                        if (pathParts.length == 3) {
                            try {
                                int id = Integer.parseInt(pathParts[2]);
                                taskManager.deleteEpicById(id);
                                sendSuccess(exchange, "Эпик удален");
                            } catch (NumberFormatException e) {
                                sendNotFound(exchange);
                            }
                        } else {
                            sendNotFound(exchange);
                        }
                        break;
                    default:
                        sendNotFound(exchange);
                        break;
                }
            } catch (Exception e) {
                sendInternalError(exchange);
            }
        }
    }

    private class HistoryHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("GET".equals(exchange.getRequestMethod())) {
                    String response = gson.toJson(taskManager.getHistory());
                    sendSuccess(exchange, response);
                } else {
                    sendNotFound(exchange);
                }
            } catch (Exception e) {
                sendInternalError(exchange);
            }
        }
    }

    private class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("GET".equals(exchange.getRequestMethod())) {
                    String response = gson.toJson(taskManager.getPrioritizedTasks());
                    sendSuccess(exchange, response);
                } else {
                    sendNotFound(exchange);
                }
            } catch (Exception e) {
                sendInternalError(exchange);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }

    private class RootHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendNotFound(exchange);
                return;
            }

            String response = """
                    Доступные эндпоинты:
                    GET /tasks - Список всех задач
                    GET /subtasks - Список всех подзадач
                    GET /epics - Список всех эпиков
                    GET /history - История просмотров
                    GET /prioritized - Приоритетные задачи
                    """;

            sendSuccess(exchange, response);
        }
    }
}