package taskmanager;

public class NotFoundException extends RuntimeException { //extends RuntimeException {
    public NotFoundException() {
    }

    NotFoundException(final String errorMessage) {
        super(errorMessage);
    }
}
