package urdego.io.urdego_notification_service.controller.dto.request.game;

public record CoordinateReq(
        String roomId,
        int roundNum,
        String questionId
) {
}
