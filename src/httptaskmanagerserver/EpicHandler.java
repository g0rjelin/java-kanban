package httptaskmanagerserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.NotFoundException;
import taskmanager.TaskManager;
import taskmodel.Epic;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

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
                case GET_EPICS:
                    sendText(httpExchange, gson.toJson(taskManager.getEpicsList()));
                    break;
                case GET_EPICS_ID:
                    sendText(httpExchange, gson.toJson(taskManager.getEpicById(getElementId(path))));
                    break;
                case POST_EPICS:
                    Epic parsedEpic = gson.fromJson(body, Epic.class);
                    Integer idAddEpic =
                            taskManager.addEpic(new Epic(parsedEpic.getName(), parsedEpic.getDescription()));
                    sendSuccessModify(httpExchange, String.format("Эпик успешно добавлен c id = %d", idAddEpic));
                    break;
                case GET_EPICS_ID_SUBTASKS:
                    Epic epic = taskManager.getEpicById(getElementId(path));
                    sendText(httpExchange, gson.toJson(taskManager.getSubtasksListByEpic(epic)));
                    break;
                case DELETE:
                    Integer idDeletedEpic = getElementId(path);
                    if (taskManager.getEpicById(idDeletedEpic) != null) {
                        taskManager.deleteEpicById(idDeletedEpic);
                        sendText(httpExchange, String.format("Эпик c id = %d успешно удален", idDeletedEpic));
                    } else {
                        throw new NotFoundException();
                    }
                    break;
                default:
                    sendNotFound(httpExchange, "Неправильное обращение к /epics");
                    break;
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, String.format("Эпик с id = %s не найден", e.getMessage()));
        } catch (IOException e) {
            sendInternalServerError(httpExchange);
        }
    }

    private EpicsEndpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2) {
            switch (requestMethod) {
                case "GET":
                    return EpicsEndpoint.GET_EPICS;
                case "POST":
                    return EpicsEndpoint.POST_EPICS;
                default:
                    return EpicsEndpoint.UNKNOWN;
            }
        } else if (pathParts.length == 3) {
            switch (requestMethod) {
                case "GET":
                    return EpicsEndpoint.GET_EPICS_ID;
                case "DELETE":
                    return EpicsEndpoint.DELETE;
                default:
                    return EpicsEndpoint.UNKNOWN;
            }
        } else if (pathParts.length == 4) {
            if (requestMethod.equals("GET") && pathParts[3].equals("subtasks")) {
                return EpicsEndpoint.GET_EPICS_ID_SUBTASKS;
            } else {
                return EpicsEndpoint.UNKNOWN;
            }
        } else {
            return EpicsEndpoint.UNKNOWN;
        }
    }


}
