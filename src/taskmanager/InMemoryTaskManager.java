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
import java.util.TreeSet;
import java.util.stream.Collector;
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
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
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

    /**
     * получение списка задач в порядке приоритета
     */
    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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
        if (newTask.getStartTime() == null || hasTaskNoIntersections(newTask)) {
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
        if (epics.containsKey(newSubtask.getIdEpic())) {
            if (newSubtask.getStartTime() == null || hasTaskNoIntersections(newSubtask)) {
                newSubtask.setId(getIdSeq());
                subtasks.put(newSubtask.getId(), newSubtask);
                Epic epicFromSubtask = epics.get(newSubtask.getIdEpic());
                epicFromSubtask.addIdSubtask(newSubtask.getId());
                updateEpicStatus(epicFromSubtask);
                updateEpicDurationStartTimeEndTime(epicFromSubtask);
                if (newSubtask.getStartTime() != null) {
                    prioritizedTasks.add(newSubtask);
                }
            } else { //если есть пересечения, возврашаем 0 и не добавляем подзадачу
                return 0;
            }
        }
        return newSubtask.getId();
    }

    /**
     * обновление задачи
     */
    @Override
    public Integer updateTask(Task updTask) {
        if (tasks.containsKey(updTask.getId())) {
            if (updTask.getStartTime() == null || hasTaskNoIntersections(updTask)) {
                tasks.put(updTask.getId(), updTask);
                prioritizedTasks.remove(tasks.get(updTask.getId()));
                if (updTask.getStartTime() != null) {
                    prioritizedTasks.add(updTask);
                }
            } else { //если есть пересечения, возврашаем 0 и не обновляем задачу
                return 0;
            }
        }
        return updTask.getId();
    }

    /**
     * обновление эпика
     */
    @Override
    public Integer updateEpic(Epic updEpic) {
        if (epics.containsKey(updEpic.getId())) {
            epics.put(updEpic.getId(), updEpic);
            updateEpicStatus(updEpic);
            updateEpicDurationStartTimeEndTime(updEpic);
        }
        return updEpic.getId();
    }

    /**
     * обновление подзадачи
     */
    @Override
    public Integer updateSubtask(Subtask updSubtask) {
        if (subtasks.containsKey(updSubtask.getId())) {
            if (updSubtask.getStartTime() == null || hasTaskNoIntersections(updSubtask)) {
                subtasks.put(updSubtask.getId(), updSubtask);
                Epic epicFromSubtask = epics.get(updSubtask.getIdEpic());
                updateEpicStatus(epicFromSubtask);
                updateEpicDurationStartTimeEndTime(epicFromSubtask);
                prioritizedTasks.remove(subtasks.get(updSubtask.getId()));
                if (updSubtask.getStartTime() != null) {
                    prioritizedTasks.add(updSubtask);
                }
            } else { //если есть пересечения, возврашаем 0 и не обновляем подзадачу
                return 0;
            }
        }
        return updSubtask.getId();
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
        if (subtasks.containsKey(id)) {
            prioritizedTasks.remove(subtasks.get(id));
            Subtask deletedSubtask = subtasks.remove(id);
            historyManager.remove(id);
            Epic epicOfRemovedSubtask = epics.get(deletedSubtask.getIdEpic()); //эпик удаляемой подзадачи
            epicOfRemovedSubtask.deleteIdSubtask(id);
            updateEpicStatus(epicOfRemovedSubtask); //проверка статуса эпика после удаления подзадачи
            updateEpicDurationStartTimeEndTime(epicOfRemovedSubtask);
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
     * определение:
     * продолжительности эпика (сумма продолжительностей всех его подзадач)
     * определение времени начала эпика - даты старта самой ранней подзадачи
     * время завершения эпика — время окончания самой поздней из подзадач
     */
    protected void updateEpicDurationStartTimeEndTime(Epic epic) {
        class DurationStartTimeEndTimeAccumulator {
            private Duration duration = Duration.ZERO;
            private LocalDateTime startTime = null;
            private LocalDateTime endTime = null;

            void accumulate(Subtask subtask) {
                duration = duration.plus(subtask.getDuration());
                LocalDateTime accStartTime = subtask.getStartTime();
                if (accStartTime != null) {
                    startTime = (startTime == null) ? accStartTime :
                            (accStartTime.isBefore(startTime) ? accStartTime : startTime);
                }
                LocalDateTime accEndTime = subtask.getEndTime();
                if (accEndTime != null) {
                    endTime = (endTime == null) ? accEndTime : (accEndTime.isAfter(endTime) ? accEndTime : endTime);
                }

            }

            DurationStartTimeEndTimeAccumulator combine(DurationStartTimeEndTimeAccumulator acc) {
                duration = duration.plus(acc.duration);
                LocalDateTime accStartTime = acc.startTime;
                if (accStartTime != null) {
                    startTime = (startTime == null) ? accStartTime :
                            (accStartTime.isBefore(startTime) ? accStartTime : startTime);
                }
                LocalDateTime accEndTime = acc.endTime;
                if (accEndTime != null) {
                    endTime = (endTime == null) ? accEndTime : (accEndTime.isAfter(endTime) ? accEndTime : endTime);
                }
                return this;
            }
        }

        DurationStartTimeEndTimeAccumulator acc = epic.getSubtaskIdList().stream()
                .map(subtaskId -> subtasks.get(subtaskId))
                .collect(Collector.of(DurationStartTimeEndTimeAccumulator::new,
                        DurationStartTimeEndTimeAccumulator::accumulate,
                        DurationStartTimeEndTimeAccumulator::combine));

        epic.setDuration(acc.duration);
        epic.setStartTime(acc.startTime);
        epic.setEndTime(acc.endTime);

    }

    /**
     * Валидация наличия пересечений с задачами и подзадачами таск менеджера
     */
    protected boolean hasTaskNoIntersections(Task validatedTask) {
        return getPrioritizedTasks().stream()
                .noneMatch(task -> CommonTaskManagerUtils.isIntersecting(validatedTask, task));
    }
}
