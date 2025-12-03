package bg.softuni.notification.web.dto;


import bg.softuni.notification.model.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PreferenceResponse {

    private NotificationType type;

    private boolean notificationEnabled;

    private String contactInfo;
}
