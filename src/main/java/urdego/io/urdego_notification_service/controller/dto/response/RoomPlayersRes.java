package urdego.io.urdego_notification_service.controller.dto.response;

import urdego.io.urdego_notification_service.common.enums.Status;

import java.util.List;
import java.util.Map;

public record RoomPlayersRes(
        String roomId,
        Status status,
        List<String> currentPlayers,
        String isHost,
        Map<String, Boolean> readyStatus,
        Boolean allReady
) {
}
