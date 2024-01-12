package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.constant.enums.TaskType;

public record TaskDto(TaskType type, String endpoint) {

}
