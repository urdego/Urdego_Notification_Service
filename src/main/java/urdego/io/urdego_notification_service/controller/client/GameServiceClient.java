package urdego.io.urdego_notification_service.controller.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import urdego.io.urdego_notification_service.controller.dto.response.RoomPlayersRes;

@FeignClient(name = "game-service", url = "${feign.client.config.service.url}")
public interface GameServiceClient {
    @PostMapping("/player/invite")
    ResponseEntity<RoomPlayersRes> invitePlayer(@RequestBody PlayerReq request);

    @PostMapping("/player/remove")
    ResponseEntity<RoomPlayersRes> removePlayer(@RequestBody PlayerReq request);

    @PostMapping("/player/ready")
    ResponseEntity<RoomPlayersRes> readyPlayer(@RequestBody PlayerReq request);

    @PostMapping("/select-content")
    ResponseEntity<Void> selectContent(@RequestBody ContentSelectReq request);


}
