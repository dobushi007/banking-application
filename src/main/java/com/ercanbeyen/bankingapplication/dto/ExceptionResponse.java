package com.ercanbeyen.bankingapplication.dto;

import java.time.LocalDateTime;

public record ExceptionResponse(int httpStatus, String message, LocalDateTime createdDate) {
}
