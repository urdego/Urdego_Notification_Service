package urdego.io.urdego_notification_service.controller.dto.response.game;

import java.util.List;

public record QuestionRes(
        String questionId,
        String roomId,
        int roundNum,
        double latitude,
        double longitude,
        String hint,
        List<String> contents
) {
}
