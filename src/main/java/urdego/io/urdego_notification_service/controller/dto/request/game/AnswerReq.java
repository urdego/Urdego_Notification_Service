package urdego.io.urdego_notification_service.controller.dto.request.game;

public record AnswerReq(
        String questionId,
        String userId,
        double latitude,
        double longitude
) {
}
