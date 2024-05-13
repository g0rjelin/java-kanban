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

public class HttpTaskManagerEpicsTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = TaskServerUtils.getGson();

    public HttpTaskManagerEpicsTest() throws IOException {
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
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic test", "Testing add epic");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request =
                HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode(), "Код ответа при создании эпика должен быть 201");

        // проверяем, что создался один эпик с корректным именем
        List<Epic> epicsFromManager = taskManager.getEpicsList();

        Assertions.assertNotNull(epicsFromManager, "Эпики не возвращаются");
        Assertions.assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        Assertions.assertEquals("Epic test", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException, NotFoundException {
        Epic epic = new Epic("Epic 1 test", "Testing add epic");
        Integer idEpic1 = taskManager.addEpic(epic);
        epic = new Epic("Epic 2 test", "Testing add epic");
        Integer idEpic2 = taskManager.addEpic(epic);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        //запрос на удаление задачи
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        Assertions.assertEquals(200, response.statusCode(), "Код ответа при удалении эпика должен быть 200");

        List<Epic> epicsFromManager = taskManager.getEpicsList();

        Assertions.assertNotNull(epicsFromManager, "Эпики не возвращаются");
        Assertions.assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        Assertions.assertEquals(taskManager.getEpicById(2), epicsFromManager.get(0), "Эпик неправильно удалился");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test 1 name", "Testing getting all epics");
        Integer idEpic1 = taskManager.addEpic(epic1);

        Subtask subtask1 =
                new Subtask("Первая подзадачка", "Запланированная подзадача", TaskStatus.DONE, Duration.ofMinutes(15),
                        LocalDateTime.parse("2024-01-01T01:00:00"), idEpic1);
        Integer idSubtask1 = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Вторая подзадачка", "Подзадача в работе", Duration.ofMinutes(15),
                LocalDateTime.parse("2024-02-01T12:00:00"), idEpic1);
        Integer idSubtask2 = taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("Test 2 name", "Testing getting all epics");
        Integer idEpic2 = taskManager.addEpic(epic1);

        //запрос на получение всех эпиков
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Код ответа при получении эпиков должен быть 200");

        List<Epic> responseEpics = gson.fromJson(response.body(), new TaskServerUtils.EpicListTypeToken().getType());
        List<Epic> epicsFromManager = taskManager.getEpicsList();

        Assertions.assertEquals(epicsFromManager, responseEpics, "Список эпиков должен быть равен списку в менеджере");
    }

    @Test
    public void testGetEpicsId() throws IOException, InterruptedException, NotFoundException {
        Epic epic1 = new Epic("Test 1 name", "Testing getting epic by id");
        Integer idEpic1 = taskManager.addEpic(epic1);
        Subtask subtask1 =
                new Subtask("Первая подзадачка", "Запланированная подзадача", TaskStatus.DONE, Duration.ofMinutes(15),
                        LocalDateTime.parse("2024-01-01T01:00:00"), idEpic1);
        Integer idSubtask1 = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Вторая подзадачка", "Подзадача в работе", Duration.ofMinutes(15),
                LocalDateTime.parse("2024-02-01T12:00:00"), idEpic1);
        Integer idSubtask2 = taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("Test 2 name", "Testing getting epic by id");
        Integer idEpic2 = taskManager.addEpic(epic2);

        //запрос на получение эпика по id
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request =HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Код ответа при получении эпика по Id должен быть 200");

        Epic responseEpic = gson.fromJson(response.body(), Epic.class);
        Epic epicFromManager = taskManager.getEpicById(1);

        Assertions.assertEquals(epicFromManager, responseEpic, "Список эпиков должен быть равен списку в менеджере");
    }

    @Test
    public void testGetSubtasksListByEpicId() throws IOException, InterruptedException, NotFoundException {
        Epic epic1 = new Epic("Test 1 name", "Testing getting subtask list by epic id");
        Integer idEpic1 = taskManager.addEpic(epic1);
        String epicJson = gson.toJson(epic1);

        Subtask subtask1 =
                new Subtask("Первая подзадачка", "Запланированная подзадача", TaskStatus.DONE, Duration.ofMinutes(15),
                        LocalDateTime.parse("2024-01-01T01:00:00"), idEpic1);
        Integer idSubtask1 = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Вторая подзадачка", "Подзадача в работе", Duration.ofMinutes(15),
                LocalDateTime.parse("2024-02-01T12:00:00"), idEpic1);
        Integer idSubtask2 = taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("Test 2 name", "Testing getting subtask list by epic by id");
        Integer idEpic2 = taskManager.addEpic(epic2);

        //запрос на получение списка подзадач эпика по id
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(),
                "Код ответа при получении списка подзадач эпика по Id должен быть 200");

        List<Subtask> responseSubtaskListByEpic =
                gson.fromJson(response.body(), new TaskServerUtils.SubtaskListTypeToken().getType());
        List<Subtask> subtaskListByEpicFromManager = taskManager.getSubtasksListByEpic(taskManager.getEpicById(1));

        Assertions.assertEquals(subtaskListByEpicFromManager, responseSubtaskListByEpic,
                "Список возвращаемых подзадач по id эпика должен быть равен списку подзадач эпика в менеджере");
    }


    @Test
    public void testTriggerNotFound() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test 1 name", "Testing triggering not found");
        Integer idEpic1 = taskManager.addEpic(epic1);

        //запрос на получение эпика по id - запрос к несуществующему эпику
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode(),
                "Код ответа при отсутствии запрошенного эпика по Id должен быть 404");

    }
}
