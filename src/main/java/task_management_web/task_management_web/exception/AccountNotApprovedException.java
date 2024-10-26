package task_management_web.task_management_web.exception;

public class AccountNotApprovedException extends RuntimeException {
    public AccountNotApprovedException(String message) {
        super(message);
    }
}
