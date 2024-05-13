package httptaskmanagerserver;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.InMemoryTaskManager;
import taskmanager.Managers;
import taskmanager.NotFoundException;
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

public class HttpTaskManagerHistoryTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = TaskServerUtils.getGson();

    public HttpTaskManagerHistoryTest() throws IOException {
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
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Первая задача", "Пример запланированной задачи", Duration.ofMinutes(15));
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

        Epic epic2 = new Epic("Второй эпик", "Второе эпичное описание");
        Integer idEpic2 = taskManager.addEpic(epic2);

        taskManager.getTaskById(idTask1);
        taskManager.getTaskById(idTask2);
        taskManager.getEpicById(idEpic1);
        taskManager.getSubtaskById(idSubtask1);
        taskManager.getSubtaskById(idSubtask2);
        taskManager.getSubtaskById(idSubtask3);
        taskManager.getEpicById(idEpic2);

        //запрос на получение истории просмотра задач
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Код ответа при получении истории просмотра задач должен быть 200");

        List<Task> responseHistory = gson.fromJson(response.body(), new TaskServerUtils.TaskListTypeToken().getType());
        List<Task> historyFromManager = taskManager.getHistory();
        List<Integer> responseHistoryIds =  responseHistory.stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        List<Integer> historyFromManagerIds =  historyFromManager.stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        Assertions.assertEquals(historyFromManagerIds, responseHistoryIds, "История просмотра задач из ендпоинта /history должна соответствовать истории в менеджере");
    }
}
