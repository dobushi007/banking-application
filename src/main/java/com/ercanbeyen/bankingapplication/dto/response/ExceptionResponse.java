package com.ercanbeyen.bankingapplication.dto.response;

import java.time.LocalDateTime;

public record ExceptionResponse(int httpStatus, String message, LocalDateTime createdAt) {

}
