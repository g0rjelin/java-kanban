package httptaskmanagerserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            if (httpExchange.getRequestMethod().equals("GET") && path.equals("/prioritized")) {
                sendText(httpExchange, gson.toJson(taskManager.getPrioritizedTasks()));
            } else {
                sendNotFound(httpExchange, "Неправильное обращение к /prioritized");
            }
        } catch (IOException e) {
            sendInternalServerError(httpExchange);
        }
    }

}
