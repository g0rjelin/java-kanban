package httptaskmanagerserver;

import com.sun.net.httpserver.HttpExchange;
import taskmanager.NotFoundException;
import taskmanager.TaskManager;
import taskmodel.Subtask;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler {

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
                case GET_SUBTASKS -> sendText(httpExchange, gson.toJson(taskManager.getSubtasksList()));
                case GET_SUBTASKS_ID -> sendText(httpExchange,
                        gson.toJson(taskManager.getSubtaskById(getElementId(path))));
                case POST_SUBTASKS -> {
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
                }
                case POST_SUBTASKS_ID -> {
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
                }
                case DELETE -> {
                    Integer idDeletedSubtask = getElementId(path);
                    taskManager.deleteSubtaskById(idDeletedSubtask);
                    sendText(httpExchange, String.format("Подзадача c id = %d успешно удалена", idDeletedSubtask));
                }
                default -> sendMethodNotAllowed(httpExchange, "Неправильное обращение к /subtasks");
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        } catch (IOException e) {
            sendInternalServerError(httpExchange);
        }
    }

    private SubtasksEndpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        return switch (requestMethod) {
            case "GET" -> pathParts.length == 3 ? SubtasksEndpoint.GET_SUBTASKS_ID :
                    (pathParts.length == 2) ? SubtasksEndpoint.GET_SUBTASKS : SubtasksEndpoint.UNKNOWN;
            case "POST" -> pathParts.length == 3 ? SubtasksEndpoint.POST_SUBTASKS_ID :
                    (pathParts.length == 2) ? SubtasksEndpoint.POST_SUBTASKS : SubtasksEndpoint.UNKNOWN;
            case "DELETE" -> SubtasksEndpoint.DELETE;
            default -> SubtasksEndpoint.UNKNOWN;
        };
    }

}
