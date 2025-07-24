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

class HttpTaskManagerHistoryAndPrioritizedTest extends HttpTaskServerTestBase {

    @Test
    void testGetHistory() throws Exception {
        Task task = new Task("Test task", "Description",
                LocalDateTime.now(), Duration.ofMinutes(30));
        manager.createTask(task);
        manager.getTask(task.getId());

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/history"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(String.valueOf(task.getId())));
    }

    @Test
    void testGetPrioritized() throws Exception {
        Task task1 = new Task("Task 1", "Description",
                LocalDateTime.now(), Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", "Description",
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(30));
        manager.createTask(task1);
        manager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/prioritized"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task 1"));
        assertTrue(response.body().contains("Task 2"));
    }
}