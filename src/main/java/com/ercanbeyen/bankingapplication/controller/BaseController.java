package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.constant.enums.Entity;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.BaseDto;
import com.ercanbeyen.bankingapplication.dto.response.MessageResponse;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.option.BaseFilteringOptions;
import com.ercanbeyen.bankingapplication.service.BaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseController<T extends BaseDto, V extends BaseFilteringOptions> {
    private final BaseService<T, V> baseService;

    @GetMapping
    public ResponseEntity<List<T>> getEntities(V options) {
        return new ResponseEntity<>(baseService.getEntities(options), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> getEntity(@PathVariable("id") Integer id) {
        return baseService.getEntity(id)
                .map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.GENERAL.getValue())));
    }

    @PostMapping
    public ResponseEntity<T> createEntity(@RequestBody @Valid T request) {
        return new ResponseEntity<>(baseService.createEntity(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<T> updateEntity(@PathVariable("id") Integer id, @RequestBody @Valid T request) {
        return new ResponseEntity<>(baseService.updateEntity(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse<String>> deleteEntity(@PathVariable("id") Integer id) {
        return baseService.getEntity(id)
                .map(entity -> {
                    baseService.deleteEntity(id);
                    MessageResponse<String> response = new MessageResponse<>(ResponseMessages.DELETE_SUCCESS);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                })
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, "Entity")));
    }
}
