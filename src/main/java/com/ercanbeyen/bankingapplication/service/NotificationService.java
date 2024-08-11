package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.dto.NotificationDto;

import java.util.concurrent.CompletableFuture;

public interface NotificationService {
    CompletableFuture<NotificationDto> createNotification(NotificationDto notificationDto);
    String deleteNotification(String id);
    void deleteNotifications(String nationalId);
}
