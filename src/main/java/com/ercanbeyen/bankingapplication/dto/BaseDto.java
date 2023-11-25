package com.ercanbeyen.bankingapplication.dto;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class BaseDto {
    LocalDateTime createTime;
    LocalDateTime updateTime;
}
