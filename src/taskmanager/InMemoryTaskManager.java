package taskmanager;

import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int idSeq = 0; //счетчик задач в менеджере

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    /**генерация id менеджером*/
    @Override
    public int getIdSeq() {
        return ++idSeq;
    }

    /**получение списка задач*/
    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    /**получение списка эпиков*/
    @Override
    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    /**получение списка подзадач*/
    @Override
    public ArrayList<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    /**получение менеджера истории*/
    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    /**получение истории*/
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    /**удаление всех задач*/
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    /**удаление всех эпиков*/
    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    /**удаление всех подзадач*/
    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic: epics.values()) {
            epic.setStatus(TaskStatus.NEW);
            epic.clearSubtaskIdList();
        }
    }

    /**получение задачи по идентификатору*/
    @Override
    public Task getTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    /**получение эпика по идентификатору*/
    @Override
    public Epic getEpicById(Integer id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        }
        return epics.get(id);
    }

    /**получение подзадачи по идентификатору*/
    @Override
    public Subtask getSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
        }
        return subtasks.get(id);
    }

    /**создание задачи*/
    @Override
    public Integer addTask(Task newTask) {
        newTask.setId(getIdSeq());
        tasks.put(newTask.getId(), newTask);
        return newTask.getId();
    }

    /**создание эпика*/
    @Override
    public Integer addEpic(Epic newEpic) {
        newEpic.setId(getIdSeq());
        epics.put(newEpic.getId(), newEpic);
        return newEpic.getId();
    }

    /**создание подзадачи*/
    @Override
    public Integer addSubtask(Subtask newSubtask) {
        if (epics.containsKey(newSubtask.getIdEpic())) {
            newSubtask.setId(getIdSeq());
            subtasks.put(newSubtask.getId(), newSubtask);
            Epic epicFromSubtask = getEpicById(newSubtask.getIdEpic());
            epicFromSubtask.addIdSubtask(newSubtask.getId());
            updateEpicStatus(epicFromSubtask);
        }
        return newSubtask.getId();
    }

    /**обновление задачи*/
    @Override
    public Integer updateTask(Task updTask) {
        if (tasks.containsKey(updTask.getId())) {
            tasks.put(updTask.getId(),updTask);
        }
        return updTask.getId();
    }

    /**обновление эпика*/
    @Override
    public Integer updateEpic(Epic updEpic) {
        if (epics.containsKey(updEpic.getId())) {
            epics.put(updEpic.getId(),updEpic);
            updateEpicStatus(updEpic);
        }
        return updEpic.getId();
    }

    /**обновление подзадачи*/
    @Override
    public Integer updateSubtask(Subtask updSubtask) {
        if (subtasks.containsKey(updSubtask.getId())) {
            subtasks.put(updSubtask.getId(),updSubtask);
            updateEpicStatus(getEpicById(updSubtask.getIdEpic()));
        }
        return updSubtask.getId();
    }

    /**удаление задачи*/
    @Override
    public void deleteTaskById(Integer id) {
            tasks.remove(id);
    }

    /**удаление эпика*/
    @Override
    public void deleteEpicById(Integer id) {
        Epic deletedEpic = epics.remove(id);
        if (deletedEpic == null) { //если объект не существовал - выходим из метода
            return;
        }
        //удаление связанных с эпиком подзадач
        for(Integer subtaskId : deletedEpic.getSubtaskIdList()) {
            subtasks.remove(subtaskId);
        }
    }

    /**удаление подзадачи*/
    @Override
    public void deleteSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            Subtask deletedSubtask = subtasks.remove(id);
            Epic epicOfRemovedSubtask = getEpicById(deletedSubtask.getIdEpic()); //эпик удаляемой подзадачи
            epicOfRemovedSubtask.deleteIdSubtask(id);
            updateEpicStatus(epicOfRemovedSubtask); //проверка статуса эпика после удаления подзадачи
        }
    }

    /**Получение списка всех подзадач определённого эпика*/
    @Override
    public ArrayList<Subtask> getSubtasksListByEpic(Epic epic) {
        if (subtasks.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        for (Integer idSubtask: epic.getSubtaskIdList()) {
            subtasksList.add(getSubtaskById(idSubtask));
        }
        return subtasksList;
    }

    /**обновление статуса эпика*/
    @Override
    public void updateEpicStatus(Epic epic) {
        if (epic.getSubtaskIdList().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            ArrayList<Subtask> subtasksList = getSubtasksListByEpic(epic);
            //проверяем все статусы подзадач (сначала 0, а потом с 1й в цикле)
            TaskStatus currentSubTaskStatus = subtasksList.get(0).getStatus();
            for (int i = 1; i < subtasksList.size(); i++) {
                if (subtasksList.get(i).getStatus() != currentSubTaskStatus) { //если статус хотя бы одной подзадачи отличается от других, то эпик в работе
                    currentSubTaskStatus = TaskStatus.IN_PROGRESS;
                    break;
                }
            }
            epic.setStatus(currentSubTaskStatus);
        }
    }


}
