package urdego.io.urdego_notification_service.controller.dto.response.game;

public record AnswerRes(
        String roomId,
        String questionId,
        Long userId,
        double latitude,
        double longitude
) {
}
