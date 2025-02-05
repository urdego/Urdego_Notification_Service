package urdego.io.urdego_notification_service.controller.dto.response.game;

import urdego.io.urdego_notification_service.common.enums.Status;

import java.util.Map;

public record GameEndRes(
        String gameId,
        String roomId,
        Status status,
        Map<String, Integer> totalScores,
        Map<String, Integer> exp
) {
}
