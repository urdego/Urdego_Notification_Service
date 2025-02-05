package urdego.io.urdego_notification_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import urdego.io.urdego_notification_service.controller.dto.WebSocketMessage;
import urdego.io.urdego_notification_service.controller.dto.request.ReplyRequest;
import urdego.io.urdego_notification_service.controller.dto.request.notification.NotificationRequest;
import urdego.io.urdego_notification_service.domain.entity.Notification;
import urdego.io.urdego_notification_service.domain.service.NotificationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/notification-service/notifications")
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;
    //TODO : Response에 Entity를 리턴하는 행위는 좋지 않음 차후 NotificationResponse로 수정해야 함

    @MessageMapping("/send")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = WebSocketMessage.class)))
    @Operation(summary = "게임초대 알림 전송",description = "userId로 게임초대 알림 전송")
    public ResponseEntity<WebSocketMessage<Notification>> sendNotification(@RequestBody NotificationRequest request) {
        //uri가 send로 나눌 필요가 있나? {userId}로 해도 되지 않나? or PK는 숨기는 좋을까?
        WebSocketMessage<Notification> response = notificationService.publishNotification(request);
        return ResponseEntity.ok().body(response);
    }

    @MessageMapping("/{userId}/reply")
    @Operation(summary = "알림 답장", description = "notificationId로 게임초대 알림에 대한 답변 받기")
    public ResponseEntity<Object> replyNotification(@DestinationVariable("userId") Long userId,
                                                  @RequestBody ReplyRequest request) {
        Notification updatedNotification = notificationService.updateReadStatus(request, userId);
        return ResponseEntity.ok().body(updatedNotification);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "알림 조회", description = "userId로 해당 유저에게 전달된 전체 알림 조회")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable("userId") Long userId) {

        List<Notification> notifications = notificationService.readNotificationList(userId);
        return ResponseEntity.ok().body(notifications);
    }
}
