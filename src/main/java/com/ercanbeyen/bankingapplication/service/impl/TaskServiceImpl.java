package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.TaskDto;
import com.ercanbeyen.bankingapplication.entity.Task;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.TaskMapper;
import com.ercanbeyen.bankingapplication.repository.TaskRepository;
import com.ercanbeyen.bankingapplication.service.TaskService;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public String createTask(TaskDto taskDto) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod())
        );

        Task task = taskMapper.dtoToTask(taskDto);
        Task createdTask = taskRepository.save(task);
        log.info(LogMessages.TASK_CREATED, createdTask.getType());

        return createdTask.getId();
    }

    @Override
    public void deleteTask(String id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod())
        );

        Task task = findTaskById(id);
        log.info(LogMessages.RESOURCE_FOUND, LogMessages.ResourceNames.TASK);

        taskRepository.delete(task);
    }

    private Task findTaskById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.NOT_FOUND));
    }
}
