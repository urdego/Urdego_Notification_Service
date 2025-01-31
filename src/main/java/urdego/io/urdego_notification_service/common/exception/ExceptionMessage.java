package urdego.io.urdego_notification_service.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {
    NOT_FOUND_USER("해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "not found user");

    private final String text;
    private final HttpStatus status;
    private final String title;
}
