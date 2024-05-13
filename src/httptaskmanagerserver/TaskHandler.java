package httptaskmanagerserver;

import com.sun.net.httpserver.HttpExchange;
import taskmanager.NotFoundException;
import taskmanager.TaskManager;
import taskmodel.Task;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler {

    enum TasksEndpoint { GET_TASKS, GET_TASKS_ID, POST_TASKS, POST_TASKS_ID, DELETE, UNKNOWN }

    TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String body = getBodyAsString(httpExchange);
            switch (getEndpoint(path, httpExchange.getRequestMethod())) {
                case GET_TASKS -> sendText(httpExchange, gson.toJson(taskManager.getTasksList()));
                case GET_TASKS_ID -> sendText(httpExchange, gson.toJson(taskManager.getTaskById(getElementId(path))));
                case POST_TASKS -> {
                    Task parsedTask = gson.fromJson(body, Task.class);
                    Integer idAddTask = taskManager.addTask(
                            new Task(parsedTask.getName(), parsedTask.getDescription(), parsedTask.getStatus(),
                                    parsedTask.getDuration(), parsedTask.getStartTime()));
                    if (idAddTask != 0) {
                        sendSuccessModify(httpExchange, String.format("Задача успешно добавлена c id = %d", idAddTask));
                    } else {
                        sendHasInteractions(httpExchange,
                                "Добавляемая задача не может быть добавлена из-за пересечений с другими задачами");
                    }
                }
                case POST_TASKS_ID -> {
                    Task updTask = gson.fromJson(body, Task.class);
                    updTask.setId(getElementId(path));
                    Integer idUpdTask = taskManager.updateTask(updTask);
                    if (idUpdTask != 0) {
                        sendSuccessModify(httpExchange, String.format("Задача c id = %d успешно обновлена", idUpdTask));
                    } else {
                        sendHasInteractions(httpExchange,
                                "Задача не может быть обновлена из-за пересечений с другими задачами");
                    }
                }
                case DELETE -> {
                    Integer idDeletedTask = getElementId(path);
                    taskManager.deleteTaskById(idDeletedTask);
                    sendText(httpExchange, String.format("Задача c id = %d успешно удалена", idDeletedTask));
                }
                default -> sendMethodNotAllowed(httpExchange, "Неправильное обращение к /tasks");
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        } catch (IOException e) {
            sendInternalServerError(httpExchange);
        }
    }

    private TasksEndpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        return switch (requestMethod) {
            case "GET" -> pathParts.length == 3 ? TasksEndpoint.GET_TASKS_ID :
                    (pathParts.length == 2) ? TasksEndpoint.GET_TASKS : TasksEndpoint.UNKNOWN;
            case "POST" -> pathParts.length == 3 ? TasksEndpoint.POST_TASKS_ID :
                    (pathParts.length == 2) ? TasksEndpoint.POST_TASKS : TasksEndpoint.UNKNOWN;
            case "DELETE" -> pathParts.length == 3 ? TasksEndpoint.DELETE : TasksEndpoint.UNKNOWN;
            default -> TasksEndpoint.UNKNOWN;
        };
    }
}
