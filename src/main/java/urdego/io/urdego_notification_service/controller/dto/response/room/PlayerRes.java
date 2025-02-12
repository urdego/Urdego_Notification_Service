package urdego.io.urdego_notification_service.controller.dto.response.room;

public record PlayerRes(
        Long userId,
        String nickname,
        String activeCharacter,
        int level
) {
}
