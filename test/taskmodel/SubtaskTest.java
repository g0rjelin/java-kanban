package taskmodel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;

public class SubtaskTest {

    @Test
    public void shouldSubasksBeEqualWhenTheirIdEquals() {
        Epic epic = new Epic(5,"Epic1 Equality Id Test", "Description Equality Id Test", Duration.ofMinutes(30), new ArrayList<>());
        int epicId = epic.getId();
        Subtask subtaskOneWithId1 = new Subtask(1, "Subtask1 Equality Id Test", "Description Equality Id Test", TaskStatus.NEW, Duration.ofMinutes(45), epicId);
        //прямая проверка: id равные, при этом поля могут отличаться
        Subtask subtaskTwoWithId1 = new Subtask(1, "Subtask1 Equality Id Test (with changed fields)", "Description Equality Id Test (with changed fields)",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(30), epicId-1);

        //обратная проверка: все поля равны, только id отличается
        Subtask subtaskThreeWithId2 = new Subtask(2, "Subtask1 Equality Id Test", "Description Equality Id Test", TaskStatus.NEW, Duration.ofMinutes(15), epicId);

        Assertions.assertEquals(subtaskOneWithId1, subtaskTwoWithId1, "Экземпляры класса Subtask должны быть равны друг другу, если равен их id");
        Assertions.assertNotEquals(subtaskOneWithId1, subtaskThreeWithId2, "Экземпляры Subtask с разным id не должны быть равны");
    }

}
