package taskmanager;

import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int idSeq = 0; //счетчик задач в менеджере

    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, Subtask> subtasks;
    protected HistoryManager historyManager;

    protected TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        this.historyManager = historyManager;
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    protected InMemoryTaskManager(HistoryManager historyManager, HashMap<Integer, Task> tasks,
                                  HashMap<Integer, Epic> epics, HashMap<Integer, Subtask> subtasks) {
        this.tasks = tasks;
        this.epics = epics;
        this.subtasks = subtasks;
        this.historyManager = historyManager;
        this.prioritizedTasks = tasks.values().stream()
                .filter(task -> task.getStartTime() != null)
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Task::getStartTime))));
    }

    /**
     * генерация id менеджером
     */
    protected int getIdSeq() {
        return ++idSeq;
    }

    /**
     * получение списка задач
     */
    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * получение списка эпиков
     */
    @Override
    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    /**
     * получение списка подзадач
     */
    @Override
    public ArrayList<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    /**
     * получение менеджера истории
     */
    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    /**
     * получение истории
     */
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    /*
     *
     * */
    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    /**
     * удаление всех задач
     */
    @Override
    public void removeAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            prioritizedTasks.remove(tasks.get(taskId));
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    /**
     * удаление всех эпиков
     */
    @Override
    public void removeAllEpics() {
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        epics.clear();
        for (Integer subtaskId : subtasks.keySet()) {
            prioritizedTasks.remove(subtasks.get(subtaskId));
            historyManager.remove(subtaskId);
        }
        subtasks.clear();
    }

    /**
     * удаление всех подзадач
     */
    @Override
    public void removeAllSubtasks() {
        for (Integer subtaskId : subtasks.keySet()) {
            prioritizedTasks.remove(subtasks.get(subtaskId));
            historyManager.remove(subtaskId);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setStatus(TaskStatus.NEW);
            epic.clearSubtaskIdList();
        }
    }

    /**
     * получение задачи по идентификатору
     */
    @Override
    public Task getTaskById(Integer id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    /**
     * получение эпика по идентификатору
     */
    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    /**
     * получение подзадачи по идентификатору
     */
    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    /**
     * создание задачи
     */
    @Override
    public Integer addTask(Task newTask) {
        if (hasTaskNoIntersections(newTask)) {
            newTask.setId(getIdSeq());
            tasks.put(newTask.getId(), newTask);
            if (newTask.getStartTime() != null) {
                prioritizedTasks.add(newTask);
            }
            return newTask.getId();
        } else { //если есть пересечения, возврашаем 0 и не добавляем задачу
            return 0;
        }
    }

    /**
     * создание эпика
     */
    @Override
    public Integer addEpic(Epic newEpic) {
        newEpic.setId(getIdSeq());
        epics.put(newEpic.getId(), newEpic);
        return newEpic.getId();
    }

    /**
     * создание подзадачи
     */
    @Override
    public Integer addSubtask(Subtask newSubtask) {
        if (hasTaskNoIntersections(newSubtask)) {
            if (epics.containsKey(newSubtask.getIdEpic())) {
                newSubtask.setId(getIdSeq());
                subtasks.put(newSubtask.getId(), newSubtask);
                Epic epicFromSubtask = epics.get(newSubtask.getIdEpic());
                epicFromSubtask.addIdSubtask(newSubtask.getId());
                updateEpicStatus(epicFromSubtask);
                updateEpicStartTime(epicFromSubtask);
                updateEpicDuration(epicFromSubtask);
            }
            if (newSubtask.getStartTime() != null) {
                prioritizedTasks.add(newSubtask);
            }
            return newSubtask.getId();
        } else { //если есть пересечения, возврашаем 0 и не добавляем подзадачу
            return 0;
        }
    }

    /**
     * обновление задачи
     */
    @Override
    public Integer updateTask(Task updTask) {
        if (hasTaskNoIntersections(updTask)) {
            if (tasks.containsKey(updTask.getId())) {
                tasks.put(updTask.getId(), updTask);
            }
            if (updTask.getStartTime() != null) {
                prioritizedTasks.add(updTask);
            } else { //при обновлении могут убрать время начала задачи
                prioritizedTasks.remove(updTask);
            }
            return updTask.getId();
        } else { //если есть пересечения, возврашаем 0 и не обновляем задачу
            return 0;
        }
    }

    /**
     * обновление эпика
     */
    @Override
    public Integer updateEpic(Epic updEpic) {
        if (epics.containsKey(updEpic.getId())) {
            epics.put(updEpic.getId(), updEpic);
            updateEpicStatus(updEpic);
            updateEpicStartTime(updEpic);
            updateEpicDuration(updEpic);
        }
        return updEpic.getId();
    }

    /**
     * обновление подзадачи
     */
    @Override
    public Integer updateSubtask(Subtask updSubtask) {
        if (hasTaskNoIntersections(updSubtask)) {
            if (subtasks.containsKey(updSubtask.getId())) {
                subtasks.put(updSubtask.getId(), updSubtask);
                Epic epicFromSubtask = epics.get(updSubtask.getIdEpic());
                updateEpicStatus(epicFromSubtask);
                updateEpicStartTime(epicFromSubtask);
                updateEpicDuration(epicFromSubtask);

            }
            if (updSubtask.getStartTime() != null) {
                prioritizedTasks.add(updSubtask);
            } else { //при обновлении могут убрать время начала подзадачи
                prioritizedTasks.remove(updSubtask);
            }
            return updSubtask.getId();
        } else { //если есть пересечения, возврашаем 0 и не обновляем подзадачу
            return 0;
        }
    }

    /**
     * удаление задачи
     */
    @Override
    public void deleteTaskById(Integer id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    /**
     * удаление эпика
     */
    @Override
    public void deleteEpicById(Integer id) {
        Epic deletedEpic = epics.remove(id);
        if (deletedEpic == null) { //если объект не существовал - выходим из метода
            return;
        }
        historyManager.remove(id);
        //удаление связанных с эпиком подзадач
        for (Integer subtaskId : deletedEpic.getSubtaskIdList()) {
            prioritizedTasks.remove(subtasks.get(subtaskId));
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
    }

    /**
     * удаление подзадачи
     */
    @Override
    public void deleteSubtaskById(Integer id) {
        prioritizedTasks.remove(subtasks.get(id));
        if (subtasks.containsKey(id)) {
            Subtask deletedSubtask = subtasks.remove(id);
            historyManager.remove(id);
            Epic epicOfRemovedSubtask = epics.get(deletedSubtask.getIdEpic()); //эпик удаляемой подзадачи
            epicOfRemovedSubtask.deleteIdSubtask(id);
            updateEpicStatus(epicOfRemovedSubtask); //проверка статуса эпика после удаления подзадачи
            updateEpicStartTime(epicOfRemovedSubtask);
            updateEpicDuration(epicOfRemovedSubtask);
            deletedSubtask.setId(0); //Удаляемые подзадачи не должны хранить внутри себя старые id
        }
    }

    /**
     * Получение списка всех подзадач определённого эпика
     */
    @Override
    public ArrayList<Subtask> getSubtasksListByEpic(Epic epic) {
        return epic.getSubtaskIdList().stream()
                .map(this::getSubtaskById)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * обновление статуса эпика:
     * нет подзадач - новая
     * если статус хотя бы одной подзадачи отличается от других, то эпик в работе
     * иначе определяется единым статусом всех входящих в эпик подзадач
     */
    protected void updateEpicStatus(Epic epic) {
        TaskStatus identityElementStatus = epic.getSubtaskIdList().stream()
                .map(subtaskId -> getSubtaskById(subtaskId).getStatus())
                .findFirst()
                .orElse(TaskStatus.NEW);
        epic.setStatus(epic.getSubtaskIdList().stream()
                .map(subtaskId -> getSubtaskById(subtaskId).getStatus())
                .reduce(identityElementStatus,
                        (subtaskStatus, intermediateSubTaskStatus) -> (intermediateSubTaskStatus != subtaskStatus) ?
                                TaskStatus.IN_PROGRESS : subtaskStatus));
    }

    /**
     * определение продолжительности эпика (сумма продолжительностей всех его подзадач)
     */
    protected void updateEpicDuration(Epic epic) {
        Duration duration = epic.getSubtaskIdList().stream()
                .map(subtaskId -> subtasks.get(subtaskId).getDuration())
                .reduce(Duration.ZERO, Duration::plus);
        epic.setDuration(duration);
        if (epic.getStartTime() != null) {
            epic.setEndTime(epic.getStartTime().plus(duration));
        }
    }

    /**
     * определение времени начала эпика - даты старта самой ранней подзадачи
     */
    protected void updateEpicStartTime(Epic epic) {
        LocalDateTime startTime = epic.getSubtaskIdList().stream()
                .map(subtaskId -> subtasks.get(subtaskId).getStartTime())
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
        epic.setStartTime(startTime);
        if (startTime != null) {
            epic.setEndTime(startTime.plus(epic.getDuration()));
        }
    }

    /**
     * Валидация наличия пересечений с задачами и подзадачами таск менеджера
     */
    protected boolean hasTaskNoIntersections(Task validatedTask) {
        return (validatedTask.getStartTime() == null) || getPrioritizedTasks().stream()
                .noneMatch(task -> CommonTaskManagerUtils.isIntersecting(validatedTask, task));
    }
}
