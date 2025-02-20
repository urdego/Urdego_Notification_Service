package urdego.io.urdego_notification_service.controller.dto.request.room;

import java.util.List;

public record ContentSelectReq(
        String roomId,
        Long userId,
        List<String> contentIds
) {
}
