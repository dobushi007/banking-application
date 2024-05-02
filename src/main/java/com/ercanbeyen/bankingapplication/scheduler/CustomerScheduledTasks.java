package com.ercanbeyen.bankingapplication.scheduler;

import com.ercanbeyen.bankingapplication.constant.enums.Entity;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.dto.CustomerDto;
import com.ercanbeyen.bankingapplication.dto.NotificationDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Async
@Slf4j
@RequiredArgsConstructor
public class CustomerScheduledTasks {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(cron = " 0 0 12 * * *") // 12:00 everyday
    public void celebrateCustomersBirthday() {
        final String task = "celebrate customers' birthday";
        log.info(LogMessages.SCHEDULED_TASK_STARTED, task);
        LocalDate birthday = LocalDate.now();
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(Entity.CUSTOMER.getCollectionUrl())
                .queryParam("birthDate", birthday.toString())
                .build();
        String notificationMessage = "happy birthday";
        notifyCustomers(task, uriComponents, notificationMessage);
    }

    @Scheduled(cron = "0 0 0 1 9 ?") // Every September 1st at midnight
    public void announceStartOfRating() {
        final String task = "announce start of rating";
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(Entity.CUSTOMER.getCollectionUrl())
                .build();
        String notificationMessage = "Ratings for " + LocalDateTime.now().getYear() + " is started";
        notifyCustomers(task, uriComponents, notificationMessage);
    }

    private void notifyCustomers(String task, UriComponents uriComponents, String notificationMessage) {
        log.info(LogMessages.SCHEDULED_TASK_STARTED, task);
        List<CustomerDto> customerDtos;

        try {
            String url = uriComponents.toString();
            List<?> response = restTemplate.getForObject(url, List.class);
            assert response != null;
            log.info(LogMessages.CLASS_OF_RESPONSE, response.getClass());

            customerDtos = objectMapper.convertValue(response, new TypeReference<>() {});
            customerDtos.forEach(customerDto -> log.info(LogMessages.CLASS_OF_OBJECT, "CustomerDto", customerDto.getClass()));

            log.info(LogMessages.REST_TEMPLATE_SUCCESS, customerDtos);
        } catch (Exception exception) {
            log.error(LogMessages.EXCEPTION, exception.getMessage());
            return;
        }

        customerDtos.forEach(customerDto -> {
            NotificationDto request = new NotificationDto(customerDto.getNationalId(), notificationMessage);

            try {
                log.info(LogMessages.BEFORE_REQUEST);
                NotificationDto response = restTemplate.postForObject(Entity.NOTIFICATION.getCollectionUrl(), request, NotificationDto.class);
                log.info(LogMessages.REST_TEMPLATE_SUCCESS, response);
            } catch (Exception exception) {
                log.error(LogMessages.EXCEPTION, exception.getMessage());
            }

            log.info(LogMessages.AFTER_REQUEST);
        });

        log.info(LogMessages.SCHEDULED_TASK_ENDED, task);
    }
}
