package taskmodel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;

public class EpicTest {

    @Test
    public void shouldEpicsBeEqualWhenTheirIdEquals() {
        ArrayList<Integer> subtaskIdList = new ArrayList<>();
        Epic epicOneWithId1 = new Epic(1, "Epic1 Equality Id Test", "Description Equality Id Test", Duration.ofMinutes(15), subtaskIdList);
        //прямая проверка: id равные, при этом поля могут отличаться
        Epic epicTwoWithId1 = new Epic(1, "Epic1 Equality Id Test (with changed fields)", "Description Equality Id Test (with changed fields)",
                Duration.ofMinutes(15), new ArrayList<>());

        //обратная проверка: все поля равны, только id отличается
        Epic epicThreeWithId2 = new Epic(2, "Epic1 Equality Id Test", "Description Equality Id Test", Duration.ofMinutes(15), subtaskIdList);

        Assertions.assertEquals(epicOneWithId1, epicTwoWithId1, "Экземпляры класса Epic равны друг другу, если равен их id");
        Assertions.assertNotEquals(epicOneWithId1, epicThreeWithId2, "Экземпляры Epic с разным id не должны быть равны");
    }

}
