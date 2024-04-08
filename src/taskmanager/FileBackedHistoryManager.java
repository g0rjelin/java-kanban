package taskmanager;

import taskmodel.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedHistoryManager extends InMemoryHistoryManager {
    static final String DEFAULT_HISTORY_MANAGER_PATH = "resources/history_manager.csv";

    private Path historyManagerPath;

    FileBackedHistoryManager() {
        super();
        historyManagerPath = Paths.get(DEFAULT_HISTORY_MANAGER_PATH);
    }

    FileBackedHistoryManager(Path historyManagerPath) {
        this();
        this.historyManagerPath = historyManagerPath;
    }

    @Override
    public void add(Task task) {
        super.add(task);
        save();
    }

    @Override
    public void remove(int id) {
        super.remove(id);
        save();
    }

    private void save() {
        try {
            if (!Files.exists(historyManagerPath)) {
                Files.createFile(historyManagerPath);
            }
            Writer historyManagerFileWriter = new FileWriter(String.valueOf(historyManagerPath));
            String historyString = historyToString(this);
            historyManagerFileWriter.write(historyString);
            historyManagerFileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении истории просмотров задач");
        }
    }

    static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        String[] historyIdArray = new String[history.size()];
        int count = 0;
        for (Task task : history) {
            historyIdArray[count] = String.valueOf(task.getId());
            count++;
        }
        return String.join(",", historyIdArray);
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> listId = new ArrayList<>();
        for (String strId : value.split(",")) {
            if (strId.length() > 0) {
                listId.add(Integer.valueOf(strId));
            }
        }
        return listId;
    }
}
