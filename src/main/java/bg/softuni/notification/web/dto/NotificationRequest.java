package bg.softuni.notification.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NotNull(message = "Business ID is required")
    private UUID businessId;

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotBlank(message = "Business email is required")
    @Email(message = "Invalid email format")
    private String businessEmail;

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotBlank(message = "Product brand is required")
    private String productBrand;

    @Min(value = 0, message = "Current quantity cannot be negative")
    private int currentQuantity;

    @Min(value = 0, message = "Min stock threshold cannot be negative")
    private int minStockThreshold;
}

