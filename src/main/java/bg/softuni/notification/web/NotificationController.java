package bg.softuni.notification.web;

import bg.softuni.notification.mapper.DtoMapper;
import bg.softuni.notification.model.Notification;
import bg.softuni.notification.service.NotificationService;
import bg.softuni.notification.web.dto.NotificationRequest;
import bg.softuni.notification.web.dto.NotificationResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/notification-svc")
public class NotificationController {

    private final NotificationService emailNotificationService;

    @Autowired
    public NotificationController(NotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @PostMapping("/notifications/low-stock")
    public ResponseEntity<String> sendLowStockNotification(@Valid @RequestBody NotificationRequest request) {
        try {
            emailNotificationService.sendLowStockNotification(request);
            return ResponseEntity.ok("Low stock notification sent successfully");
        } catch (IllegalStateException e) {
            log.warn("Notification not sent: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Notification not sent: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending notification: " + e.getMessage());
        }
    }

    @GetMapping("/notifications/business/{businessId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationHistory(@PathVariable UUID businessId) {
        try {
            List<Notification> notifications = emailNotificationService.getHistory(businessId);
            List<NotificationResponse> responses = notifications.stream()
                    .map(DtoMapper::from)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error retrieving notification history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error: " + e.getMessage());
    }
}

