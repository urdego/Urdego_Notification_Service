package urdego.io.urdego_notification_service.controller.dto.request;

import urdego.io.urdego_notification_service.common.enums.MessageType;

public record WebSocketMessageReq<T>(
        MessageType messageType,
        T payload
) {
}
