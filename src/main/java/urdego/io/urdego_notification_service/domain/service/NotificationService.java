package urdego.io.urdego_notification_service.domain.service;

import org.springframework.stereotype.Service;
import urdego.io.urdego_notification_service.controller.dto.request.NotificationRequest;
import urdego.io.urdego_notification_service.controller.dto.response.NotificationResponse;
import urdego.io.urdego_notification_service.controller.dto.response.WebSocketMessageResponse;
import urdego.io.urdego_notification_service.domain.entity.Notification;

import java.util.List;

public interface NotificationService {

    //메세지 발행
    public WebSocketMessageResponse<Notification> publishNotification(NotificationRequest notificationRequest);

    //사용자 별 메세지 확인
    List<Object> getUserNotifications(Long userId);

    // 읽음 상태 저장
    void updateReadStatus(Long userId, String lastReadMessageId);

    //읽음 상태 조회
    String getLastReadMessage(String userId);

    void saveNotification(Notification notification);
}
