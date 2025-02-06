package urdego.io.urdego_notification_service.common.exception.notification;

import urdego.io.urdego_notification_service.common.exception.BaseException;
import urdego.io.urdego_notification_service.common.exception.ExceptionMessage;

public class InvalidNotificationId extends BaseException {
    public static final BaseException EXCEPTION = new InvalidNotificationId();

    private InvalidNotificationId() {
        super(ExceptionMessage.INVALID_NOTIFICATION_ID);
    }
}
