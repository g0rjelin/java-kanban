import taskmanager.*;
import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Первая задача", "Пример запланированной задачи");
        Task task2 = new Task("Вторая задача", "Пример задачи в работе", TaskStatus.IN_PROGRESS);
        Epic epic1 = new Epic("Первый эпик", "Первое эпичное описание");
        System.out.println("Добавлена задача с id = " + taskManager.addTask(task1));
        System.out.println("Добавлена задача с id = " + taskManager.addTask(task2));
        System.out.println("Добавлен эпик с id = " + taskManager.addEpic(epic1));
        Subtask subtask1 = new Subtask("Первая подзадачка", "Запланированная подзадача", TaskStatus.DONE, epic1.getId());
        Subtask subtask2 = new Subtask("Вторая подзадачка", "Подзадача в работе", epic1.getId());
        System.out.println("Добавлена подзадача с id = " + taskManager.addSubtask(subtask1));
        System.out.println("Добавлена подзадача с id = " + taskManager.addSubtask(subtask2));
        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubtasksList());

        System.out.println();
        System.out.println("Изменения #1:");
        Task updateTask1 = new Task( task1.getId(), task1.getName(), task1.getDescription(), TaskStatus.DONE);
        Subtask updateSubtask2 = new Subtask(subtask2.getId(), subtask2.getName(), subtask2.getDescription(), TaskStatus.DONE, subtask2.getIdEpic());
        System.out.println(taskManager.getTaskById(taskManager.updateTask(updateTask1)));
        System.out.println(taskManager.getSubtaskById(taskManager.updateSubtask(updateSubtask2)));


        System.out.println();
        System.out.println("После изменений #1:");
        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubtasksList());


        System.out.println();
        System.out.println("Изменения #2:");
        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteEpicById(epic1.getId());

        System.out.println();
        System.out.println("После изменений #2:");
        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubtasksList());


    }
}
