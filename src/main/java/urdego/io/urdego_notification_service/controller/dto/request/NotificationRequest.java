package urdego.io.urdego_notification_service.controller.dto.request;

import lombok.Builder;
import urdego.io.urdego_notification_service.domain.entity.Notification;

import java.io.Serializable;

@Builder
public record NotificationRequest(
        String roomId,
        String roomName,

        Long senderId,
        String senderNickname,

        Long targetId,
        String targetNickname,

        String action
) implements Serializable {
    public static NotificationRequest of(Notification notification) {
        return NotificationRequest.builder()
                .roomId(notification.getRoomId())
                .roomName(notification.getRoomName())
                .senderId(notification.getSenderId())
                .senderNickname(notification.getSenderNickname())
                .targetId(notification.getTargetId())
                .targetNickname(notification.getTargetNickname())
                .action(notification.getAction().toString())
                .build();
    }
}
