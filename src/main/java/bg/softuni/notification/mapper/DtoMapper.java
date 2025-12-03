package bg.softuni.notification.mapper;


import bg.softuni.notification.model.Notification;
import bg.softuni.notification.model.NotificationPreference;
import bg.softuni.notification.web.dto.NotificationResponse;
import bg.softuni.notification.web.dto.PreferenceResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static PreferenceResponse from(NotificationPreference preference) {

        return PreferenceResponse.builder()
                .type(preference.getType())
                .notificationEnabled(preference.isEnabled())
                .contactInfo(preference.getContactInfo())
                .build();
    }

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .subject(notification.getSubject())
                .body(notification.getBody())
                .createdOn(notification.getCreatedOn())
                .status(notification.getStatus())
                .type(notification.getType())
                .businessId(notification.getBusinessId())
                .deleted(notification.isDeleted())
                .build();
    }
}
