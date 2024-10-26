package task_management_web.task_management_web.exception;

public class AuthenticationFailedException extends RuntimeException{
    public AuthenticationFailedException(String message){
        super(message);
    }
}
