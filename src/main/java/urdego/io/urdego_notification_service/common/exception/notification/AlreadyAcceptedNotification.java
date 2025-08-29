package urdego.io.urdego_notification_service.common.exception.notification;

import urdego.io.urdego_notification_service.common.exception.BaseException;
import urdego.io.urdego_notification_service.common.exception.ExceptionMessage;

public class AlreadyAcceptedNotification extends BaseException {
    public static final BaseException EXCEPTION = new AlreadyAcceptedNotification();

    private AlreadyAcceptedNotification() {
        super(ExceptionMessage.INVALID_NOTIFICATION_ID);
    }
}
