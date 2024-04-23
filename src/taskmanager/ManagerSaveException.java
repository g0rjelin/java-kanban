package taskmanager;

public class ManagerSaveException extends RuntimeException {
    ManagerSaveException(String errorMessage) {
        super(errorMessage);
    }
}