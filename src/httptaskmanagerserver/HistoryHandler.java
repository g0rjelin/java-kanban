package httptaskmanagerserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

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
                sendNotFound(httpExchange, "Неправильное обращение к /history");
            }
        } catch (IOException e) {
            sendInternalServerError(httpExchange);
        }
    }

}
