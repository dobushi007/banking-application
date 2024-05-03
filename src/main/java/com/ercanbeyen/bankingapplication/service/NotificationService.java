package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.dto.NotificationDto;

public interface NotificationService {
    NotificationDto createNotification(NotificationDto notificationDto);
    String deleteNotification(String id);
    void deleteNotifications(String nationalId);
}
