package httptaskmanagerserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.NotFoundException;
import taskmanager.TaskManager;
import taskmodel.Task;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

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
                case GET_TASKS:
                    sendText(httpExchange, gson.toJson(taskManager.getTasksList()));
                    break;
                case GET_TASKS_ID:
                    sendText(httpExchange, gson.toJson(taskManager.getTaskById(getElementId(path))));
                    break;
                case POST_TASKS:
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
                    break;
                case POST_TASKS_ID:
                    Task updTask = gson.fromJson(body, Task.class);
                    updTask.setId(getElementId(path));
                    Integer idUpdTask = taskManager.updateTask(updTask);
                    if (idUpdTask != 0) {
                        sendSuccessModify(httpExchange, String.format("Задача c id = %d успешно обновлена", idUpdTask));
                    } else {
                        sendHasInteractions(httpExchange,
                                "Задача не может быть обновлена из-за пересечений с другими задачами");
                    }
                    break;
                case DELETE:
                    Integer idDeletedTask = getElementId(path);
                    if (taskManager.getTaskById(idDeletedTask) != null) {
                        taskManager.deleteTaskById(idDeletedTask);
                        sendText(httpExchange, String.format("Задача c id = %d успешно удалена", idDeletedTask));
                    } else {
                        throw new NotFoundException();
                    }
                    break;
                default:
                    sendNotFound(httpExchange, "Неправильное обращение к /tasks");
                    break;
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, String.format("Задача с id = %s не найдена", e.getMessage()));
        } catch (IOException e) {
            sendInternalServerError(httpExchange);
        }
    }

    private TasksEndpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2) {
            switch (requestMethod) {
                case "GET":
                    return TasksEndpoint.GET_TASKS;
                case "POST":
                    return TasksEndpoint.POST_TASKS;
                default:
                    return TasksEndpoint.UNKNOWN;
            }
        } else if (pathParts.length == 3) {
            switch (requestMethod) {
                case "GET":
                    return TasksEndpoint.GET_TASKS_ID;
                case "POST":
                    return TasksEndpoint.POST_TASKS_ID;
                case "DELETE":
                    return TasksEndpoint.DELETE;
                default:
                    return TasksEndpoint.UNKNOWN;
            }
        } else {
            return TasksEndpoint.UNKNOWN;
        }
    }

}
