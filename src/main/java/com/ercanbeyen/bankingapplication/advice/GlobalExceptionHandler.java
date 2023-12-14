package com.ercanbeyen.bankingapplication.advice;

import com.ercanbeyen.bankingapplication.exception.ResourceExpectationFailedException;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.response.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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

        return constructResponse(errors.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(Exception exception) {
        return constructResponse(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class, ResourceExpectationFailedException.class})
    public ResponseEntity<?> handleResourceExpectationFailedException(Exception exception) {
        return constructResponse(exception.getMessage(), HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception exception) {
        return constructResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> constructResponse(String message, HttpStatus httpStatus) {
        ExceptionResponse response = new ExceptionResponse(httpStatus.value(), message, LocalDateTime.now());
        return new ResponseEntity<>(response, httpStatus);
    }
}
