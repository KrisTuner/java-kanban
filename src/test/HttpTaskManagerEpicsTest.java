package test;

import model.Epic;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerEpicsTest extends HttpTaskServerTestBase {

    @Test
    void testCreateEpic() throws Exception {
        Epic epic = new Epic("Test epic", "Description");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/epics"))
                        .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllEpics().size());
    }

    @Test
    void testGetEpicSubtasks() throws Exception {
        Epic epic = new Epic("Test epic", "Description");
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/epics/" + epic.getId() + "/subtasks"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }
}