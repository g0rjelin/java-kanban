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

    Task getTaskById(Integer id) throws NotFoundException;

    Epic getEpicById(Integer id) throws NotFoundException;

    Subtask getSubtaskById(Integer id) throws NotFoundException;

    Integer addTask(Task newTask);

    Integer addEpic(Epic newEpic);

    Integer addSubtask(Subtask newSubtask);

    Integer updateTask(Task updTask) throws NotFoundException;

    Integer updateEpic(Epic updEpic) throws NotFoundException;

    Integer updateSubtask(Subtask updSubtask) throws NotFoundException;

    void deleteTaskById(Integer id);

    void deleteEpicById(Integer id);

    void deleteSubtaskById(Integer id);

    ArrayList<Subtask> getSubtasksListByEpic(Epic epic) throws NotFoundException;

    HistoryManager getHistoryManager();

    List<Task> getHistory();

    ArrayList<Task> getPrioritizedTasks();
}
