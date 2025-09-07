package urdego.io.urdego_notification_service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import urdego.io.urdego_notification_service.common.enums.MessageType;
import urdego.io.urdego_notification_service.common.exception.notification.NotificationSendFailed;
import urdego.io.urdego_notification_service.controller.client.GameServiceClient;
import urdego.io.urdego_notification_service.controller.dto.WebSocketMessage;
import urdego.io.urdego_notification_service.controller.dto.request.ReplyRequest;
import urdego.io.urdego_notification_service.controller.dto.request.notification.NotificationRequest;
import urdego.io.urdego_notification_service.domain.entity.Notification;
import urdego.io.urdego_notification_service.domain.service.components.NotificationRedisManager;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRedisManager redisManager;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameServiceClient gameServiceClient;


    @Override
    public WebSocketMessage<Notification> publishNotification(NotificationRequest request) {
        Notification notification = Notification.of(request);
        //메세지 Redis에 저장
        redisManager.saveNotification(notification);

        //프로토콜 감싸기
        WebSocketMessage<Notification> message = new WebSocketMessage<>(MessageType.INVITE_PLAYER, notification);
        try {
            simpMessagingTemplate.convertAndSend("/urdego/sub/notifications/" + notification.getTargetId(), message);
            log.info("Published notification : senderId {}, targetId {}  " , notification.getSenderId(), notification.getTargetId());
        } catch (Exception e){
            //@todo : Fallback 메서드가 필요하지 않을까? 재전송 로직
            log.error("Notification Send Fail in NotificationService : notification ID {} ", notification.getNotificationId());
            throw NotificationSendFailed.EXCEPTION;
        }

        return message;
    }

    @Override
    public Notification updateStatus(ReplyRequest request) {
        Notification updatedNotification = redisManager.updateRedisStatus(request);
        simpMessagingTemplate.convertAndSend("/urdego/sub/notifications/" + updatedNotification.getTargetId(), updatedNotification);
        return updatedNotification;
    }


    @Override
    public List<Notification> readNotificationList(Long userId) {
        List<Notification> notificationList = redisManager.readNotificationList(userId);
        return notificationList;
    }

}
