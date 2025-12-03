package bg.softuni.notification.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PreferenceRequest {

    private UUID businessId;

    private boolean notificationEnabled;

    private String contactInfo;
}

