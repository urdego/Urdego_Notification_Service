package urdego.io.urdego_notification_service.controller.dto.request;

import java.util.UUID;

public record ReplyRequest(
        boolean isAccepted,
        String notificationId
) {
}
