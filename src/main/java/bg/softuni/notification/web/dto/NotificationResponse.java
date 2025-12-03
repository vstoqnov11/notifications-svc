package bg.softuni.notification.web.dto;

import bg.softuni.notification.model.NotificationStatus;
import bg.softuni.notification.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private UUID id;
    private String subject;
    private String body;
    private LocalDateTime createdOn;
    private NotificationStatus status;
    private NotificationType type;
    private UUID businessId;
    private boolean deleted;
}

