package com.validata.deepdetect.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@Slf4j
@ControllerAdvice
@Order(1)  // Higher priority than ResponseEntityExceptionHandler
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            apiError.addSubError(new ApiValidationError(
                    ex.getBindingResult().getObjectName(),
                    fieldName,
                    ((FieldError) error).getRejectedValue(),
                    errorMessage));
        });

        return new ResponseEntity<>(apiError, headers, status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex,
            WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.setPath(request.getDescription(false).replace("uri=", ""));

        ex.getConstraintViolations().forEach(violation -> {
            apiError.addSubError(new ApiValidationError(
                    violation.getRootBeanClass().getName(),
                    violation.getPropertyPath().toString(),
                    violation.getInvalidValue(),
                    violation.getMessage()));
        });

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    protected ResponseEntity<Object> handleCustomerNotFoundException(
            CustomerNotFoundException ex,
            WebRequest request) {
        log.warn("Customer not found: {}", ex.getMessage());
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidFileException.class)
    protected ResponseEntity<Object> handleInvalidFileException(
            InvalidFileException ex,
            WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ModelServerException.class)
    protected ResponseEntity<Object> handleModelServerException(
            ModelServerException ex,
            WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> handleBadCredentialsException(
            BadCredentialsException ex,
            WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, "Access denied");
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Object> handleAuthenticationException(
            AuthenticationException ex,
            WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Authentication failed");
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName()));
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(String.format("The parameter '%s' is missing", ex.getParameterName()));
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAllUncaughtException(
            Exception ex,
            WebRequest request) {
        log.error("Unhandled exception occurred", ex);
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(ExistingSocialUserException.class)
    protected ResponseEntity<Object> handleExistingSocialUserException(
            ExistingSocialUserException ex,
            WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT, ex.getMessage());
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(LoginFailedException.class)
    protected ResponseEntity<Object> handleLoginFailedException(
            LoginFailedException ex,
            WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getMessage());
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

}
