package taskmodel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;

import java.util.List;

public class TaskTest {

    @Test
    public void shouldTasksBeEqualWhenTheirIdEquals() {
        Task taskOneWithId1 = new Task(1, "Task1 Equality Id Test", "Description Equality Id Test", TaskStatus.NEW);
        //прямая проверка: id равные, при этом поля могут отличаться
        Task taskTwoWithId1 = new Task(1, "Task1 Equality Id Test (with changed fields)", "Description Equality Id Test (with changed fields)", TaskStatus.IN_PROGRESS);

        //обратная проверка: все поля равны, только id отличается
        Task taskThreeWithId2 = new Task(2, "Task1 Equality Id Test", "Description Equality Id Test", TaskStatus.NEW);

        Assertions.assertEquals(taskOneWithId1, taskTwoWithId1, "Экземпляры класса Task должны быть равны друг другу, если равен их id");
        Assertions.assertNotEquals(taskOneWithId1, taskThreeWithId2, "Экземпляры Task с разным id не должны быть равны");
    }
}
