package taskmanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class FileBackedHistoryManagerTest {
    HistoryManager historyManager;
    Path testHistoryManagerPath;

    @BeforeEach
    void setUp() throws IOException {
        testHistoryManagerPath = File.createTempFile(String.format("FileBackedHistoryManager_%s", UUID.randomUUID().toString() ) ,".csv").toPath();
        historyManager = new FileBackedHistoryManager(testHistoryManagerPath);
        int idAdd1 = 1;
        Task task = new Task(idAdd1, "FileBackedHistory Task",
                "FileBackedHistory task description", TaskStatus.NEW);
        int idAdd2 = 2;
        Epic epic = new Epic(idAdd2, "FileBackedHistory Epic", "FileBackedHistory Epic description", new ArrayList<>());

        int idAdd3 = 3;
        Subtask subtask = new Subtask(idAdd3, "FileBackedHistory Subtask",
                "FileBackedHistory subtask description", TaskStatus.NEW, idAdd2);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

    }

    @Test
    void shouldCorrectlySendToString() {
        Assertions.assertEquals("1,2,3", FileBackedHistoryManager.historyToString(historyManager),
                "Неправильное преобразование истории в строку");

        historyManager = new FileBackedHistoryManager();
        Assertions.assertEquals("", FileBackedHistoryManager.historyToString(historyManager),
                "Неправильное преобразовании пустой истории в строку");
    }

    @Test
    void shouldCorrectlyParseStringToHistory() {
        String historyString = "1,2,3";
        Integer[] historyId = {1, 2, 3};
        List<Integer> historyIdList = FileBackedHistoryManager.historyFromString(historyString);

        Assertions.assertArrayEquals(historyId, historyIdList.toArray(),
                "Неправильное преобразование строки в историю");

        historyString = "";
        historyIdList = FileBackedHistoryManager.historyFromString(historyString);
        Assertions.assertEquals(0, historyIdList.size(),
                "Неправильное преобразование пустой строки в историю");
    }

    @Test
    void shouldCorrectlySaveHistoryToFile() throws IOException {
        String historyString = Files.readString(testHistoryManagerPath);

        Assertions.assertEquals("1,2,3", historyString, "Неправильное сохранение истории в файл");
    }
}