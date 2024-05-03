package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.dto.NotificationDto;
import com.ercanbeyen.bankingapplication.dto.response.MessageResponse;
import com.ercanbeyen.bankingapplication.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(@RequestBody @Valid NotificationDto request) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse<String>> deleteNotification(@PathVariable("id") String id) {
        MessageResponse<String> response = new MessageResponse<>(notificationService.deleteNotification(id));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteNotifications(@RequestParam("from") String nationalId) {
        notificationService.deleteNotifications(nationalId);
        return ResponseEntity.noContent()
                .build();
    }
}
