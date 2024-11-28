package task_management_web.task_management_web.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.management.relation.RoleNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String errorType) {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), message, errorType);
        return ResponseEntity.status(status).body(errorResponse);
    }

    // Handle ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "ResourceNotFoundException");
    }

    // Handle UserNotFoundException
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        logger.warn("User not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "UserNotFoundException");
    }

    // Handle AccountAlreadyExistException
    @ExceptionHandler(AccountAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleAccountAlreadyExistException(AccountAlreadyExistException ex) {
        logger.warn("Account already exists: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), "AccountAlreadyExistException");
    }

    // Handle RoleNotFoundException
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFoundException(RoleNotFoundException ex) {
        logger.warn("Role not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "RoleNotFoundException");
    }

    // Handle AccountNotApprovedException
    @ExceptionHandler(AccountNotApprovedException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotApprovedException(AccountNotApprovedException ex) {
        logger.warn("Account not approved: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), "AccountNotApprovedException");
    }

    // Handle AuthenticationFailedException
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationFailedException(AuthenticationFailedException ex) {
        logger.warn("Authentication failed: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), "AuthenticationFailedException");
    }

    // Handle InvalidRequestException
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException ex) {
        logger.warn("Invalid request: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "InvalidRequestException");
    }

    //Handle AcessDeniedException
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access denied occurred."); // Log đơn giản, không chi tiết
        return buildErrorResponse(HttpStatus.FORBIDDEN, "You do not have permission to perform this action.", "AccessDeniedException");
    }

    //Handle AcessDeniedException
    @ExceptionHandler(SessionAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleSessionAuthenticationException(SessionAuthenticationException ex) {
        logger.warn("Session expired or invalid"); // Log đơn giản, không chi tiết
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), "SessionException");
    }

    // Handle MaxUploadSizeExceededException
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        logger.warn("File upload exceeds maximum size: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "File size exceeds the maximum limit of 10MB.", "MaxUploadSizeExceededException");
    }

    // Handle General Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later.", "GeneralException");
    }
}
