package urdego.io.urdego_notification_service.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import urdego.io.urdego_notification_service.common.enums.MessageType;
import urdego.io.urdego_notification_service.common.exception.notification.NotificationSendFailed;
import urdego.io.urdego_notification_service.controller.dto.WebSocketMessage;
import urdego.io.urdego_notification_service.controller.dto.request.ReplyRequest;
import urdego.io.urdego_notification_service.controller.dto.request.notification.NotificationRequest;
import urdego.io.urdego_notification_service.domain.entity.Notification;
import urdego.io.urdego_notification_service.domain.service.components.NotificationRedisManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {
    @InjectMocks private NotificationServiceImpl notificationService;

    @Mock private NotificationRedisManager notificationRedisManager;
    @Mock SimpMessagingTemplate messagingTemplate;

    @Test
    @DisplayName("메세지 전송 성공")
    void publishNotification_shouldSendMessageThenReturnMessage(){
        NotificationRequest dummyRequest = new NotificationRequest("123", "TestGameRoom", 1L, "TestHost", 2L, "TestGuest", "INVITE");
        Notification dummyNotification = Notification.of(dummyRequest);

        doNothing().when(notificationRedisManager).saveNotification(any(Notification.class));
        doNothing().when(messagingTemplate).convertAndSend(anyString(), Optional.ofNullable(any()));

        //when
        WebSocketMessage<Notification> result = notificationService.publishNotification(dummyRequest);

        //then
        assertNotNull(result);
        assertEquals(MessageType.INVITE_PLAYER, result.messageType());
        assertEquals(dummyRequest.roomId(), result.payload().getRoomId());

        verify(notificationRedisManager).saveNotification(any(Notification.class));
        verify(messagingTemplate).convertAndSend(
                eq("/urdego/sub/notifications/" + dummyRequest.targetId())
                , any(WebSocketMessage.class));
    }

    @Test
    @DisplayName("메세지 전송 실패")
    void publishNotification_shouldSendMessageThenReturnNotificationSendFailedException(){
        //given
        NotificationRequest dummyRequest = new NotificationRequest("123", "TestGameRoom", 1L, "TestHost", 2L, "TestGuest", "INVITE");
        Notification dummyNotification = Notification.of(dummyRequest);

        doNothing().when(notificationRedisManager).saveNotification(any(Notification.class));
        doThrow(new RuntimeException("WebSocket error"))
                .when(messagingTemplate)
                .convertAndSend(anyString(), any(WebSocketMessage.class));

        //when
        assertThrows(NotificationSendFailed.class, ()-> notificationService.publishNotification(dummyRequest));
        verify(notificationRedisManager).saveNotification(any(Notification.class));
        verify(messagingTemplate).convertAndSend(anyString(), any(WebSocketMessage.class));
    }

    @Test
    @DisplayName("알림 답변 성공")
    void updateReadStatus_shouldReplyNotificationThenReturnUpdatedNotification(){
        //given
        NotificationRequest dummyRequest = new NotificationRequest("123", "TestGameRoom", 1L, "TestHost", 2L, "TestGuest", "INVITE");
        Notification dummyNotification = Notification.of(dummyRequest);
        ReplyRequest dummyReplyRequest = new ReplyRequest(2L, true, dummyNotification.getNotificationId().toString());

        dummyNotification.updateReply(Boolean.TRUE);

        when(notificationRedisManager.updateRedisStatus(any(ReplyRequest.class))).thenReturn(dummyNotification);
        doNothing().when(messagingTemplate).convertAndSend(anyString(), any(Notification.class));

        //when
        Notification result = notificationService.updateStatus(dummyReplyRequest);

        //then
        assertTrue(result.isAccepted());
        assertTrue(result.isRead());
        verify(messagingTemplate).convertAndSend(eq("/urdego/sub/notifications/" + result.getTargetId()), eq(dummyNotification));
    }

    @Test
    @DisplayName("알림 리스트 조회 성공")
    void readNotificationList_should(){
        NotificationRequest dummyRequest = new NotificationRequest("123", "TestGameRoom",
                1L, "TestHost", 2L, "TestGuest", "INVITE");
        Notification dummyNotification = Notification.of(dummyRequest);

        Long userId = dummyNotification.getTargetId();

        List<Notification> expectedList = List.of(dummyNotification);
        when(notificationService.readNotificationList(userId)).thenReturn(expectedList);

        List<Notification> resultList = notificationService.readNotificationList(dummyNotification.getTargetId());

        assertEquals(1, resultList.size());
        verify(notificationRedisManager).readNotificationList(userId);
    }
}