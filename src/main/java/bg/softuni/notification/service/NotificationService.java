package bg.softuni.notification.service;


import bg.softuni.notification.model.Notification;
import bg.softuni.notification.model.NotificationPreference;
import bg.softuni.notification.model.NotificationStatus;
import bg.softuni.notification.model.NotificationType;
import bg.softuni.notification.repository.NotificationRepository;
import bg.softuni.notification.web.dto.NotificationRequest;
import bg.softuni.notification.web.dto.PreferenceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PreferenceService preferenceService;
    private final MailSender mailSender;

    public NotificationService(NotificationRepository notificationRepository, PreferenceService preferenceService, MailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.preferenceService = preferenceService;
        this.mailSender = mailSender;
    }

    @Transactional
    public Notification sendLowStockNotification(NotificationRequest request) {
        String recipientEmail = request.getBusinessEmail();
        
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Business email is required");
        }
        
        String subject = String.format("Low Stock Alert: %s", request.getProductName());
        String body = String.format(
            "Dear %s,\n\n" +
            "This is to inform you that your product stock has fallen below the minimum threshold.\n\n" +
            "Product Details:\n" +
            "- Product: %s\n" +
            "- Brand: %s\n" +
            "- Current Quantity: %d\n" +
            "- Minimum Threshold: %d\n\n" +
            "Please consider restocking this item.\n\n" +
            "Best regards,\n" +
            "Inventorize Notification Service",
            request.getBusinessName(),
            request.getProductName(),
            request.getProductBrand(),
            request.getCurrentQuantity(),
            request.getMinStockThreshold()
        );

        NotificationPreference preference;
        try {
            preference = preferenceService.getBusinessById(request.getBusinessId());
            if (!recipientEmail.equals(preference.getContactInfo())) {
                preference = preferenceService.upsert(PreferenceRequest.builder()
                    .businessId(request.getBusinessId())
                    .contactInfo(recipientEmail)
                    .notificationEnabled(true)
                    .build());
            }
        } catch (IllegalArgumentException e) {
            preference = preferenceService.upsert(PreferenceRequest.builder()
                .businessId(request.getBusinessId())
                .contactInfo(recipientEmail)
                .notificationEnabled(true)
                .build());
        }

        if (!preference.isEnabled()) {
            throw new IllegalStateException("User's notifications are disabled");
        }

        Notification notification = Notification.builder()
                .subject(subject)
                .body(body)
                .createdOn(LocalDateTime.now())
                .type(NotificationType.EMAIL)
                .businessId(request.getBusinessId())
                .deleted(false)
                .status(NotificationStatus.FAILED)
                .build();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
            notification.setStatus(NotificationStatus.SUCCEEDED);
            log.info("Low stock notification sent successfully to: {}", recipientEmail);
        } catch (Exception e) {
            log.error("Failed to send low stock notification email to {}: {}", recipientEmail, e.getMessage(), e);
            notification.setStatus(NotificationStatus.FAILED);
        }

        try {
            Notification savedNotification = notificationRepository.save(notification);
            log.info("Notification saved to database with ID: {}", savedNotification.getId());
            return savedNotification;
        } catch (Exception e) {
            log.error("Failed to save notification to database: {}", e.getMessage(), e);
            log.error("Notification details - Subject: {}, BusinessId: {}, Status: {}", 
                    notification.getSubject(), notification.getBusinessId(), notification.getStatus());
            throw new RuntimeException("Failed to save notification to database: " + e.getMessage(), e);
        }
    }

    public List<Notification> getHistory (UUID businessId) {

        return notificationRepository.findByBusinessId(businessId).stream().filter(n -> !n.isDeleted()).toList();
    }
}
