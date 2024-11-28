package task_management_web.task_management_web.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationFailedException extends AuthenticationException {
    public AuthenticationFailedException(String message){
        super(message);
    }
}
