package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.dto.TaskDto;

public interface TaskService {
    String createTask(TaskDto taskDto);
    void deleteTask(String id);
}
