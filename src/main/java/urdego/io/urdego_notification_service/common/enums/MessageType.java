package urdego.io.urdego_notification_service.common.enums;

public enum MessageType {
    // 알림
    ALBERT,
    INVITE_PLAYER,
    REPLY,
    // 대기방
    PLAYER_JOIN,
    PLAYER_REMOVE,
    PLAYER_READY,
    CONTENT_SELECT,
    // 게임
    GAME_START,
    SCORE_UPDATE,
    GAME_END,
    // 라운드 진행
    QUESTION_GIVE,
    ANSWER_SUBMIT,
    ROUND_RESULT,
    // 오류
    ERROR

}
