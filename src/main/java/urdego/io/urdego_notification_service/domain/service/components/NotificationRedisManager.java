package urdego.io.urdego_notification_service.domain.service.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import urdego.io.urdego_notification_service.common.exception.notification.AlreadyAcceptedNotification;
import urdego.io.urdego_notification_service.common.exception.notification.InvalidNotificationId;
import urdego.io.urdego_notification_service.common.exception.notification.NotFoundNotification;
import urdego.io.urdego_notification_service.controller.dto.request.ReplyRequest;
import urdego.io.urdego_notification_service.domain.entity.Notification;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRedisManager {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String PREFIX = "urdego_notification:";
    private static final long EXPIRATION_TIME = 2; //2일

    public void saveNotification(Notification notification) {
        String key = genKey(notification.getTargetId());

        redisTemplate.opsForList().rightPush(key, notification);
        redisTemplate.expire(key,EXPIRATION_TIME, TimeUnit.DAYS);
        log.info("save Notification : Notification ID {}", notification.getNotificationId());
    }


    public List<Notification> readNotificationList(Long userId) {
        String key = genKey(userId);

        List<Object> rawNotification = redisTemplate.opsForList().range(key, 0, -1);
        log.info("rawNotification List<Obj> : {} ", rawNotification.size());
        if(rawNotification == null || rawNotification.size() == 0) { throw NotFoundNotification.EXCEPTION;}

        //Object -> Notification
        List<Notification> notifications = rawNotification.stream().map(obj -> objectMapper.convertValue(obj, Notification.class))
                .collect(Collectors.toList());

        return notifications;
    }

    public Notification updateRedisStatus(ReplyRequest request) {
        // 키 생성
        String key = genKey(request.userId());
        List<Notification> notifications = readNotificationList(request.userId());

        //notificationId의 알림 index 찾기 없으면 Exception!!
        int index = IntStream.range(0, notifications.size())
                .filter(i -> notifications.get(i).getNotificationId().toString().equals(request.notificationId()))
                .findFirst().orElseThrow(() -> InvalidNotificationId.EXCEPTION);

        Notification updatedNotification = notifications.get(index);

        // 락
        boolean isLock = tryLockNotification(updatedNotification.getNotificationId());
        //이미 응답을 했다면 그냥 넘어가기
        if (updatedNotification.isAccepted()) {
            log.info("Notification {} already accepted. Skipping re-processing. in NotificationRedisManager", updatedNotification.getNotificationId());
            return updatedNotification;
        }
        if (!isLock) {
            log.warn("Notification {} already checked notification in NotificationRedisManager", updatedNotification.getNotificationId());
            throw AlreadyAcceptedNotification.EXCEPTION;
        }

        updatedNotification.updateReply(request.isAccepted());

        //redis에 수정사항 저장
        redisTemplate.opsForList().set(key,index, updatedNotification);
        log.info("Reply notification : senderId {}, targetId {}  in NotificationRedisManager" , updatedNotification.getSenderId(), updatedNotification.getTargetId());
        return updatedNotification;
    }

    public boolean tryLockNotification(UUID notificationId) {
        String lockKey = "lock:notification:" + notificationId;
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", Duration.ofSeconds(3))
        );
    }

    public static String genKey(Long userId){
        return PREFIX+userId;
    }
}
