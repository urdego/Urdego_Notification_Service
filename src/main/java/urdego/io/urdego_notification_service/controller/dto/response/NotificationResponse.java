package urdego.io.urdego_notification_service.controller.dto.response;

import java.util.UUID;

public record NotificationResponse(
        UUID NotificationId,

        String roomId,
        String roomName,

        Long senderId,
        String senderNickname,

        Long targetId,
        String targetNickname,

        String action
) {
}
