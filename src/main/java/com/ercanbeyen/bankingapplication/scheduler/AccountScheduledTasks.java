package com.ercanbeyen.bankingapplication.scheduler;

import com.ercanbeyen.bankingapplication.constant.enums.AccountType;
import com.ercanbeyen.bankingapplication.constant.enums.Entity;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.dto.AccountDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
@Async
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class AccountScheduledTasks {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String ID = "id";

    @Scheduled(cron = " 0 0 9 * * *") // 9:00 everyday
    public void addMoneyToDepositAccounts() {
        final String task = "periodic money deposit to deposit account";
        log.info(LogMessages.SCHEDULED_TASK_STARTED, task);

        List<AccountDto> accountDtos;

        try {
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(Entity.ACCOUNT.getCollectionUrl())
                    .queryParam("type", String.valueOf(AccountType.DEPOSIT))
                    .build();

            String url = uriComponents.toString();
            log.info("Getting deposit accounts url: {}", url);

            List<?> response = restTemplate.getForObject(url, List.class);
            assert response != null;
            log.info(LogMessages.CLASS_OF_RESPONSE, response.getClass());

            accountDtos = objectMapper.convertValue(response, new TypeReference<>() {});
            accountDtos.forEach(accountDto -> log.info(LogMessages.CLASS_OF_OBJECT, "AccountDto", accountDto.getClass()));

            log.info(LogMessages.REST_TEMPLATE_SUCCESS, accountDtos);
        } catch (Exception exception) {
            log.error(LogMessages.EXCEPTION, exception.getMessage());
            return;
        }

        log.info("Before mapping to integer list");
        List<Integer> accountIdList = accountDtos.stream()
                .map(AccountDto::getId)
                .toList();
        log.info("After mapping to integer list");

        log.info("AccountIdList: {}", accountIdList);

        accountIdList.forEach(accountId -> {
            log.info(LogMessages.BEFORE_REQUEST);

            Map<String, Integer> parameters = Map.of(ID, accountId);
            String url = Entity.ACCOUNT.getCollectionUrl() + "/{" + ID + "}/deposit";

            try {
                restTemplate.put(url, null, parameters);
                String logMessage = Entity.ACCOUNT.getValue() + " " + accountId + " is successfully updated";
                log.info(LogMessages.REST_TEMPLATE_SUCCESS, logMessage);
            } catch (Exception exception) {
                log.error(LogMessages.EXCEPTION, exception.getMessage());
            }

            log.info(LogMessages.AFTER_REQUEST);
        });

        log.info(LogMessages.SCHEDULED_TASK_ENDED, task);
    }
}
