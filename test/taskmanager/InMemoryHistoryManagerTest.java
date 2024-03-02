package taskmanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void add() {
        Task task = new Task("TestHistory addNewTask", "Test addNewTask description", TaskStatus.NEW);
        final int taskId = taskManager.addTask(task);

        HistoryManager historyManager = taskManager.getHistoryManager();

        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    /**задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных*/
    @Test
    public void shouldPreserveTaskPrevVersionInHistory() {
        String initName = "Test task Version 1";
        String initDescription = "Test task Version 1 Description";
        TaskStatus initTaskStatus = TaskStatus.NEW;
        Task task = new Task(initName, initDescription, initTaskStatus);
        final int taskId = taskManager.addTask(task);

        //обращение к задаче для внесения е в историю
        taskManager.getTaskById(taskId);
        //внесение изменений в задачу
        Task changedTask = new Task( task.getId(), "Test task Version 2", "Test task Version 2 Description", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(changedTask);
        //обращение к задаче после внесения ее в историю
        taskManager.getTaskById(taskId);

        Assertions.assertEquals( taskId, taskManager.getHistory().get(0).getId(), "Id задачи при сохранении в истории должно сохраниться");
        Assertions.assertEquals( initName, taskManager.getHistory().get(0).getName(), "В истории должно остаться первоначальное наименование задачи!");
        Assertions.assertEquals( initDescription, taskManager.getHistory().get(0).getDescription(), "В истории должно остаться первоначальное описание задачи!");
        Assertions.assertEquals( initTaskStatus, taskManager.getHistory().get(0).getStatus(), "В истории должен остаться первоначальный статус задачи!");
    }

    /**эпики, добавляемые в HistoryManager, сохраняют предыдущую версию эпика и её данных*/
    @Test
    public void shouldPreserveEpicPrevVersionInHistory() {
        String initName = "Test epic Version 1";
        String initDescription = "Test epic Version 1 Description";
        Epic epic = new Epic(initName, initDescription);
        int epicId = taskManager.addEpic(epic);
        //обращение к задачам для внесения их в историю
        taskManager.getEpicById(epicId);
        Subtask subtask = new Subtask("Test subtask1", "Test subtask1 Description", TaskStatus.NEW, epic.getId());
        int subtaskId = taskManager.addSubtask(subtask);
        TaskStatus initEpicStatus = epic.getStatus();


        //внесение изменений в задачи
        Subtask subtaskAdditional = new Subtask("Test subtask2", "Test subtask2 Description", TaskStatus.DONE, epic.getId());
        Epic changedEpic = new Epic(epic.getId(), "Test epic Version 2", "Test epic Version 2 Description", epic.getSubtaskIdList());
        taskManager.updateEpic(changedEpic);
        taskManager.addSubtask(subtaskAdditional);
        Subtask changedSubtask = new Subtask(subtask.getId(), "Test subtask Version 2", "Test subtask Version 2 Description", TaskStatus.DONE, subtask.getIdEpic());
        taskManager.updateSubtask(changedSubtask); //изменение приведет к смене статуса эпика
        //обращение к эпику после внесения изменений в историю
        taskManager.getEpicById(epicId);

        Assertions.assertEquals( epicId, taskManager.getHistory().get(0).getId(), "Id эпика при сохранении в истории должно сохраниться");
        Assertions.assertEquals( initName, taskManager.getHistory().get(0).getName(), "В истории должно остаться первоначальное наименование эпика!");
        Assertions.assertEquals( initDescription, taskManager.getHistory().get(0).getDescription(), "В истории должно остаться первоначальное описание эпика!");
        Assertions.assertEquals( initEpicStatus, taskManager.getHistory().get(0).getStatus(), "В истории должен остаться первоначальный статус эпика!");
    }

    /**подзадачи, добавляемые в HistoryManager, сохраняют предыдущую версию подзадачи и её данных*/
    @Test
    public void shouldPreserveSubtaskPrevVersionInHistory() {
        Epic epic = new Epic("Epic Name for Subtask Test", "Epic Description for Subtask Test");
        int epicId = taskManager.addEpic(epic);
        String initName = "Test subtask Version 1";
        String initDescription = "Test subtask Version 1 Description";
        TaskStatus initSubtaskStatus = TaskStatus.NEW;
        Subtask subtask = new Subtask(initName, initDescription, initSubtaskStatus, epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        //обращение к задаче для внесения в историю
        taskManager.getSubtaskById(subtaskId);
        //внесение изменений в подзадачу
        Epic newEpic = new Epic("NewEpic Name for Subtask Test", "NewEpic Description for Subtask Test");
        int newEpicId = taskManager.addEpic(newEpic);
        Subtask changedSubtask = new Subtask( subtask.getId(), "Test subtask Version 2", "Test subtask Version 2 Description", TaskStatus.IN_PROGRESS, newEpicId);
        taskManager.updateSubtask(changedSubtask);
        //обращение к задаче после внесения ее в историю
        taskManager.getSubtaskById(subtaskId);

        Assertions.assertEquals( subtaskId, taskManager.getHistory().get(1).getId(), "Id подзадачи при сохранении в истории должно сохраниться");
        Assertions.assertEquals( initName, taskManager.getHistory().get(1).getName(), "В истории должно остаться первоначальное наименование подзадачи!");
        Assertions.assertEquals( initDescription, taskManager.getHistory().get(1).getDescription(), "В истории должно остаться первоначальное описание подзадачи!");
        Assertions.assertEquals( initSubtaskStatus, taskManager.getHistory().get(1).getStatus(), "В истории должен остаться первоначальный статус подзадачи!");
        Subtask subtaskFromHistory = (Subtask) taskManager.getHistory().get(1);
        Assertions.assertEquals( epicId, subtaskFromHistory.getIdEpic(), "В истории должен остаться первоначальный эпик подзадачи!");
    }
}