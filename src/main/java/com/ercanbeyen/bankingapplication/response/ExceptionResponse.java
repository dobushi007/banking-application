package com.ercanbeyen.bankingapplication.response;

import java.time.LocalDateTime;

public record ExceptionResponse(int httpStatus, String message, LocalDateTime createdAt) {

}
