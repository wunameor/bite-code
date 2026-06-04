package exception;

public class UserPermissionException extends RuntimeException {
    public UserPermissionException() {
    }

    public UserPermissionException(String message) {
        super(message);
    }
}
