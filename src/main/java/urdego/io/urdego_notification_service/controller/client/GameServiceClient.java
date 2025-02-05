package urdego.io.urdego_notification_service.controller.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import urdego.io.urdego_notification_service.controller.dto.request.game.AnswerReq;
import urdego.io.urdego_notification_service.controller.dto.request.game.QuestionReq;
import urdego.io.urdego_notification_service.controller.dto.request.game.ScoreReq;
import urdego.io.urdego_notification_service.controller.dto.request.room.ContentSelectReq;
import urdego.io.urdego_notification_service.controller.dto.request.room.PlayerReq;
import urdego.io.urdego_notification_service.controller.dto.response.game.AnswerRes;
import urdego.io.urdego_notification_service.controller.dto.response.game.GameEndRes;
import urdego.io.urdego_notification_service.controller.dto.response.game.QuestionRes;
import urdego.io.urdego_notification_service.controller.dto.response.game.ScoreRes;
import urdego.io.urdego_notification_service.controller.dto.response.room.RoomPlayersRes;

@FeignClient(name = "game-service")
@RequestMapping("/api/game-service")
public interface GameServiceClient {
    @PostMapping("/room/player/invite")
    ResponseEntity<RoomPlayersRes> invitePlayer(@RequestBody PlayerReq request);

    @PostMapping("/room/player/remove")
    ResponseEntity<RoomPlayersRes> removePlayer(@RequestBody PlayerReq request);

    @PostMapping("/room/player/ready")
    ResponseEntity<RoomPlayersRes> readyPlayer(@RequestBody PlayerReq request);

    @PostMapping("/room/select-content")
    ResponseEntity<Void> selectContent(@RequestBody ContentSelectReq request);

    @PostMapping("/game/score")
    ResponseEntity<ScoreRes> giveScores(@RequestBody ScoreReq request);

    @PostMapping("/game/end")
    ResponseEntity<GameEndRes> endGame(@RequestBody String gameId);

    @PostMapping("/round/question")
    ResponseEntity<QuestionRes> giveQuestion(@RequestBody QuestionReq request);

    @PostMapping("/round/answer")
    ResponseEntity<AnswerRes> submitAnswer(@RequestBody AnswerReq request);
}
