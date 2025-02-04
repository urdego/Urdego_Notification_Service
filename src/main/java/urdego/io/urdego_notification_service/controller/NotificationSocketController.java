package urdego.io.urdego_notification_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import urdego.io.urdego_notification_service.common.enums.MessageType;
import urdego.io.urdego_notification_service.controller.client.GameServiceClient;
import urdego.io.urdego_notification_service.controller.dto.request.ContentSelectReq;
import urdego.io.urdego_notification_service.controller.dto.request.PlayerReq;
import urdego.io.urdego_notification_service.controller.dto.request.WebSocketMessageReq;
import urdego.io.urdego_notification_service.controller.dto.response.WebSocketMessageRes;
import urdego.io.urdego_notification_service.controller.util.ReflectionUtil;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationSocketController {

    private final GameServiceClient gameServiceClient;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/room/event")
    public void handleRoomEvent(WebSocketMessageReq<?> request) {
        Object response = null;
        switch (request.messageType()) {
            case PLAYER_JOINED -> response = gameServiceClient.invitePlayer((PlayerReq) request.payload()).getBody();
            case PLAYER_REMOVED -> response = gameServiceClient.removePlayer((PlayerReq) request.payload()).getBody();
            case PLAYER_READY -> response = gameServiceClient.readyPlayer((PlayerReq) request.payload()).getBody();
            case CONTENT_SELECTED -> response = gameServiceClient.selectContent((ContentSelectReq) request.payload()).getBody();
        }
        sendMessage(response, request.messageType());
    }



    private <T> void sendMessage(T response, MessageType messageType) {
        if (response == null) {
            log.info("빈 응답이므로 기본 메시지를 전송합니다.");
            return;
        }
        try {
            String roomId = ReflectionUtil.getRoomIdFromResponse(response);
            messagingTemplate.convertAndSend(
                    "/urdego/sub/" + roomId,
                    new WebSocketMessageRes<>(messageType.name(), response)
            );
        } catch (IllegalArgumentException e) {
            log.error("roomId 조회 실패: {}", e.getMessage());
        }
    }
}
