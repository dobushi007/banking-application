package com.ercanbeyen.bankingapplication.mapper;

import com.ercanbeyen.bankingapplication.dto.TaskDto;
import com.ercanbeyen.bankingapplication.entity.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskDto taskToDto(Task task);
    Task dtoToTask(TaskDto taskDto);
}
