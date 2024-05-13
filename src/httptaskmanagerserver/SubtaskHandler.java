package httptaskmanagerserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.NotFoundException;
import taskmanager.TaskManager;
import taskmodel.Subtask;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    enum SubtasksEndpoint { GET_SUBTASKS, GET_SUBTASKS_ID, POST_SUBTASKS, POST_SUBTASKS_ID, DELETE, UNKNOWN }

    SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String body = getBodyAsString(httpExchange);
            switch (getEndpoint(path, httpExchange.getRequestMethod())) {
                case GET_SUBTASKS:
                    sendText(httpExchange, gson.toJson(taskManager.getSubtasksList()));
                    break;
                case GET_SUBTASKS_ID:
                    sendText(httpExchange, gson.toJson(taskManager.getSubtaskById(getElementId(path))));
                    break;
                case POST_SUBTASKS:
                    Subtask parsedSubtask = gson.fromJson(body, Subtask.class);
                    Integer idAddSubtask = taskManager.addSubtask(
                            new Subtask(parsedSubtask.getName(), parsedSubtask.getDescription(),
                                    parsedSubtask.getStatus(), parsedSubtask.getDuration(),
                                    parsedSubtask.getStartTime(), parsedSubtask.getIdEpic()));
                    if (idAddSubtask != 0) {
                        sendSuccessModify(httpExchange,
                                String.format("Подзадача успешно добавлена c id = %d", idAddSubtask));
                    } else {
                        sendHasInteractions(httpExchange,
                                "Добавляемая подзадача не может быть добавлена из-за пересечений с другими задачами");
                    }
                    break;
                case POST_SUBTASKS_ID:
                    Subtask updSubtask = gson.fromJson(body, Subtask.class);
                    updSubtask.setId(getElementId(path));
                    Integer idUpdSubtask = taskManager.updateSubtask(updSubtask);
                    if (idUpdSubtask != 0) {
                        sendSuccessModify(httpExchange,
                                String.format("Подзадача c id = %d успешно обновлена", idUpdSubtask));
                    } else {
                        sendHasInteractions(httpExchange,
                                "Подзадача не может быть обновлена из-за пересечений с другими задачами");
                    }
                    break;
                case DELETE:
                    Integer idDeletedSubtask = getElementId(path);
                    if (taskManager.getSubtaskById(idDeletedSubtask) != null) {
                        taskManager.deleteSubtaskById(idDeletedSubtask);
                        sendText(httpExchange, String.format("Подзадача c id = %d успешно удалена", idDeletedSubtask));
                    } else {
                        throw new NotFoundException();
                    }
                    break;
                default:
                    sendNotFound(httpExchange, "Неправильное обращение к /subtasks");
                    break;
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, String.format("Подзадача с id = %s не найдена", e.getMessage()));
        } catch (IOException e) {
            sendInternalServerError(httpExchange);
        }
    }

    private SubtasksEndpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2) {
            switch (requestMethod) {
                case "GET":
                    return SubtasksEndpoint.GET_SUBTASKS;
                case "POST":
                    return SubtasksEndpoint.POST_SUBTASKS;
                default:
                    return SubtasksEndpoint.UNKNOWN;
            }
        } else if (pathParts.length == 3) {
            switch (requestMethod) {
                case "GET":
                    return SubtasksEndpoint.GET_SUBTASKS_ID;
                case "POST":
                    return SubtasksEndpoint.POST_SUBTASKS_ID;
                case "DELETE":
                    return SubtasksEndpoint.DELETE;
                default:
                    return SubtasksEndpoint.UNKNOWN;
            }
        } else {
            return SubtasksEndpoint.UNKNOWN;
        }
    }

}
