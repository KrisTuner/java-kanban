package test;

import com.google.gson.Gson;
import manager.Managers;
import manager.TaskManager;
import manager.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.io.IOException;

public abstract class HttpTaskServerTestBase {
    protected TaskManager manager;
    protected HttpTaskServer taskServer;
    protected Gson gson;
    protected static final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    public void setUp() throws IOException {
        manager = Managers.getDefault();
        gson = Managers.getGson();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }
}