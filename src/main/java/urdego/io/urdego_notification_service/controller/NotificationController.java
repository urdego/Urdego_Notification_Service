package urdego.io.urdego_notification_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import urdego.io.urdego_notification_service.domain.entity.Notification;
import urdego.io.urdego_notification_service.domain.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("api/notification-service/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;
    private final Notification notification;

    @GetMapping("/{userId}")
    @Operation(summary = "알림 조회", description = "userId로 전달된 전체 알림 조회")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.readNotificationList(userId);
        return ResponseEntity.ok(notifications);
    }

}

