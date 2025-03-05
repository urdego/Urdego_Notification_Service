package urdego.io.urdego_notification_service.controller.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import urdego.io.urdego_notification_service.controller.dto.request.game.*;
import urdego.io.urdego_notification_service.controller.dto.request.room.ContentSelectReq;
import urdego.io.urdego_notification_service.controller.dto.request.room.PlayerReq;
import urdego.io.urdego_notification_service.controller.dto.response.game.*;
import urdego.io.urdego_notification_service.controller.dto.response.room.RoomPlayersRes;

import java.util.Map;

@FeignClient(name = "game-service")
public interface GameServiceClient {
    @PostMapping("/api/game-service/room/player/invite")
    ResponseEntity<RoomPlayersRes> invitePlayer(@RequestBody PlayerReq request);

    @PostMapping("/api/game-service/room/player/remove")
    ResponseEntity<RoomPlayersRes> removePlayer(@RequestBody PlayerReq request);

    @PostMapping("/api/game-service/room/player/ready")
    ResponseEntity<RoomPlayersRes> readyPlayer(@RequestBody PlayerReq request);

    @PostMapping("/api/game-service/room/select-content")
    ResponseEntity<Void> selectContent(@RequestBody ContentSelectReq request);

    @PostMapping("/api/game-service/room/delete")
    ResponseEntity<Void> deleteRoom(@RequestBody Map<String, String> request);

    @PostMapping("/api/game-service/game/start")
    ResponseEntity<GameCreateRes> startGame(@RequestBody GameCreateReq request);

    @PostMapping("/api/game-service/game/score")
    ResponseEntity<ScoreRes> giveScores(@RequestBody ScoreReq request);

    @PostMapping("/api/game-service/game/end")
    ResponseEntity<GameEndRes> endGame(@RequestBody String gameId);

    @PostMapping("/api/game-service/round/question")
    ResponseEntity<QuestionRes> giveQuestion(@RequestBody QuestionReq request);

    @PostMapping("/api/game-service/round/answer")
    ResponseEntity<AnswerRes> submitAnswer(@RequestBody AnswerReq request);

    @PostMapping("/api/game-service/round/roundResult")
    ResponseEntity<CoordinateRes> roundResult(@RequestBody CoordinateReq request);
}