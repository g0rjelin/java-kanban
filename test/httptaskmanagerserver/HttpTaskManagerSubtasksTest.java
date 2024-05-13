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
import taskmodel.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskManagerSubtasksTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = TaskServerUtils.getGson();
    Epic epic;
    Integer idEpic1;

    public HttpTaskManagerSubtasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
        taskServer.start();
        epic = new Epic("Epic 1 test", "Test epic for subtasks testing");
        idEpic1 = taskManager.addEpic(epic);
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        // создаём подзадачу
        Subtask subtask = new Subtask("Test add subtask", "Testing add subtask",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), idEpic1);
        // конвертируем её в JSON
        String taskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request =
                HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание подзадач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(201, response.statusCode(), "Код ответа при создании подзадачи должен быть 201");

        // проверяем, что создалась одна подзадача с корректным именем
        List<Subtask> subtasksFromManager = taskManager.getSubtasksList();

        Assertions.assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        Assertions.assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        Assertions.assertEquals("Test add subtask", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Test init name", "Testing update subtask",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), idEpic1);
        Integer idSubtask = taskManager.addSubtask(subtask);

        //изменение имени подзадачи
        String updName = "Test new name";
        Subtask updateSubtask2 = new Subtask(subtask.getId(), updName, subtask.getDescription(), subtask.getStatus(),
                subtask.getDuration(), subtask.getStartTime(), subtask.getIdEpic());
        String subtaskJson = gson.toJson(updateSubtask2);

        //запрос на обновление подзадачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request =
                HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        Assertions.assertEquals(201, response.statusCode(), "Код ответа при обновлении подзадачи должен быть 201");

        // проверяем, что создалась одна подзадача с корректным именем
        List<Subtask> subtasksFromManager = taskManager.getSubtasksList();

        Assertions.assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        Assertions.assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        Assertions.assertEquals(updName, subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException, NotFoundException {
        Subtask subtask1 = new Subtask("Test 1 name", "Testing delete subtask",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), idEpic1);
        Integer idSubtask1 = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test 2 name", "Testing delete subtask",
                TaskStatus.NEW, Duration.ofMinutes(5), idEpic1);
        Integer idSubtask2 = taskManager.addSubtask(subtask2);

        //запрос на удаление подзадачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        Assertions.assertEquals(200, response.statusCode(), "Код ответа при удалении подзадачи должен быть 200");

        // проверяем, что создалась одна подзадача с корректным именем
        List<Subtask> subtasksFromManager = taskManager.getSubtasksList();

        Assertions.assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        Assertions.assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        Assertions.assertEquals(taskManager.getSubtaskById(3), subtasksFromManager.get(0),
                "Подзадача неправильно удалилась");
    }

    @Test
    public void testTriggerIntersectionWhileAdding() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Test 1 subtask", "Testing intersection subtask 1",
                TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 1, 15, 12, 0, 0), idEpic1);
        Integer idSubtask1 = taskManager.addSubtask(subtask1);

        // создаём подзадачу, пересекающуюся с первой добавленной подзадачей
        Subtask subtask2 = new Subtask("Test 2 subtask with intersection", "Testing intersection subtask 1",
                TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 1, 15, 12, 5, 0), idEpic1);
        // конвертируем её в JSON
        String subtaskJson = gson.toJson(subtask2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request =
                HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание подзадач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        Assertions.assertEquals(406, response.statusCode(),
                "Код ответа при создании подзадачи, пересекающейся с другими, должен быть 406");

        // проверяем, что осталась созданной одна подзадача
        List<Subtask> subtasksFromManager = taskManager.getSubtasksList();

        Assertions.assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Test 1 subtask", subtasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testTriggerIntersectionWhileUpdating() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Test 1 name", "Testing intersection task while updating",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 15, 12, 5, 0), idEpic1);
        Integer idSubtask1 = taskManager.addSubtask(subtask1);
        LocalDateTime subtask2StartTime = LocalDateTime.of(2024, 1, 15, 13, 0, 0);
        Subtask subtask2 = new Subtask("Test 2 name", "Testing intersection task while updating",
                TaskStatus.NEW, Duration.ofMinutes(30), subtask2StartTime, idEpic1);
        Integer idSubtask2 = taskManager.addSubtask(subtask2);

        //меняем время подзадачи так, чтобы она пересекалась с другой
        LocalDateTime updStartDate = LocalDateTime.of(2024, 1, 15, 12, 10, 0);
        Subtask updateSubtask2 =
                new Subtask(subtask2.getId(), subtask2.getName(), subtask2.getDescription(), subtask2.getStatus(),
                        subtask2.getDuration(), updStartDate, subtask2.getIdEpic());
        String subtaskJson = gson.toJson(updateSubtask2);
        //запрос на обновление подзадачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request =
                HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode(),
                "Код ответа при обновлении подзадачи, пересекающейся с другими, должен быть 406");

        // проверяем, что осталась созданной одна подзадача
        List<Subtask> subtasksFromManager = taskManager.getSubtasksList();

        Assertions.assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        Assertions.assertEquals(2, subtasksFromManager.size(), "Некорректное количество подзадач");
        Assertions.assertEquals(subtask2StartTime, subtasksFromManager.get(1).getStartTime(),
                "Подзадача не должна обновиться при наличии пересечения");
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Test 1 name", "Testing getting all subtasks",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 15, 12, 5, 0), idEpic1);
        Integer idSubtask1 = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test 2 name", "Testing getting all subtasks",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 15, 13, 0, 0), idEpic1);
        Integer idSubtask2 = taskManager.addSubtask(subtask2);

        //запрос на получение всех подзадач
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Код ответа при получении подзадач должен быть 200");

        List<Subtask> responseSubtasks =
                gson.fromJson(response.body(), new TaskServerUtils.SubtaskListTypeToken().getType());
        List<Subtask> subtasksFromManager = taskManager.getSubtasksList();

        Assertions.assertEquals(subtasksFromManager, responseSubtasks,
                "Список подзадач должен быть равен списку в менеджере");
    }

    @Test
    public void testGetSubtasksId() throws IOException, InterruptedException, NotFoundException {
        Subtask subtask1 = new Subtask("Test 1 name", "Testing getting subtask by id",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 15, 12, 5, 0), idEpic1);
        Integer idSubtask1 = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test 2 name", "Testing getting subtask by id",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 15, 13, 0, 0), idEpic1);
        Integer idSubtask2 = taskManager.addSubtask(subtask2);

        String taskJson = gson.toJson(subtask1);

        //запрос на получение подзадач по id
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Код ответа при получении подзадачи по Id должен быть 200");

        Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);
        Subtask subtaskFromManager = taskManager.getSubtaskById(2);

        Assertions.assertEquals(subtaskFromManager, responseSubtask,
                "Возвращенная по id подзадача должна быть равна подзадаче с этим же id в менеджере");
    }

    @Test
    public void testTriggerNotFound() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Test 1 name", "Testing triggering not found",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 15, 12, 5, 0), idEpic1);
        Integer idSubtask1 = taskManager.addSubtask(subtask1);

        //запрос на получение задач по id - запрос к несуществующей задаче
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode(),
                "Код ответа при отсутствии запрошенной подзадачи по Id должен быть 404");

    }
}
