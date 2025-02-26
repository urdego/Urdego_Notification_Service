package urdego.io.urdego_notification_service.controller.dto.response.game;

public record LevelRes(
        Long userId,
        int level,
        Long totalExp,
        boolean isLevelUp
) {
}
