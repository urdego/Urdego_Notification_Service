package urdego.io.urdego_notification_service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import urdego.io.urdego_notification_service.controller.dto.request.NotificationRequest;
import urdego.io.urdego_notification_service.controller.dto.response.NotificationResponse;
import urdego.io.urdego_notification_service.controller.dto.response.WebSocketMessageResponse;
import urdego.io.urdego_notification_service.domain.entity.Notification;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "urdego_notification:";
    private static final long EXPIRATION_TIME = 3;

    @Override
    public WebSocketMessageResponse<Notification> publishNotification(NotificationRequest request) {
        Notification notification = Notification.of(request);

        //프로토콜 감싸기
        WebSocketMessageResponse<Notification> message = new WebSocketMessageResponse<>(notification);
        simpMessagingTemplate.convertAndSend("/urdego/sub/notifications/" + notification.getTargetId(), message);
        log.info("Published notification : senderId {}, targetId {}  " , notification.getSenderId(), notification.getTargetId());

        //redis 저장
        saveNotification(notification);
        return message;
    }

    @Override
    public List<Object> getUserNotifications(Long userId) {
        return redisTemplate.opsForList().range(PREFIX + userId, 0, -1);
    }

    @Override
    public void updateReadStatus(Long userId, String lastReadMessageId) {

    }

    @Override
    public String getLastReadMessage(String userId) {
        return "";
    }

    @Override
    public void saveNotification(Notification notification) {
        String key = PREFIX + notification.getTargetId();
        redisTemplate.opsForList().rightPush(key, notification);
        redisTemplate.expire(key,EXPIRATION_TIME, TimeUnit.DAYS);
    }
}
