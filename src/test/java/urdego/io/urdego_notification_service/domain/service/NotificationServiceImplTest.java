package urdego.io.urdego_notification_service.domain.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import urdego.io.urdego_notification_service.common.enums.Action;
import urdego.io.urdego_notification_service.controller.dto.WebSocketMessage;
import urdego.io.urdego_notification_service.controller.dto.request.notification.NotificationRequest;
import urdego.io.urdego_notification_service.domain.entity.Notification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {
    @InjectMocks private NotificationServiceImpl notificationService;
    @Mock SimpMessagingTemplate messagingTemplate;

    @Mock private RedisTemplate<String, Object> redisTemplate;
    @Mock private ListOperations<String, Object> listOps;
    private static final String PREFIX = "urdego_notification:";

    @Test
    void publishNotification_shouldSave() {
        //give
        NotificationRequest dummyRequest = new NotificationRequest("123", "TestGameRoom", 1L, "TestHost", 2L, "TestGuest", "INVITE");
        Notification dummyNotification = Notification.of(dummyRequest);
        String key = PREFIX + dummyRequest.targetId();
        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.range(key,0,-1)).thenReturn(List.of(dummyNotification));
        //when
        //Redis 저장
        WebSocketMessage<Notification> result = notificationService.publishNotification(dummyRequest);

        //then
        List<Object> redisData = redisTemplate.opsForList().range(key, 0, -1);
        assertNotNull(redisData);
        assertEquals(1, redisData.size());

        Notification resultMessage = (Notification) redisData.get(0);
        verify(messagingTemplate).convertAndSend("/urdego/sub/notifications/" + dummyNotification.getTargetId(), result);
        assertEquals(dummyNotification.getNotificationId(), resultMessage.getNotificationId());
        assertEquals(dummyRequest.targetId(), resultMessage.getTargetId());
        assertEquals(dummyRequest.senderId(), resultMessage.getSenderId());
        assertEquals(Action.valueOf("INVITE"), resultMessage.getAction());
    }

    @Test
    void updateReadStatus() {
    }

    @Test
    void saveNotification() {
    }

    @Test
    void readNotificationList() {
    }

    @Test
    void tryLockNotification() {
    }
}