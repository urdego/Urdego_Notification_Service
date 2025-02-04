package urdego.io.urdego_notification_service.domain.service;

import org.springframework.stereotype.Service;
import urdego.io.urdego_notification_service.controller.dto.request.NotificationRequest;
import urdego.io.urdego_notification_service.controller.dto.request.ReplyRequest;
import urdego.io.urdego_notification_service.controller.dto.response.NotificationResponse;
import urdego.io.urdego_notification_service.controller.dto.response.WebSocketMessageResponse;
import urdego.io.urdego_notification_service.controller.dto.request.notification.NotificationRequest;
import urdego.io.urdego_notification_service.controller.dto.WebSocketMessage;
import urdego.io.urdego_notification_service.domain.entity.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    //저장
    void saveNotification(Notification notification);

    //메세지 발행
    public WebSocketMessage<Notification> publishNotification(NotificationRequest notificationRequest);

    // 답장 및 읽음 상태 변경
    Notification updateReadStatus(ReplyRequest request, Long userId);

    //조회
    List<Notification> readNotificationList(Long userId);
}
