package httptaskmanagerserver;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.InMemoryTaskManager;
import taskmanager.Managers;
import taskmanager.TaskManager;
import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManagerPrioritizedTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = TaskServerUtils.getGson();

    public HttpTaskManagerPrioritizedTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("Первая задача", "Пример запланированной задачи", Duration.ofMinutes(15), LocalDateTime.now());
        Integer idTask1 = taskManager.addTask(task1);
        Task task2 = new Task("Вторая задача", "Пример задачи в работе", TaskStatus.IN_PROGRESS, Duration.ofMinutes(15));
        Integer idTask2 = taskManager.addTask(task2);
        Epic epic1 = new Epic("Первый эпик", "Первое эпичное описание");
        Integer idEpic1 = taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Первая подзадачка", "Запланированная подзадача", TaskStatus.DONE, Duration.ofMinutes(15),
                LocalDateTime.parse("2024-01-01T01:00:00"), idEpic1);
        Integer idSubtask1 = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Вторая подзадачка", "Подзадача в работе", Duration.ofMinutes(15),
                LocalDateTime.parse("2024-02-01T12:00:00"), idEpic1);
        Integer idSubtask2 = taskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Третья подзадачка", "Подзадача в работе", Duration.ofMinutes(15), idEpic1);
        Integer idSubtask3 = taskManager.addSubtask(subtask3);

        //запрос на получение отсортированного списка приоритетных задач
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Код ответа при получении отсортированного списка задач должен быть 200");

        List<Task> responsePrioritized = gson.fromJson(response.body(), new TaskServerUtils.TaskListTypeToken().getType());
        List<Task> prioritizedFromManager = taskManager.getPrioritizedTasks();
        List<Integer> responsePrioritizedIds =  responsePrioritized.stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        List<Integer> prioritizedFromManagerIds =  prioritizedFromManager.stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        Assertions.assertEquals(prioritizedFromManagerIds, responsePrioritizedIds, "Отсортированный список задач из ендпоинта /prioritized должен соответствовать отсортированному списку в менеджере");
    }
}
