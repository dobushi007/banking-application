package com.ercanbeyen.bankingapplication.dto;

import java.time.LocalDateTime;

public record BaseDto(LocalDateTime creationDate, LocalDateTime updateTime) {

}
