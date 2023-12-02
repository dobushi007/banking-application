package com.ercanbeyen.bankingapplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class ResourceExpectationFailedException extends RuntimeException {
    public ResourceExpectationFailedException(String message) {
        super(message);
    }
}
