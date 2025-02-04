package urdego.io.urdego_notification_service.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {
    NOT_FOUND_USER("해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "not found user"),
    NOT_FOUND_NOTIFICATION("해당 알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "not found notification"),
    INVALID_NOTIFICATION_ID("잘못된 알림 ID 입니다.", HttpStatus.BAD_REQUEST, "invalid notification id");

    private final String text;
    private final HttpStatus status;
    private final String title;
}
