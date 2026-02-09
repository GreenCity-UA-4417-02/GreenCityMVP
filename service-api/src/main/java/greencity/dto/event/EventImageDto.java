package greencity.dto.event;

import greencity.annotations.ValidImageFormat;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record EventImageDto(
        @NotBlank(message = "Image URL is required")
        @URL(message = "Invalid URL format")
        @ValidImageFormat(allowedFormats = {"jpg", "jpeg", "png"},
                message = "Image must be in JPG, JPEG or PNG format")
        String url,

        boolean isMain
) {}
