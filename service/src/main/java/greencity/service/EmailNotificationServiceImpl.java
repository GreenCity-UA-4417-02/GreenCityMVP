package greencity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.NotificationDto;
import greencity.dto.event.EventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class EmailNotificationServiceImpl implements EmailNotificationService {
    private final TemplateEngine templateEngine;
    @Value("${greencityuser.server.address}")
    private String serverUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EmailNotificationServiceImpl(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public void sendDeleteNotification(EventDto eventDto) {
        sendNotification(eventDto, "Event Deleted - " + eventDto.title(), "email/event-deletion-notification");
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public void sendUpdateNotification(EventDto eventDto) {
        sendNotification(eventDto, "Event Updated - " + eventDto.title(), "email/event-update-notification");
    }

    private void sendNotification(EventDto eventDto, String notificationTitle, String htmlTemplate) {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null
                    && authentication.getDetails() != null
                    && authentication.getDetails() instanceof String token) {

                String htmlBody = createEventNotificationHtmlFromTemplate(eventDto, authentication.getName(), htmlTemplate);
                NotificationDto dto = NotificationDto.builder()
                        .title(notificationTitle)
                        .body(htmlBody)
                        .build();

                String jsonBody = objectMapper.writeValueAsString(dto);

                URI uri = UriComponentsBuilder
                        .fromHttpUrl(serverUrl + "/email/notification")
                        .queryParam("email", eventDto.organizerEmail())
                        .build()
                        .toUri();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    log.info("Event notification sent to: user {}", eventDto.organizerId());
                } else {
                    log.error("Failed to send notification. Status: {}", response.statusCode());
                }
            }

        } catch (Exception e) {
            log.error("Error while sending notification", e);
        }
    }

    private String createEventNotificationHtmlFromTemplate(EventDto eventDto, String user, String htmlTemplate) {
        Context context = new Context();
        context.setVariable("organizerName", eventDto.organizerName());
        context.setVariable("eventId", eventDto.id());
        context.setVariable("eventTitle", eventDto.title());
        context.setVariable("adminOrOrganiser", user);
        context.setVariable("date", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        context.setVariable("clientLink", "http://localhost:4200");

        return templateEngine.process(htmlTemplate, context);
    }
}
