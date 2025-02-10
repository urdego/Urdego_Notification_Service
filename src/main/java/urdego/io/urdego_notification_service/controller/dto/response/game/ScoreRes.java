package urdego.io.urdego_notification_service.controller.dto.response.game;

import java.util.List;
import java.util.Map;

public record ScoreRes(
        String roomId,
        int roundNum,
        boolean isLast,
        List<PlayerScore> roundScore,
        List<PlayerScore> totalScore
) {
}
