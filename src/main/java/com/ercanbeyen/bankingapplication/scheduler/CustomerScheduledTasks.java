package com.ercanbeyen.bankingapplication.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Async
@Slf4j
@RequiredArgsConstructor
public class CustomerScheduledTasks {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String CUSTOMER_COLLECTION_URL = "http://localhost:8080/api/v1/customers";
    private static final String ID = "id";
}
