package urdego.io.urdego_notification_service.controller.dto.request;

import java.util.UUID;

public record ReplyRequest(
        Long userId,
        boolean isAccepted,
        String notificationId
) {
}
