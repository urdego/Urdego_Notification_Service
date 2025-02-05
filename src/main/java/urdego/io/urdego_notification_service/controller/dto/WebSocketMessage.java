package urdego.io.urdego_notification_service.controller.dto;

import urdego.io.urdego_notification_service.common.enums.MessageType;

public record WebSocketMessage<T>(
        MessageType messageType,
        T payload
) {
}
