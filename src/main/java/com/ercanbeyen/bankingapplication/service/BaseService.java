package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.dto.BaseDto;
import com.ercanbeyen.bankingapplication.option.BaseFilteringOptions;

import java.util.List;
import java.util.Optional;

public interface BaseService<T extends BaseDto, V extends BaseFilteringOptions> {
    List<T> getEntities(V options);
    Optional<T> getEntity(Integer id);
    T createEntity(T request);
    T updateEntity(Integer id, T request);
    void deleteEntity(Integer id);
}
