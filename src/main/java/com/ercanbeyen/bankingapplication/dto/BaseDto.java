package com.ercanbeyen.bankingapplication.dto;

import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public class BaseDto {
    LocalDateTime creationDate;
    LocalDateTime updateTime;
}
