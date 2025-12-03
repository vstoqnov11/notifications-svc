package bg.softuni.notification.web;


import bg.softuni.notification.mapper.DtoMapper;
import bg.softuni.notification.model.NotificationPreference;
import bg.softuni.notification.service.PreferenceService;
import bg.softuni.notification.web.dto.PreferenceRequest;
import bg.softuni.notification.web.dto.PreferenceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/preferences")
public class PreferenceController {

    private final PreferenceService preferenceService;

    @Autowired
    public PreferenceController (PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @PostMapping
    public ResponseEntity<PreferenceResponse> upsertPreference(@RequestBody PreferenceRequest request) {

        NotificationPreference notificationPreference = preferenceService.upsert(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DtoMapper.from(notificationPreference));
    }

}
