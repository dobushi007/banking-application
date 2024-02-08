package com.ercanbeyen.bankingapplication.dto;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public sealed class BaseDto permits CustomerDto, AccountDto, RegularTransferOrderDto {
    private Integer id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
