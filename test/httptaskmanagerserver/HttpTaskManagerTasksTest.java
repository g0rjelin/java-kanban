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

public class HttpTaskManagerTasksTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = TaskServerUtils.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
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
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5) ,   LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(201, response.statusCode(), "Код ответа при создании задачи должен быть 201");

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getTasksList();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test init name", "Testing update task",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Integer idTask = taskManager.addTask(task);

        //изменение имени задачи
        String updName = "Test new name";
        Task updateTask2 = new Task(task.getId(), updName, task.getDescription(), task.getStatus(), task.getDuration(), task.getStartTime());
        String taskJson = gson.toJson(updateTask2);

        //запрос на обновление задачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        Assertions.assertEquals(201, response.statusCode(), "Код ответа при обновлении задачи должен быть 201");

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getTasksList();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(updName, tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException, NotFoundException {
        Task task1 = new Task("Test 1 name", "Testing delete task",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Integer idTask1 = taskManager.addTask(task1);
        Task task2 = new Task("Test 2 name", "Testing delete task",
                TaskStatus.NEW, Duration.ofMinutes(5));
        Integer idTask2 = taskManager.addTask(task2);

        //запрос на удаление задачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        Assertions.assertEquals(200, response.statusCode(), "Код ответа при удалении задачи должен быть 200");

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getTasksList();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(taskManager.getTaskById(2),tasksFromManager.get(0), "Задача неправильно удалилась");
    }

    @Test
    public void testTriggerIntersectionWhileAdding() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1 task", "Testing intersection task 1",
                TaskStatus.NEW, Duration.ofMinutes(15) ,   LocalDateTime.of(2024, 1, 15, 12,0,0));
        Integer idTask1 = taskManager.addTask(task1);

        // создаём задачу, пересекающуюся с первой добавленной задачей
        Task task2 = new Task("Test 2 task with intersection", "Testing intersection task 1",
                TaskStatus.NEW, Duration.ofMinutes(15) ,   LocalDateTime.of(2024, 1, 15, 12,5,0));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        Assertions.assertEquals(406, response.statusCode(), "Код ответа при создании задачи, пересекающейся с другими, должен быть 406");

        // проверяем, что осталась созданной одна задача
        List<Task> tasksFromManager = taskManager.getTasksList();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Test 1 task", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testTriggerIntersectionWhileUpdating() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1 name", "Testing intersection task while updating",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 15, 12,5,0));
        Integer idTask1 = taskManager.addTask(task1);
        LocalDateTime task2StartTime = LocalDateTime.of(2024, 1, 15, 13,0,0);
        Task task2 = new Task("Test 2 name", "Testing intersection task while updating",
                TaskStatus.NEW, Duration.ofMinutes(30), task2StartTime);
        Integer idTask2 = taskManager.addTask(task2);

        //меняем время задачи так, чтобы она пересекалась с другой
        LocalDateTime updStartDate = LocalDateTime.of(2024, 1, 15, 12,10,0);
        Task updateTask2 = new Task(task2.getId(), task2.getName(), task2.getDescription(), task2.getStatus(), task2.getDuration(), updStartDate);
        String taskJson = gson.toJson(updateTask2);
        //запрос на обновление задачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode(), "Код ответа при обновлении задачи, пересекающейся с другими, должен быть 406");

        // проверяем, что осталась созданной одна задача
        List<Task> tasksFromManager = taskManager.getTasksList();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(task2StartTime, tasksFromManager.get(1).getStartTime(), "Задача не должна обновиться при наличии пересечения");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1 name", "Testing getting all tasks",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 15, 12,5,0));
        Integer idTask1 = taskManager.addTask(task1);
        Task task2 = new Task("Test 2 name", "Testing getting all tasks",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 15, 13,0,0));
        Integer idTask2 = taskManager.addTask(task2);

        //запрос на получение всх задач
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Код ответа при получении задач должен быть 200");

        List<Task> responseTasks = gson.fromJson(response.body(), new TaskServerUtils.TaskListTypeToken().getType());
        List<Task> tasksFromManager = taskManager.getTasksList();

        Assertions.assertEquals(tasksFromManager, responseTasks, "Список задач должен быть равен списку в менеджере");
    }

    @Test
    public void testGetTasksId() throws IOException, InterruptedException, NotFoundException {
        Task task1 = new Task("Test 1 name", "Testing getting task by id",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 15, 12, 5, 0));
        Integer idTask1 = taskManager.addTask(task1);
        Task task2 = new Task("Test 2 name", "Testing getting task by id",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 15, 13, 0, 0));
        Integer idTask2 = taskManager.addTask(task2);

        String taskJson = gson.toJson(task1);

        //запрос на получение задач по id
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Код ответа при получении задачи по Id должен быть 200");

        Task responseTask = gson.fromJson(response.body(), Task.class);
        Task taskFromManager = taskManager.getTaskById(1);

        Assertions.assertEquals(taskFromManager, responseTask, "Список задач должен быть равен списку в менеджере");
    }

    @Test
    public void testTriggerNotFound() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1 name", "Testing triggering not found",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 15, 12, 5, 0));
        Integer idTask1 = taskManager.addTask(task1);

        String taskJson = gson.toJson(task1);

        //запрос на получение задач по id - запрос к несуществующей задаче
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode(), "Код ответа при отсутствии запрошенной задачи по Id должен быть 404");

    }
}
