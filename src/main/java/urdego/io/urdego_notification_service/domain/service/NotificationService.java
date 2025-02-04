package urdego.io.urdego_notification_service.domain.service;

import urdego.io.urdego_notification_service.controller.dto.request.notification.NotificationRequest;
import urdego.io.urdego_notification_service.controller.dto.WebSocketMessage;
import urdego.io.urdego_notification_service.domain.entity.Notification;

import java.util.List;

public interface NotificationService {

    //메세지 발행
    public WebSocketMessage<Notification> publishNotification(NotificationRequest notificationRequest);

    //사용자 별 메세지 확인
    List<Object> getUserNotifications(Long userId);

    // 읽음 상태 저장
    void updateReadStatus(Long userId, String lastReadMessageId);

    //읽음 상태 조회
    String getLastReadMessage(String userId);

    void saveNotification(Notification notification);
}
