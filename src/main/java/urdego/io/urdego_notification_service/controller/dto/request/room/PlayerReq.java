package urdego.io.urdego_notification_service.controller.dto.request.room;

public record PlayerReq(
        String roomId,
        Long userId,
        Boolean isReady
) {
}
