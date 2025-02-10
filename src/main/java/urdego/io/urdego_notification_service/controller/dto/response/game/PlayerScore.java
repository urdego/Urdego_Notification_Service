package urdego.io.urdego_notification_service.controller.dto.response.game;

public record PlayerScore(
        int rank,
        Long userId,
        String nickname,
        String characterType,
        int score
) {
}
