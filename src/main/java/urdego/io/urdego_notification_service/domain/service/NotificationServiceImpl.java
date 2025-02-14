package urdego.io.urdego_notification_service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import urdego.io.urdego_notification_service.common.enums.MessageType;
import urdego.io.urdego_notification_service.common.exception.notification.InvalidNotificationId;
import urdego.io.urdego_notification_service.common.exception.notification.NotFoundNotification;
import urdego.io.urdego_notification_service.controller.client.GameServiceClient;
import urdego.io.urdego_notification_service.controller.dto.WebSocketMessage;
import urdego.io.urdego_notification_service.controller.dto.request.ReplyRequest;
import urdego.io.urdego_notification_service.controller.dto.request.notification.NotificationRequest;
import urdego.io.urdego_notification_service.domain.entity.Notification;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final GameServiceClient gameServiceClient;
    private static final String PREFIX = "urdego_notification:";
    private static final long EXPIRATION_TIME = 2; //2일

    @Override
    public WebSocketMessage<Notification> publishNotification(NotificationRequest request) {
        Notification notification = Notification.of(request);
        //프로토콜 감싸기
        WebSocketMessage<Notification> message = new WebSocketMessage<>(MessageType.INVITE_PLAYER, notification);
        simpMessagingTemplate.convertAndSend("/urdego/sub/notifications/" + notification.getTargetId(), message);
        log.info("Published notification : senderId {}, targetId {}  " , notification.getSenderId(), notification.getTargetId());

        //redis 저장
        saveNotification(notification);
        return message;
    }

    @Override
    public Notification updateReadStatus(ReplyRequest request) {
        // 키 생성
        String key = PREFIX + request.userId();

        List<Notification> notifications = readNotificationList(request.userId());

        //notificationId의 알림 index 찾기 없으면 Exception!!
        int index = IntStream.range(0, notifications.size())
                .filter(i -> notifications.get(i).getNotificationId().toString().equals(request.notificationId()))
                .findFirst().orElseThrow(() -> InvalidNotificationId.EXCEPTION);

        Notification updatedNotification = notifications.get(index);
        updatedNotification.updateReply(request.isAccepted());
        simpMessagingTemplate.convertAndSend("/urdego/sub/notifications/" + updatedNotification.getTargetId(), updatedNotification);

        //redis에 수정사항 저장
        //TODO 수정 후 redis에 저장이 안됨..;;
        redisTemplate.opsForList().set(key,index, updatedNotification);
        return updatedNotification;
    }

    @Override
    public void saveNotification(Notification notification) {
        String key = PREFIX + notification.getTargetId();
        redisTemplate.opsForList().rightPush(key, notification);
        redisTemplate.expire(key,EXPIRATION_TIME, TimeUnit.DAYS);
    }

    @Override
    public List<Notification> readNotificationList(Long userId) {
        // 키 생성
        String key = PREFIX + userId;

        List<Object> rawNotification = redisTemplate.opsForList().range(key, 0, -1);
        if(rawNotification == null || rawNotification.size() == 0) { throw NotFoundNotification.EXCEPTION;}

        //Object -> Notification
        List<Notification> notifications = rawNotification.stream().filter(obj -> obj instanceof Notification)
                .map(obj -> (Notification) obj).collect(Collectors.toList());

        return notifications;
    }
}
