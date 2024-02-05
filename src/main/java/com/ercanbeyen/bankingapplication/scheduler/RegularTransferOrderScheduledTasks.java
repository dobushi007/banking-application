package com.ercanbeyen.bankingapplication.scheduler;

import com.ercanbeyen.bankingapplication.constant.enums.Entity;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.dto.RegularTransferOrderDto;
import com.ercanbeyen.bankingapplication.dto.request.TransferRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Component
@Async
@Slf4j
@RequiredArgsConstructor
public class RegularTransferOrderScheduledTasks {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(cron = " 0 0 10 * * *") // 10:00 everyday
    public void applyRegularTransferOrders() {
        final String task = "apply regular transfer orders";
        log.info(LogMessages.SCHEDULED_TASK_STARTED, task);

        List<RegularTransferOrderDto> regularTransferOrderDtos;

        try {
            String url = Entity.REGULAR_TRANSFER_ORDER.getCollectionUrl();
            log.info("Getting regular transfer url: {}", url);

            List<?> response = restTemplate.getForObject(url, List.class);
            assert response != null;
            log.info(LogMessages.CLASS_OF_RESPONSE, response.getClass());

            regularTransferOrderDtos = objectMapper.convertValue(response, new TypeReference<>() {});
            regularTransferOrderDtos.forEach(regularTransferOrderDto -> log.info(LogMessages.CLASS_OF_OBJECT, "RegularTransferDto", regularTransferOrderDto.getClass()));

            log.info(LogMessages.REST_TEMPLATE_SUCCESS, regularTransferOrderDtos);
        } catch (Exception exception) {
             log.error(LogMessages.EXCEPTION, exception.getMessage());
             return;
         }

        regularTransferOrderDtos.forEach(regularTransferOrderDto -> {
            if (getRegularTransferOrderDtoPredicate().test(regularTransferOrderDto)) {
                log.info("Period check is passed");
                getRegularTransferOrderDtoConsumer().accept(regularTransferOrderDto);
                log.info("Transfer is successfully completed");
            }
        });

        log.info(LogMessages.SCHEDULED_TASK_ENDED, task);
    }

    private static Predicate<RegularTransferOrderDto> getRegularTransferOrderDtoPredicate() {
        return regularTransferOrderDto -> {
            LocalDate regularTransferOrderDate = regularTransferOrderDto.getCreateTime()
                    .toLocalDate()
                    .plusWeeks(regularTransferOrderDto.getPeriod());
            LocalDate today = LocalDate.now();

            return today.isEqual(regularTransferOrderDate);
        };
    }

    private Consumer<RegularTransferOrderDto> getRegularTransferOrderDtoConsumer() {
        return regularTransferOrderDto -> {
            Integer senderAccountId = regularTransferOrderDto.getSenderAccountId();
            Integer receiverAccountId = regularTransferOrderDto.getRegularTransfer().getReceiverAccountId();
            TransferRequest transferRequest = new TransferRequest(
                    senderAccountId,
                    receiverAccountId,
                    regularTransferOrderDto.getRegularTransfer().getAmount(),
                    regularTransferOrderDto.getRegularTransfer().getExplanation());

            try {
                String url = Entity.ACCOUNT.getCollectionUrl() + "/transfer";
                log.info("Transfer url: {}", url);
                restTemplate.put(url, transferRequest);

                String logMessage = "Transfer from " + senderAccountId + " to " + receiverAccountId + " is successfully completed";
                log.info(LogMessages.TRANSACTION_MESSAGE, logMessage);
            } catch (Exception exception) {
                log.error(LogMessages.EXCEPTION, exception.getMessage());
            }

            log.info(LogMessages.AFTER_REQUEST);
        };
    }
}
