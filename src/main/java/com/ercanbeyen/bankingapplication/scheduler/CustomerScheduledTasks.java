package com.ercanbeyen.bankingapplication.scheduler;

import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.resource.Resources;
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
        List<CustomerDto> customerDtoList;

        try {
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(Resources.Urls.CUSTOMERS)
                    .queryParam("birthDate", birthday.toString())
                    .build();

            String url = uriComponents.toString();
            log.info("Getting customers have birthday url: {}", url);

            List<?> response = restTemplate.getForObject(url, List.class);
            assert response != null;
            log.info("Class of response: {}", response.getClass());

            customerDtoList = objectMapper.convertValue(response, new TypeReference<>() {});
            customerDtoList.forEach(customerDto -> log.info("Class of CustomerDto: {}", customerDto.getClass()));

            log.info(LogMessages.REST_TEMPLATE_SUCCESS, customerDtoList);
        } catch (Exception exception) {
            log.error(LogMessages.EXCEPTION, exception.getMessage());
            return;
        }

        customerDtoList.forEach(customerDto -> {
            NotificationDto request = new NotificationDto(customerDto.getNationalId(), "happy birthday");

            try {
                log.info(LogMessages.BEFORE_REQUEST);
                NotificationDto response = restTemplate.postForObject(Resources.Urls.NOTIFICATIONS, request, NotificationDto.class);
                log.info(LogMessages.REST_TEMPLATE_SUCCESS, response);
            } catch (Exception exception) {
                log.error(LogMessages.EXCEPTION, exception.getMessage());
            }

            log.info(LogMessages.AFTER_REQUEST);
        });

        log.info(LogMessages.SCHEDULED_TASK_ENDED, task);
    }
}
