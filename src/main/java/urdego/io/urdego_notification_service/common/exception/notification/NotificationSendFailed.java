package urdego.io.urdego_notification_service.common.exception.notification;

import urdego.io.urdego_notification_service.common.exception.BaseException;
import urdego.io.urdego_notification_service.common.exception.ExceptionMessage;

public class NotificationSendFailed extends BaseException {
    public static final BaseException EXCEPTION = new NotificationSendFailed();

    private NotificationSendFailed() {
        super(ExceptionMessage.INVALID_NOTIFICATION_ID);
    }
}
