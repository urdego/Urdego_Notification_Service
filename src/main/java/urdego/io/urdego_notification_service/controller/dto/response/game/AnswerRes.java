package urdego.io.urdego_notification_service.controller.dto.response.game;

public record AnswerRes(
        String questionId,
        String userId,
        double latitude,
        double longitude
) {
}
