package httptaskmanagerserver;

import com.sun.net.httpserver.HttpExchange;
import taskmanager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            if (httpExchange.getRequestMethod().equals("GET") && path.equals("/history")) {
                sendText(httpExchange, gson.toJson(taskManager.getHistory()));
            } else {
                sendMethodNotAllowed(httpExchange, "Неправильное обращение к /history");
            }
        } catch (IOException e) {
            sendInternalServerError(httpExchange);
        }
    }

}
