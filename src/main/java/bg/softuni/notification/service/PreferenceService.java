package bg.softuni.notification.service;


import bg.softuni.notification.model.NotificationPreference;
import bg.softuni.notification.model.NotificationType;
import bg.softuni.notification.repository.NotificationPreferenceRepository;
import bg.softuni.notification.web.dto.PreferenceRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;

    public PreferenceService (NotificationPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    public NotificationPreference upsert (PreferenceRequest request) {

        Optional<NotificationPreference> preferenceOpt = preferenceRepository.findByBusinessId(request.getBusinessId());

        if (preferenceOpt.isPresent()) {
            NotificationPreference preference = preferenceOpt.get();
            preference.setEnabled(request.isNotificationEnabled());
            preference.setContactInfo(request.getContactInfo());
            preference.setUpdatedOn(LocalDateTime.now());
            return preferenceRepository.save(preference);
        }

        NotificationPreference preference = NotificationPreference.builder()
                .businessId(request.getBusinessId())
                .type(NotificationType.EMAIL)
                .enabled(request.isNotificationEnabled())
                .contactInfo(request.getContactInfo())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        return preferenceRepository.save(preference);
    }

    public NotificationPreference getBusinessById (UUID businessId) {

        return preferenceRepository.findByBusinessId(businessId).orElseThrow(() -> new IllegalArgumentException("No preference found for this business"));
    }
}
