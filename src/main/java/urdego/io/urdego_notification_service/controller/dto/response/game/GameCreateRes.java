package urdego.io.urdego_notification_service.controller.dto.response.game;

import urdego.io.urdego_notification_service.common.enums.Status;

import java.util.List;

public record GameCreateRes(
        String gameId,
        String roomId,
        Status status,
        List<Long> players,
        List<String> questionIds
) {
}
