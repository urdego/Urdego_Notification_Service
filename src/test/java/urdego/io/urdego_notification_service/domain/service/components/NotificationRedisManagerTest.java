package urdego.io.urdego_notification_service.domain.service.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import urdego.io.urdego_notification_service.common.exception.notification.AlreadyAcceptedNotification;
import urdego.io.urdego_notification_service.common.exception.notification.InvalidNotificationId;
import urdego.io.urdego_notification_service.common.exception.notification.NotFoundNotification;
import urdego.io.urdego_notification_service.controller.dto.request.ReplyRequest;
import urdego.io.urdego_notification_service.controller.dto.request.notification.NotificationRequest;
import urdego.io.urdego_notification_service.domain.entity.Notification;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class NotificationRedisManagerTest {
    @Mock private RedisTemplate<String, Object> redisTemplate;
    @Mock private ListOperations<String, Object> listOps;
    @Mock private ValueOperations <String, Object> valueOps;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String PREFIX = "urdego_notification:";


    @Test
    @DisplayName("알림 저장 시 요소들이 옳바를 때")
    void saveNotification_shouldSave() {
        //given
        NotificationRequest dummyRequest = new NotificationRequest("123", "TestGameRoom", 1L, "TestHost", 2L, "TestGuest", "INVITE");
        Notification dummyNotification = Notification.of(dummyRequest);
        String key = PREFIX + dummyNotification.getTargetId();

        NotificationRedisManager notificationRedisManager = new NotificationRedisManager(redisTemplate, objectMapper);
        when(redisTemplate.opsForList()).thenReturn(listOps);
        //when
        notificationRedisManager.saveNotification(dummyNotification);

        //then
        verify(redisTemplate).opsForList();
        verify(listOps).rightPush(key, dummyNotification);
        verify(redisTemplate).expire(eq(key), eq(2L), eq(TimeUnit.DAYS));
    }

    @Test
    @DisplayName("조회한 리스트가 비어있거나 사이즈가 0일 때 NotFoundNotification 예외 발생")
    void readNotificationList_shouldEmptyListsThenReturnNotFoundNotificationException(){
        //given
        Long userId = 1L;
        String key = PREFIX + userId;
        NotificationRedisManager notificationRedisManager = new NotificationRedisManager(redisTemplate, objectMapper);
        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.range(eq(key), eq(0L), eq(-1L))).thenReturn(Collections.EMPTY_LIST);

        //then
        NotFoundNotification ex = assertThrows(NotFoundNotification.class, () -> notificationRedisManager.readNotificationList(userId));
        assertEquals("해당 알림을 찾을 수 없습니다.", ex.getMessage());
        verify(redisTemplate).opsForList();
        verify(listOps).range(key, 0, -1);
    }

    @Test
    @DisplayName("조회한 리스트가 정상적 일 때 Notification 리턴")
    void readNotificationList_shouldNotEmptyListThenReturnNotification(){
        //given
        NotificationRequest dummyRequest = new NotificationRequest("123", "TestGameRoom", 1L, "TestHost", 2L, "TestGuest", "INVITE");
        Notification dummyNotification = Notification.of(dummyRequest);
        String key = PREFIX + dummyNotification.getTargetId();

        List<Object> dummyList = List.of(dummyNotification);
        ObjectMapper realObjMapper = new ObjectMapper();
        NotificationRedisManager manager = new NotificationRedisManager(redisTemplate, realObjMapper);
        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.range(eq(key), eq(0L), eq(-1L))).thenReturn(dummyList);


        //when
        List<Notification> result = manager.readNotificationList(dummyNotification.getTargetId());

        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dummyNotification.getNotificationId(), result.get(0).getNotificationId());

        verify(redisTemplate).opsForList();
        verify(listOps).range(key, 0, -1);
    }

    @Test
    @DisplayName("락 시도 시 정상 락 획득 TRUE")
    void tryLockNotification_shouldLockReturnTrue(){
        //given
        NotificationRedisManager redisManager = new NotificationRedisManager(redisTemplate,objectMapper);
        Long userId = 2L;
        UUID notificationId = UUID.randomUUID();
        String lockKey = "lock:notification:" + notificationId;

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(eq(lockKey), eq("LOCKED"), eq(Duration.ofSeconds(3)))).thenReturn(Boolean.TRUE);
        //when
        Boolean result = redisManager.tryLockNotification(notificationId);

        assertEquals(Boolean.TRUE, result);
        verify(valueOps).setIfAbsent(eq(lockKey), eq("LOCKED"), eq(Duration.ofSeconds(3)));
    }

    @Test
    @DisplayName("락 시도 시 정상 락 획득 실패 FALSE")
    void tryLockNotification_shouldLockReturnFalse(){
        //given
        NotificationRedisManager redisManager = new NotificationRedisManager(redisTemplate,objectMapper);
        Long userId = 2L;
        UUID notificationId = UUID.randomUUID();
        String lockKey = "lock:notification:" + notificationId;

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(eq(lockKey), eq("LOCKED"), eq(Duration.ofSeconds(3)))).thenReturn(Boolean.FALSE);
        //when
        Boolean result = redisManager.tryLockNotification(notificationId);

        assertEquals(Boolean.FALSE, result);
        verify(valueOps).setIfAbsent(eq(lockKey), eq("LOCKED"), eq(Duration.ofSeconds(3)));
    }

    @Test
    @DisplayName("존재하지 않은 Notification-ID일 경우 InvalidNotificationId 예외 발생")
    void updateStatus_shouldSearchListThenReturnInvalidNotificationException(){
        //given
        NotificationRequest dummyRequest = new NotificationRequest("123", "TestGameRoom", 1L, "TestHost", 2L, "TestGuest", "INVITE");
        Notification dummyNotification = Notification.of(dummyRequest);
        ReplyRequest dummyReplyRequest = new ReplyRequest(2L, true, "nonExistNotificationId");
        String key = PREFIX + dummyNotification.getTargetId();

        NotificationRedisManager notificationRedisManager = new NotificationRedisManager(redisTemplate, objectMapper);

        List<Object> dummyNotificationList = List.of(dummyNotification);
        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.range(eq(key), eq(0L), eq(-1L))).thenReturn(dummyNotificationList);

        //when
        InvalidNotificationId ex = assertThrows(InvalidNotificationId.class, ()->notificationRedisManager.updateRedisStatus(dummyReplyRequest));
        assertEquals("잘못된 알림 ID 입니다.", ex.getMessage());
    }

    @Test
    @DisplayName("이미 응답한 알림일 경우 알림 객체 반환")
    void updateStatus_shouldAlreadyReplyNotificationThenReturnNotification(){
        //given
        NotificationRequest dummyRequest = new NotificationRequest("123", "TestGameRoom", 1L, "TestHost", 2L, "TestGuest", "INVITE");
        Notification dummyNotification = Notification.of(dummyRequest);

        dummyNotification.updateReply(Boolean.TRUE);

        ReplyRequest dummyReplyRequest = new ReplyRequest(2L, true, dummyNotification.getNotificationId().toString());
        String key = PREFIX + dummyNotification.getTargetId();
        String lockKey = "lock:notification:" + dummyNotification.getNotificationId();
        NotificationRedisManager notificationRedisManager = new NotificationRedisManager(redisTemplate, objectMapper);
        List<Object> dummyNotificationList = List.of(dummyNotification);

        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.range(eq(key), eq(0L), eq(-1L))).thenReturn(dummyNotificationList);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(eq(lockKey), eq("LOCKED"), eq(Duration.ofSeconds(3)))).thenReturn(Boolean.TRUE);

        Notification result = notificationRedisManager.updateRedisStatus(dummyReplyRequest);
        Notification expect = (Notification) dummyNotificationList.get(0);
        assertEquals(expect.getNotificationId(), result.getNotificationId());
    }

    @Test
    @DisplayName("락을 획득하지 못했다면 예외 발생")
    void updateStatus_shouldGetLockFailThenReturnAlreadyAcceptedNotificationException(){
        //given
        NotificationRequest dummyRequest = new NotificationRequest("123", "TestGameRoom", 1L, "TestHost", 2L, "TestGuest", "INVITE");
        Notification dummyNotification = Notification.of(dummyRequest);


        ReplyRequest dummyReplyRequest = new ReplyRequest(2L, true, dummyNotification.getNotificationId().toString());
        String key = PREFIX + dummyNotification.getTargetId();

        String lockKey = "lock:notification:" + dummyNotification.getNotificationId();
        NotificationRedisManager notificationRedisManager = new NotificationRedisManager(redisTemplate, objectMapper);
        List<Object> dummyNotificationList = List.of(dummyNotification);

        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.range(eq(key), eq(0L), eq(-1L))).thenReturn(dummyNotificationList);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(eq(lockKey), eq("LOCKED"), eq(Duration.ofSeconds(3)))).thenReturn(Boolean.FALSE);

        assertThrows(AlreadyAcceptedNotification.class, ()->notificationRedisManager.updateRedisStatus(dummyReplyRequest));

    }

    @Test
    @DisplayName("알림 수락 시 내용 업데이트")
    void updateStatus_shouldGetLockAndUpdateEntityThenReturnUpdateNotification(){
        //given
        NotificationRequest dummyRequest = new NotificationRequest("123", "TestGameRoom", 1L, "TestHost", 2L, "TestGuest", "INVITE");
        Notification dummyNotification = Notification.of(dummyRequest);


        ReplyRequest dummyReplyRequest = new ReplyRequest(2L, true, dummyNotification.getNotificationId().toString());
        String key = PREFIX + dummyNotification.getTargetId();

        String lockKey = "lock:notification:" + dummyNotification.getNotificationId();
        NotificationRedisManager notificationRedisManager = new NotificationRedisManager(redisTemplate, objectMapper);
        List<Object> dummyNotificationList = List.of(dummyNotification);

        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.range(eq(key), eq(0L), eq(-1L))).thenReturn(dummyNotificationList);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(eq(lockKey), eq("LOCKED"), eq(Duration.ofSeconds(3)))).thenReturn(Boolean.TRUE);

        Notification result = notificationRedisManager.updateRedisStatus(dummyReplyRequest);
        assertTrue(result.isAccepted());
        assertTrue(result.isRead());
    }
}