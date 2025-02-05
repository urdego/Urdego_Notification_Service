package urdego.io.urdego_notification_service.controller.dto.request.game;

public record ScoreReq(
        String gameId,
        int roundNum
) {
}
