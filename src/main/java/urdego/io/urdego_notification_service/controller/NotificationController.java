package urdego.io.urdego_notification_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import urdego.io.urdego_notification_service.controller.dto.request.NotificationRequest;
import urdego.io.urdego_notification_service.controller.dto.response.NotificationResponse;
import urdego.io.urdego_notification_service.controller.dto.response.WebSocketMessageResponse;
import urdego.io.urdego_notification_service.domain.entity.Notification;
import urdego.io.urdego_notification_service.domain.service.NotificationService;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/notification-service/notifications")
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/send")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = WebSocketMessageResponse.class)))
    @Operation(summary = "게임초대 알림 전송",description = "userId로 게임초대 알림 전송")
    public ResponseEntity<WebSocketMessageResponse<Notification>> sendNotification(@RequestBody NotificationRequest request) {
        WebSocketMessageResponse<Notification> response = notificationService.publishNotification(request);
        return ResponseEntity.ok().body(response);

    }
}
