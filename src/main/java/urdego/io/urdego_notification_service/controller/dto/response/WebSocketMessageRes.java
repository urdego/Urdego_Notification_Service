package urdego.io.urdego_notification_service.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMessageRes<T> {
    private String messageType;
    private T data;
}
