package urdego.io.urdego_notification_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import urdego.io.urdego_notification_service.controller.client.GameServiceClient;
import urdego.io.urdego_notification_service.controller.dto.request.ContentSelectReq;
import urdego.io.urdego_notification_service.controller.dto.request.PlayerReq;
import urdego.io.urdego_notification_service.controller.dto.response.RoomPlayersRes;

@Controller
@RequiredArgsConstructor
public class NotificationSocketController {

    private final GameServiceClient gameServiceClient;
    private final SimpMessagingTemplate messagingTemplate;

    // 플레이어 참여
    @MessageMapping("/room/player/invite")
    public void invitePlayer(PlayerReq request) {
        RoomPlayersRes response = gameServiceClient.invitePlayer(request).getBody();
        messagingTemplate.convertAndSend("/urdego/sub/" + response.roomId(), response);
    }

    // 플레이어 삭제
    @MessageMapping("/room/player/remove")
    public void removePlayer(PlayerReq request) {
        RoomPlayersRes response = gameServiceClient.removePlayer(request).getBody();
        messagingTemplate.convertAndSend("/urdego/sub/" + response.roomId(), response);
    }

    // 플레이어 준비
    @MessageMapping("/room/player/ready")
    public void readyPlayer(PlayerReq request) {
        RoomPlayersRes response = gameServiceClient.readyPlayer(request).getBody();
        messagingTemplate.convertAndSend("/urdego/sub/" + response.roomId(), response);
    }

    // 컨텐츠 선택
    @MessageMapping("/room/select-content")
    public void selectContent(ContentSelectReq request) {
        gameServiceClient.selectContent(request);
    }

}
