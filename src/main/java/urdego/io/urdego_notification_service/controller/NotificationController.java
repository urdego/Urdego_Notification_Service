package urdego.io.urdego_notification_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import urdego.io.urdego_notification_service.common.exception.notification.NotFoundNotification;
import urdego.io.urdego_notification_service.controller.dto.request.NotificationRequest;
import urdego.io.urdego_notification_service.controller.dto.request.ReplyRequest;
import urdego.io.urdego_notification_service.controller.dto.response.NotificationResponse;
import urdego.io.urdego_notification_service.controller.dto.response.WebSocketMessageResponse;
import urdego.io.urdego_notification_service.domain.entity.Notification;
import urdego.io.urdego_notification_service.domain.service.NotificationService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/notification-service/notifications")
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;
    private static final String PREFIX = "urdego_notification:";
    private final RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/send")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = WebSocketMessageResponse.class)))
    @Operation(summary = "게임초대 알림 전송",description = "userId로 게임초대 알림 전송")
    public ResponseEntity<WebSocketMessageResponse<Notification>> sendNotification(@RequestBody NotificationRequest request) {
        WebSocketMessageResponse<Notification> response = notificationService.publishNotification(request);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{userId}/reply")
    @Operation(summary = "알림 답장", description = "notificationId로 게임초대 알림에 대한 답변 받기")
    public ResponseEntity<Object> replyNotification(@PathVariable("userId") Long userId,
                                                  @RequestBody ReplyRequest request) {
        Notification updatedNotification = notificationService.updateReadStatus(request, userId);
        return ResponseEntity.ok().body(updatedNotification);
    }
}
