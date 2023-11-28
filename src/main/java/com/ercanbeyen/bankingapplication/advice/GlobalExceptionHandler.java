package com.ercanbeyen.bankingapplication.advice;

import com.ercanbeyen.bankingapplication.response.ExceptionResponse;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(Exception exception) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.EXPECTATION_FAILED.value(), "File is too large to upload", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception exception) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
