package urdego.io.urdego_notification_service.controller.dto.request;

import java.util.List;

public record ContentSelectReq(
        String roomId,
        String userId,
        List<String> contentIds
) {
}
