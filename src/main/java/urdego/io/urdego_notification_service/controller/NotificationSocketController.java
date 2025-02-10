package urdego.io.urdego_notification_service.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import urdego.io.urdego_notification_service.common.enums.MessageType;
import urdego.io.urdego_notification_service.controller.client.GameServiceClient;
import urdego.io.urdego_notification_service.controller.dto.WebSocketMessage;
import urdego.io.urdego_notification_service.controller.dto.request.game.AnswerReq;
import urdego.io.urdego_notification_service.controller.dto.request.game.QuestionReq;
import urdego.io.urdego_notification_service.controller.dto.request.game.ScoreReq;
import urdego.io.urdego_notification_service.controller.dto.request.room.ContentSelectReq;
import urdego.io.urdego_notification_service.controller.dto.request.room.PlayerReq;
import urdego.io.urdego_notification_service.controller.util.ReflectionUtil;

import java.sql.Ref;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationSocketController {

    private final GameServiceClient gameServiceClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MessageMapping("/room/event")
    public void handleRoomEvent(WebSocketMessage<?> request) {
        Object response = null;
        switch (request.messageType()) {
            case PLAYER_JOIN -> response = gameServiceClient.invitePlayer(objectMapper.convertValue(request.payload(), PlayerReq.class)).getBody();
            case PLAYER_REMOVE -> response = gameServiceClient.removePlayer(objectMapper.convertValue(request.payload(), PlayerReq.class)).getBody();
            case PLAYER_READY -> response = gameServiceClient.readyPlayer(objectMapper.convertValue(request.payload(), PlayerReq.class)).getBody();
            case CONTENT_SELECT -> response = gameServiceClient.selectContent(objectMapper.convertValue(request.payload(), ContentSelectReq.class)).getBody();
        }
        sendMessage(response, request.messageType());
    }

    @MessageMapping("/game/event")
    public void handleGameEvent(WebSocketMessage<?> request) {
        Object response = null;
        switch (request.messageType()) {
            case SCORE_UPDATE -> response = gameServiceClient.giveScores(objectMapper.convertValue(request.payload(), ScoreReq.class)).getBody();
            case GAME_END -> response = gameServiceClient.endGame(objectMapper.convertValue(request.payload(), new TypeReference<Map<String, String>>() {}).get("gameId")).getBody();
            case QUESTION_GIVE -> response = gameServiceClient.giveQuestion(objectMapper.convertValue(request.payload(), QuestionReq.class)).getBody();
            case ANSWER_SUBMIT -> response = gameServiceClient.submitAnswer(objectMapper.convertValue(request.payload(), AnswerReq.class)).getBody();
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
                    new WebSocketMessage<>(messageType, response)
            );
        } catch (IllegalArgumentException e) {
            log.error("roomId 조회 실패: {}", e.getMessage());
        }
    }
}
