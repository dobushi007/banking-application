package com.ercanbeyen.bankingapplication.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerDto extends BaseDto {
    String name;
    String surname;
}
