package test;

import model.Task;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTasksTest extends HttpTaskServerTestBase {

    @Test
    void testCreateTask() throws Exception {
        Task task = new Task("Test task", "Description",
                LocalDateTime.now(), Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Task[] tasks = gson.fromJson(
                client.send(HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "/tasks"))
                                .GET().build(),
                        HttpResponse.BodyHandlers.ofString()).body(),
                Task[].class);

        assertEquals(1, tasks.length);
        assertEquals("Test task", tasks[0].getName());
    }

    @Test
    void testGetTaskById() throws Exception {
        Task task = new Task("Test task", "Description",
                LocalDateTime.now(), Duration.ofMinutes(30));
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/tasks/" + task.getId()))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task retrievedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task.getId(), retrievedTask.getId());
    }

    @Test
    void testDeleteTask() throws Exception {
        Task task = new Task("Test task", "Description",
                LocalDateTime.now(), Duration.ofMinutes(30));
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> deleteResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/tasks/" + task.getId()))
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, deleteResponse.statusCode());
        assertEquals(0, manager.getAllTasks().size());
    }
}