import taskmanager.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Первая задача", "Пример запланированной задачи");
        Task task2 = new Task("Вторая задача", "Пример задачи в работе", TaskStatus.IN_PROGRESS);
        Epic epic1 = new Epic("Первый эпик", "Первое эпичное описание");
        Subtask subtask1 = new Subtask("Первая подзадачка", "Запланированная подзадача", TaskStatus.DONE, epic1);
        Subtask subtask2 = new Subtask("Вторая подзадачка", "Подзадача в работе", epic1);
        taskManager.addTask(null);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubtasksList());

        System.out.println();
        System.out.println("Изменения #1:");
        Task updateTask1 = new Task(task1.getName(), task1.getDescription(), task1.getId(), TaskStatus.DONE);

        Subtask updateSubtask2 = new Subtask(subtask2.getName(), subtask2.getDescription(), subtask2.getId(), TaskStatus.DONE, subtask2.getEpic());
        System.out.println(taskManager.updateTask(updateTask1));
        System.out.println(taskManager.updateSubtask(updateSubtask2));


        System.out.println();
        System.out.println("После изменений #1:");
        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubtasksList());


        System.out.println();
        System.out.println("Изменения #2:");
        System.out.println(taskManager.deleteTaskById(task1.getId()));
        System.out.println(taskManager.deleteEpicById(epic1.getId()));

        System.out.println();
        System.out.println("После изменений #2:");
        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubtasksList());


    }
}
