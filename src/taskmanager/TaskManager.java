package taskmanager;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    public static int idSeq = 0; //счетчик задач в менеджере

    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    /**генерация id менеджером*/
    public static int getIdSeq() {
        return idSeq++;
    }

    /**получение списка задач*/
    public ArrayList<Task> getTasksList() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Integer idTask : tasks.keySet()) {
            tasksList.add(tasks.get(idTask));
        }
        return tasksList;
    }

    /**получение списка эпиков*/
    public ArrayList<Epic> getEpicsList() {
        ArrayList<Epic> epicsList = new ArrayList<>();
        for (Integer idEpic : epics.keySet()) {
            epicsList.add(epics.get(idEpic));
        }
        return epicsList;
    }

    /**получение списка подзадач*/
    public ArrayList<Subtask> getSubtasksList() {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        for (Integer idSubtask : subtasks.keySet()) {
            subtasksList.add(subtasks.get(idSubtask));
        }
        return subtasksList;
    }

    /**удаление всех задач*/
    public void removeAllTasks() {
        if (!tasks.isEmpty()) {
            for (Integer idTask : tasks.keySet()) {
                tasks.remove(idTask);
            }
            System.out.println("Все задачи удалены из менеджера");
        }
    }

    /**удаление всех эпиков*/
    public void removeAllEpics() {
        if (!epics.isEmpty()) {
            for (Integer idEpic : epics.keySet()) {
                epics.remove(idEpic);
            }
            System.out.println("Все эпики удалены из менеджера");
        }
    }

    /**удаление всех подзадач*/
    public void removeAllSubtasks() {
        if (!subtasks.isEmpty()) {
            for (Integer idSubtask : subtasks.keySet()) {
                subtasks.remove(idSubtask);
            }
            for (Integer idEpic : epics.keySet()) {
                epics.get(idEpic).subtasksList = new ArrayList<>();
            }
            System.out.println("Все подзадачи удалены из менеджера");
        }
    }

    /**получение задачи по идентификатору*/
    public Task getTaskById(Integer id) {
        if (tasks.isEmpty()) {
            System.out.println("В менеджере отсутствуют задачи.");
            return null;
        }
        for (Integer idTask : tasks.keySet()) {
            if (tasks.get(idTask).getId() == id) {
                return tasks.get(idTask);
            }
        }
        System.out.println("Задача с идентификатором " + id + " не найдена в менеджере.");
        return null;
    }

    /**получение эпика по идентификатору*/
    public Epic getEpicById(Integer id) {
        if (epics.isEmpty()) {
            System.out.println("В менеджере отсутствуют эпики.");
            return null;
        }
        for (Integer idEpic : epics.keySet()) {
            if (epics.get(idEpic).getId() == id) {
                return epics.get(idEpic);
            }
        }
        System.out.println("Эпик с идентификатором " + id + " не найден в менеджере.");
        return null;
    }

    /**получение подзадачи по идентификатору*/
    public Subtask getSubtaskById(Integer id) {
        if (subtasks.isEmpty()) {
            System.out.println("В менеджере отсутствуют подзадачи.");
            return null;
        }
        for (Integer idSubtask : subtasks.keySet()) {
            if (subtasks.get(idSubtask).getId() == id) {
                return subtasks.get(idSubtask);
            }
        }
        System.out.println("Подзадача с идентификатором " + id + " не найдена в менеджере.");
        return null;
    }

    /**создание задачи*/
    public Task addTask(Task newTask) {
        if (newTask != null) {
            for (Task task : tasks.values()) {
                if (task.equals(newTask)) {
                    System.out.println("Задача с идентификатором " + newTask.getId() + " уже есть в менеджере.");
                    return null;
                }
            }
            newTask.setId(getIdSeq()); //задание id задачи при ее добавлении в менеджер
            tasks.put(newTask.getId(), newTask);
            System.out.println("Задача добавлена в менеджер с идентификатором " + newTask.getId() + ".");
            return newTask;
        } else {
            return null;
        }
    }

    /**создание задачи*/
    public Epic addEpic(Epic newEpic) {
        if (newEpic != null) {
            for (Epic epic : epics.values()) {
                if (epic.equals(newEpic)) {
                    System.out.println("Эпик с идентификатором " + newEpic.getId() + " уже есть в менеджере.");
                    return null;
                }
            }
            newEpic.setId(getIdSeq()); //задание id эпика при его добавлении в менеджер
            epics.put(newEpic.getId(), newEpic);
            System.out.println("Эпик добавлен в менеджер с идентификатором " + newEpic.getId() + ".");
            return newEpic;
        } else {
            return null;
        }
    }

    /**создание подзадачи*/
    public Subtask addSubtask(Subtask newSubtask) {
        if (newSubtask != null) {
            for (Subtask subtask : subtasks.values()) {
                if (subtask.equals(newSubtask)) {
                    System.out.println("Подзадача с идентификатором " + newSubtask.getId() + " уже есть в менеджере.");
                    return null;
                }
            }
            newSubtask.setId(getIdSeq()); //задание id подзадачи при ее добавлении в менеджер
            subtasks.put(newSubtask.getId(), newSubtask);
            newSubtask.epic.subtasksList = getSubtasksListByEpic(newSubtask.epic);
            newSubtask.epic.updateStatus(); //проверка статуса эпика после добавления подзадачи
            System.out.println("Подзадача добавлена в менеджер с идентификатором " + newSubtask.getId() + ".");
            return newSubtask;
        } else {
            return null;
        }
    }

    /**обновление задачи*/
    public Task updateTask(Task updTask) {
        if (updTask != null) {
            for (Task task : tasks.values()) {
                if (task.equals(updTask)) {
                    tasks.put(updTask.getId(), updTask);
                    System.out.println("Задача с идентификатором " + updTask.getId() + " обновлена в менеджере.");
                    return updTask;
                }
            }
            System.out.println("Задача с идентификатором " + updTask.getId() + " не найдена в менеджере.");
            return updTask;
        } else {
            return null;
        }
    }

    /**обновление эпика*/
    public Epic updateEpic(Epic updEpic) {
        if (updEpic != null) {
            for (Epic epic : epics.values()) {
                if (epic.equals(updEpic)) {
                    epics.put(updEpic.getId(), updEpic);
                    System.out.println("Эпик с идентификатором " + updEpic.getId() + " обновлен в менеджере.");
                    return updEpic;
                }
            }
            System.out.println("Эпик с идентификатором " + updEpic.getId() + " не найден в менеджере.");
            return updEpic;
        } else {
            return null;
        }
    }

    /**обновление подзадачи*/
    public Subtask updateSubtask(Subtask updSubtask) {
        if (updSubtask != null) {
            for (Subtask subtask : subtasks.values()) {
                if (subtask.equals(updSubtask)) {
                    subtasks.put(updSubtask.getId(), updSubtask);
                    updSubtask.epic.subtasksList = getSubtasksListByEpic(updSubtask.epic);
                    updSubtask.epic.updateStatus(); //проверка статуса эпика после обновления подзадачи
                    System.out.println("Подзадача с идентификатором " + updSubtask.getId() + " обновлена в менеджере.");
                    return updSubtask;
                }
            }
            System.out.println("Подзадача с идентификатором " + updSubtask.getId() + " не найдена в менеджере.");
            return updSubtask;
        } else {
            return null;
        }
    }

    /**удаление задачи*/
    public Task deleteTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            System.out.println("Задача с идентификатором " + id + " удалена из менеджера.");
            return tasks.remove(id);
        } else {
            System.out.println("Задача с идентификатором " + id + " не найдена в менеджере.");
            return null;
        }
    }

    /**удаление эпика*/
    public Epic deleteEpicById(Integer id) {
        if (epics.containsKey(id)) {
            Epic deletedEpic = epics.remove(id);
            System.out.println("Эпик с идентификатором " + id + " удален из менеджера.");
            if (!deletedEpic.subtasksList.isEmpty()) {
                System.out.println("Удаление связанных подзадач:");
                for (Subtask subtask : deletedEpic.subtasksList) {
                    deleteSubtaskById(subtask.getId());
                }
            }
            return deletedEpic;
        } else {
            System.out.println("Эпик с идентификатором " + id + " не найден в менеджере.");
            return null;
        }
    }

    /**удаление подзадачи*/
    public Subtask deleteSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            Subtask deletedSubtask = subtasks.remove(id);
            Epic epicOfRemovedSubtask = deletedSubtask.epic; //эпик удаляемой подзадачи
            epicOfRemovedSubtask.subtasksList = getSubtasksListByEpic(epicOfRemovedSubtask);
            epicOfRemovedSubtask.updateStatus(); //проверка статуса эпика после удаления подзадачи
            System.out.println("Подзадача с идентификатором " + id + " удалена из менеджера.");
            return deletedSubtask;
        } else {
            System.out.println("Подзадача с идентификатором " + id + " не найдена в менеджере.");
            return null;
        }
    }

    /**Получение списка всех подзадач определённого эпика*/
    public ArrayList<Subtask> getSubtasksListByEpic(Epic epic) {
        if (subtasks.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Integer idSubtask : subtasks.keySet()) {
            if (subtasks.get(idSubtask).epic.equals(epic)) {
                subtaskList.add(subtasks.get(idSubtask));
            }
        }
        return subtaskList;
    }

}
