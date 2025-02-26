package urdego.io.urdego_notification_service.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import urdego.io.urdego_notification_service.common.enums.MessageType;
import urdego.io.urdego_notification_service.controller.client.GameServiceClient;
import urdego.io.urdego_notification_service.controller.dto.WebSocketMessage;
import urdego.io.urdego_notification_service.controller.dto.request.game.*;
import urdego.io.urdego_notification_service.controller.dto.request.ReplyRequest;
import urdego.io.urdego_notification_service.controller.dto.request.notification.NotificationRequest;
import urdego.io.urdego_notification_service.controller.dto.request.room.ContentSelectReq;
import urdego.io.urdego_notification_service.controller.dto.request.room.PlayerReq;
import urdego.io.urdego_notification_service.controller.util.ReflectionUtil;
import urdego.io.urdego_notification_service.domain.service.NotificationService;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationSocketController {

    private final GameServiceClient gameServiceClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NotificationService notificationService;

    @MessageMapping("/room/event")
    public void handleRoomEvent(WebSocketMessage<?> request) {
        Object response = null;
        try {
            switch (request.messageType()) {
                case PLAYER_JOIN -> response = gameServiceClient.invitePlayer(objectMapper.convertValue(request.payload(), PlayerReq.class)).getBody();
                case PLAYER_REMOVE -> response = gameServiceClient.removePlayer(objectMapper.convertValue(request.payload(), PlayerReq.class)).getBody();
                case PLAYER_READY -> response = gameServiceClient.readyPlayer(objectMapper.convertValue(request.payload(), PlayerReq.class)).getBody();
                case CONTENT_SELECT -> response = gameServiceClient.selectContent(objectMapper.convertValue(request.payload(), ContentSelectReq.class)).getBody();
            }
            sendMessage(response, request.messageType());
        } catch (FeignException e) {
            log.error("Feign Client 요청 실패: {}", e.getMessage());
            sendErrorMessage(request.messageType(), "게임 서비스와 통신 중 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("WebSocket 핸들링 중 오류 발생: {}", e.getMessage());
            sendErrorMessage(request.messageType(), "서버 내부 오류가 발생했습니다.");
        }
    }

    @MessageMapping("/game/event")
    public void handleGameEvent(WebSocketMessage<?> request) {
        Object response = null;
        try {
            switch (request.messageType()) {
                case GAME_START -> response = gameServiceClient.startGame(objectMapper.convertValue(request.payload(), GameCreateReq.class)).getBody();
                case SCORE_UPDATE -> response = gameServiceClient.giveScores(objectMapper.convertValue(request.payload(), ScoreReq.class)).getBody();
                case GAME_END -> response = gameServiceClient.endGame(objectMapper.convertValue(request.payload(), new TypeReference<Map<String, String>>() {}).get("gameId")).getBody();
                case QUESTION_GIVE -> response = gameServiceClient.giveQuestion(objectMapper.convertValue(request.payload(), QuestionReq.class)).getBody();
                case ANSWER_SUBMIT -> response = gameServiceClient.submitAnswer(objectMapper.convertValue(request.payload(), AnswerReq.class)).getBody();
                case ROUND_RESULT -> response = gameServiceClient.roundResult(objectMapper.convertValue(request.payload(), CoordinateReq.class)).getBody();
            }
            sendMessage(response, request.messageType());
        } catch (FeignException e) {
            log.error("Feign Client 요청 실패: {}", e.getMessage());
            sendErrorMessage(request.messageType(), "게임 서비스와 통신 중 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("WebSocket 핸들링 중 오류 발생: {}", e.getMessage());
            sendErrorMessage(request.messageType(), "서버 내부 오류가 발생했습니다.");
        }
    }

    @MessageMapping("/notification/event")
    public void handleNotificationEvent(WebSocketMessage<?> request) {
        Object response = null;
        try {
            switch (request.messageType()) {
                case INVITE_PLAYER -> response = notificationService.publishNotification(objectMapper.convertValue(request.payload(), NotificationRequest.class));
                case REPLY -> response = notificationService.updateReadStatus(objectMapper.convertValue(request.payload(), ReplyRequest.class));
            }
        } catch (Exception e) {
            log.error("WebSocket 핸들링 중 오류 발생: {}", e.getMessage());
            sendErrorMessage(request.messageType(), "서버 내부 오류가 발생했습니다.");
        }
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

    private void sendErrorMessage(MessageType messageType, String errorMessage) {
        log.error("WebSocket 에러 메시지 전송: type={}, message={}", messageType, errorMessage);
        messagingTemplate.convertAndSend(
                "/urdego/sub/errors",
                new WebSocketMessage<>(messageType, Map.of("error", errorMessage))
        );
    }
}
