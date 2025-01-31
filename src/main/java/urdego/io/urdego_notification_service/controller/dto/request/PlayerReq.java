package urdego.io.urdego_notification_service.controller.dto.request;

public record PlayerReq(
        String roomId,
        Long userId,
        Boolean isReady
) {
}
