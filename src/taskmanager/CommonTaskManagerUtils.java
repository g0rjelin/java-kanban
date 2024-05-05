package taskmanager;

import taskmodel.Task;

public final class CommonTaskManagerUtils {
    public static boolean isIntersecting(Task t1, Task t2) {
        if (t1.getStartTime().isBefore(t2.getStartTime())) {
            if (t2.getStartTime().isBefore(t1.getEndTime())) {
                return true;
            } else {
                return false;
            }
        } else {
            if (t1.getStartTime().isBefore(t2.getEndTime())) {
                return true;
            } else {
                return false;
            }
        }
    }
}
