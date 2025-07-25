package test;

import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerSubtasksTest extends HttpTaskServerTestBase {

    @Test
    void testCreateSubtask() throws Exception {
        Epic epic = new Epic("Test epic", "Description");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Test subtask", "Description",
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/subtasks"))
                        .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllSubtasks().size());
    }
}