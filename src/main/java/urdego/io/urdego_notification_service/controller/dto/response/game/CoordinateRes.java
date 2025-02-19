package urdego.io.urdego_notification_service.controller.dto.response.game;

import java.util.List;

public record CoordinateRes(
        String roomId,
        int roundNum,
        String placeName,
        String placeAddress,
        Coordinate answerCoordinate,
        List<SubmitCoordinate> submitCoordinates
) {
    public record Coordinate(
            double lat,
            double lng
    ) {}

    public record SubmitCoordinate(
            String nickname,
            String characterType,
            double lat,
            double lng
    ) {}
}
