package urdego.io.urdego_notification_service.controller.dto.response.game;

import urdego.io.urdego_notification_service.common.enums.Status;

import java.util.List;
import java.util.Map;

public record GameEndRes(
        String gameId,
        String roomId,
        Status status,
        Map<Long, Integer> totalScores,
        List<Exp> expList,
        List<LevelRes> levelList
) {
    public record Exp(
            Long userId,
            Long exp
    ) {}
}
