package urdego.io.urdego_notification_service.common.exception.notification;

import urdego.io.urdego_notification_service.common.exception.BaseException;
import urdego.io.urdego_notification_service.common.exception.ExceptionMessage;

public class NotFoundNotification extends BaseException {
    public static final BaseException EXCEPTION = new NotFoundNotification();

    private NotFoundNotification() {
        super(ExceptionMessage.NOT_FOUND_NOTIFICATION);
    }
}
