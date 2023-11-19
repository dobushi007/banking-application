package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.dto.BaseDto;

import java.util.List;
import java.util.Optional;

public interface BaseService<T extends BaseDto> {
    List<T> findAll();
    Optional<T> findById(Integer id);
    T create(T request);
    T update(Integer id, T request);
    String delete(Integer id);
}
