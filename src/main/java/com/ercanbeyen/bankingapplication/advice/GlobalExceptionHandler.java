package com.ercanbeyen.bankingapplication.advice;

import com.ercanbeyen.bankingapplication.exception.ResourceConflictException;
import com.ercanbeyen.bankingapplication.exception.ResourceExpectationFailedException;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.response.ExceptionResponse;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    String field = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();
                    errors.put(field, message);
                });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<?> handleMethodValidationException(HandlerMethodValidationException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getAllValidationResults()
                .forEach(validationResult -> {
                    String parameter = validationResult.getMethodParameter().getParameter().getName();
                    for (MessageSourceResolvable messageSourceResolvable : validationResult.getResolvableErrors()) {
                        String message = messageSourceResolvable.getDefaultMessage();
                        errors.put(parameter, message);
                    }
                });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(Exception exception) {
        return constructResponse(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<?> handleResourceConflictException(Exception exception) {
        return constructResponse(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class, ResourceExpectationFailedException.class})
    public ResponseEntity<?> handleResourceExpectationFailedException(Exception exception) {
        return constructResponse(exception, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception exception) {
        return constructResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> constructResponse(Exception exception, HttpStatus httpStatus) {
        ExceptionResponse response = new ExceptionResponse(httpStatus.value(), exception.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(response, httpStatus);
    }
}
