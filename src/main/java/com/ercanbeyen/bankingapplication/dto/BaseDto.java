package com.ercanbeyen.bankingapplication.dto;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public sealed class BaseDto permits CustomerDto {
    LocalDateTime createTime;
    LocalDateTime updateTime;
}
