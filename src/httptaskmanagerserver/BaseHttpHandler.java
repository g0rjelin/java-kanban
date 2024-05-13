package httptaskmanagerserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanager.NotFoundException;
import taskmanager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected TaskManager taskManager;
    protected Gson gson = TaskServerUtils.getGson();

    BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected String getBodyAsString(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        return new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
    }

    protected Integer getElementId(String requestPath) throws NotFoundException {
        try {
            return Integer.parseInt(requestPath.split("/")[2]);
        } catch (NumberFormatException e) {
            throw new NotFoundException();
        }

    }

    protected void sendSuccessModify(HttpExchange h, String text) throws IOException {
        sendResponse(h, text, 201);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        sendResponse(h, text, 200);
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        sendResponse(h, text, 404);
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        sendResponse(h, text, 406);
    }

    private void sendResponse(HttpExchange h, String text, Integer statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(statusCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendInternalServerError(HttpExchange h) throws IOException {
        byte[] resp = "Ошибка на стороне сервера".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(500, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }
}
