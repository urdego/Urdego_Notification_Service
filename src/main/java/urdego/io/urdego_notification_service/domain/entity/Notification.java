package urdego.io.urdego_notification_service.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import urdego.io.urdego_notification_service.common.enums.Action;
import urdego.io.urdego_notification_service.controller.dto.request.notification.NotificationRequest;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Component
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification implements Serializable {
    private UUID notificationId;

    private Long senderId;
    private Long targetId;
    private String roomId;

    private String roomName;
    private String senderNickname;
    private String targetNickname;
    private Action action;

    //수락여부
    private boolean isAccepted;

    //읽기 여부
    private boolean isRead;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String timestamp;

    public static Notification of(NotificationRequest request){
        return Notification.builder()
                .notificationId(UUID.randomUUID())
                .roomId(request.roomId())
                .roomName(request.roomName())
                .senderId(request.senderId())
                .senderNickname(request.senderNickname())
                .targetId(request.targetId())
                .targetNickname(request.targetNickname())
                .action(Action.valueOf(request.action()))
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    public void updateReply(boolean isAccepted) {
        this.isAccepted = isAccepted;
        this.isRead = true;
    }

}
