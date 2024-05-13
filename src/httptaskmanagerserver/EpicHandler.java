package httptaskmanagerserver;

import com.sun.net.httpserver.HttpExchange;
import taskmanager.NotFoundException;
import taskmanager.TaskManager;
import taskmodel.Epic;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler {

    enum EpicsEndpoint { GET_EPICS, GET_EPICS_ID, POST_EPICS, GET_EPICS_ID_SUBTASKS, DELETE, UNKNOWN }

    EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String body = getBodyAsString(httpExchange);
            switch (getEndpoint(path, httpExchange.getRequestMethod())) {
                case GET_EPICS -> sendText(httpExchange, gson.toJson(taskManager.getEpicsList()));
                case GET_EPICS_ID -> sendText(httpExchange, gson.toJson(taskManager.getEpicById(getElementId(path))));
                case POST_EPICS -> {
                    Epic parsedEpic = gson.fromJson(body, Epic.class);
                    Integer idAddEpic =
                            taskManager.addEpic(new Epic(parsedEpic.getName(), parsedEpic.getDescription()));
                    sendSuccessModify(httpExchange, String.format("Эпик успешно добавлен c id = %d", idAddEpic));
                }
                case GET_EPICS_ID_SUBTASKS -> {
                    Epic epic = taskManager.getEpicById(getElementId(path));
                    sendText(httpExchange, gson.toJson(taskManager.getSubtasksListByEpic(epic)));
                }
                case DELETE -> {
                    Integer idDeletedEpic = getElementId(path);
                    taskManager.deleteEpicById(idDeletedEpic);
                    sendText(httpExchange, String.format("Эпик c id = %d успешно удален", idDeletedEpic));
                }
                default -> sendMethodNotAllowed(httpExchange, "Неправильное обращение к /epics");
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        } catch (IOException e) {
            sendInternalServerError(httpExchange);
        }
    }

    private EpicsEndpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        return switch (requestMethod) {
            case "GET" -> pathParts.length == 2 ? EpicsEndpoint.GET_EPICS :
                    pathParts.length == 3 ? EpicsEndpoint.GET_EPICS_ID :
                            pathParts.length == 4 && pathParts[3].equals("subtasks") ?
                                    EpicsEndpoint.GET_EPICS_ID_SUBTASKS : EpicsEndpoint.UNKNOWN;
            case "POST" -> pathParts.length == 2 ? EpicsEndpoint.POST_EPICS : EpicsEndpoint.UNKNOWN;
            case "DELETE" -> EpicsEndpoint.DELETE;
            default -> EpicsEndpoint.UNKNOWN;
        };
    }


}
