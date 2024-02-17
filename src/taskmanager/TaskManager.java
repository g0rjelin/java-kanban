package taskmanager;

import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int idSeq = 0; //счетчик задач в менеджере

    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    /**генерация id менеджером*/
    int getIdSeq() {
        return idSeq++;
    }

    /**получение списка задач*/
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    /**получение списка эпиков*/
    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    /**получение списка подзадач*/
    public ArrayList<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    /**удаление всех задач*/
    public void removeAllTasks() {
        tasks.clear();
    }

    /**удаление всех эпиков*/
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    /**удаление всех подзадач*/
    public void removeAllSubtasks() {
        if (!subtasks.isEmpty()) {
            subtasks.clear();
        }
        for (Epic epic: epics.values()) {
            epic.setStatus(TaskStatus.NEW);
            epic.getSubtaskIdList().clear();
        }
    }

    /**получение задачи по идентификатору*/
    public Task getTaskById(Integer id) {
        return tasks.get(id);
    }

    /**получение эпика по идентификатору*/
    public Epic getEpicById(Integer id) {
        return epics.get(id);
    }

    /**получение подзадачи по идентификатору*/
    public Subtask getSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    /**создание задачи*/
    public Task addTask(Task newTask) {
        newTask.setId(getIdSeq());
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    /**создание эпика*/
    public Epic addEpic(Epic newEpic) {
        newEpic.setId(getIdSeq());
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    /**создание подзадачи*/
    public Subtask addSubtask(Subtask newSubtask) {
        if (epics.containsKey(newSubtask.getIdEpic())) {
            newSubtask.setId(getIdSeq());
            subtasks.put(newSubtask.getId(), newSubtask);
            Epic epicFromSubtask = getEpicById(newSubtask.getIdEpic());
            epicFromSubtask.getSubtaskIdList().add(newSubtask.getId());
            updateEpicStatus(epicFromSubtask);
        }
        return newSubtask;
    }

    /**обновление задачи*/
    public Task updateTask(Task updTask) {
        if (tasks.containsKey(updTask.getId())) {
            tasks.put(updTask.getId(),updTask);
        }
        return updTask;
    }

    /**обновление эпика*/
    public Epic updateEpic(Epic updEpic) {
        if (epics.containsKey(updEpic.getId())) {
            epics.put(updEpic.getId(),updEpic);
            updateEpicStatus(updEpic);
        }
        return updEpic;
    }

    /**обновление подзадачи*/
    public Subtask updateSubtask(Subtask updSubtask) {
        if (subtasks.containsKey(updSubtask.getId())) {
            subtasks.put(updSubtask.getId(),updSubtask);
            updateEpicStatus(getEpicById(updSubtask.getIdEpic()));
        }
        return updSubtask;
    }

    /**удаление задачи*/
    public void deleteTaskById(Integer id) {
            tasks.remove(id);
    }

    /**удаление эпика*/
    public void deleteEpicById(Integer id) {
        if (epics.containsKey(id)) {
            //удаление связанных с эпиком подзадач
            if (!getEpicById(id).getSubtaskIdList().isEmpty()) {
                for (Subtask subtask : getSubtasksListByEpic(getEpicById(id)) ) {
                    deleteSubtaskById(subtask.getId());
                }
            }
            epics.remove(id);
        }
    }

    /**удаление подзадачи*/
    public void deleteSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            Subtask deletedSubtask = subtasks.remove(id);
            Epic epicOfRemovedSubtask = getEpicById(deletedSubtask.getIdEpic()); //эпик удаляемой подзадачи
            epicOfRemovedSubtask.getSubtaskIdList().remove(id); //удаление id подзадачи из списка в эпике
            updateEpicStatus(epicOfRemovedSubtask); //проверка статуса эпика после удаления подзадачи
        }
    }

    /**Получение списка всех подзадач определённого эпика*/
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
    private void updateEpicStatus(Epic epic) {
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
