package taskmanager;

import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    ArrayList<Task> getTasksList();

    ArrayList<Epic> getEpicsList();

    ArrayList<Subtask> getSubtasksList();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    Task getTaskById(Integer id);

    Epic getEpicById(Integer id);

    Subtask getSubtaskById(Integer id);

    Integer addTask(Task newTask);

    Integer addEpic(Epic newEpic);

    Integer addSubtask(Subtask newSubtask);

    Integer updateTask(Task updTask);

    Integer updateEpic(Epic updEpic);

    Integer updateSubtask(Subtask updSubtask);

    void deleteTaskById(Integer id);

    void deleteEpicById(Integer id);

    void deleteSubtaskById(Integer id);

    ArrayList<Subtask> getSubtasksListByEpic(Epic epic);

    HistoryManager getHistoryManager();

    List<Task> getHistory();

    ArrayList<Task> getPrioritizedTasks();
}
