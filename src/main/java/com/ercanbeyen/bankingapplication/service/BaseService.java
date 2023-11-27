package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.dto.BaseDto;

import java.util.List;
import java.util.Optional;

public interface BaseService<T extends BaseDto> {
    List<T> getEntities();
    Optional<T> getEntity(Integer id);
    T createEntity(T request);
    T updateEntity(Integer id, T request);
    void deleteEntity(Integer id);
}
