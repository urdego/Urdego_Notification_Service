package urdego.io.urdego_notification_service.controller.dto.response.game;

import java.util.Map;

public record ScoreRes(
        String roomId,
        Map<Integer, Map<String, Integer>> roundScores,
        Map<String, Integer> totalScores
) {
}
