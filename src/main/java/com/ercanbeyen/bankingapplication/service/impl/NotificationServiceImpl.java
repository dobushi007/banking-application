package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.dto.NotificationDto;
import com.ercanbeyen.bankingapplication.entity.Customer;
import com.ercanbeyen.bankingapplication.entity.Notification;
import com.ercanbeyen.bankingapplication.mapper.NotificationMapper;
import com.ercanbeyen.bankingapplication.repository.NotificationRepository;
import com.ercanbeyen.bankingapplication.service.NotificationService;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final CustomerService customerService;

    @Override
    public NotificationDto createNotification(NotificationDto notificationDto) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod())
        );

        Notification notification = notificationMapper.dtoToNotification(notificationDto);
        Customer customer = customerService.findCustomerByNationalId(notificationDto.customerNationalId());
        notification.setCustomer(customer);

        return notificationMapper.notificationToDto(notificationRepository.save(notification));
    }
}
