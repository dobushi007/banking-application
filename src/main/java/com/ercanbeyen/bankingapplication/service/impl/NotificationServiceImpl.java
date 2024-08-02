package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.Entity;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.NotificationDto;
import com.ercanbeyen.bankingapplication.entity.Customer;
import com.ercanbeyen.bankingapplication.entity.Notification;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.NotificationMapper;
import com.ercanbeyen.bankingapplication.repository.NotificationRepository;
import com.ercanbeyen.bankingapplication.service.NotificationService;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final CustomerService customerService;

    @Override
    public NotificationDto createNotification(NotificationDto notificationDto) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Notification notification = notificationMapper.dtoToEntity(notificationDto);
        Customer customer = customerService.findByNationalId(notificationDto.customerNationalId());
        notification.setCustomer(customer);

        Notification savedNotification = notificationRepository.save(notification);
        log.info(LogMessages.RESOURCE_CREATE_SUCCESS, Entity.NOTIFICATION.getValue(), savedNotification.getId());

        return notificationMapper.entityToDto(savedNotification);
    }

    @Override
    public String deleteNotification(String id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        String entity = Entity.NOTIFICATION.getValue();

        notificationRepository.findById(id)
                .ifPresentOrElse(notification -> notificationRepository.deleteById(id), () -> {
                    log.error(LogMessages.RESOURCE_NOT_FOUND, entity);
                    throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, entity));
                });

        return entity + " " + id + " is successfully deleted";
    }

    @Transactional
    @Override
    public void deleteNotifications(String nationalId) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Customer customer = customerService.findByNationalId(nationalId);
        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        notificationRepository.deleteAllByCustomer(customer);
    }
}
