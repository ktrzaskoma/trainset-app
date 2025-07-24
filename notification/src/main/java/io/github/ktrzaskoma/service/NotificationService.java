package io.github.ktrzaskoma.service;

import io.github.ktrzaskoma.dto.NotificationMessage;
import io.github.ktrzaskoma.model.Notification;
import io.github.ktrzaskoma.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @RabbitListener(queues = "notification.queue")
    public void handleNotification(NotificationMessage message) {
        log.info("Received notification message: {}", message);

        // Save notification to database
        Notification notification = Notification.builder()
                .userId(message.getUserId())
                .type(message.getType())
                .recipientEmail(message.getEmail())
                .subject(message.getSubject())
                .content(message.getContent())
                .status(Notification.NotificationStatus.PENDING)
                .build();

        notification = notificationRepository.save(notification);

        // Send email
        try {
            sendEmail(message);
            notification.setStatus(Notification.NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            log.info("Email sent successfully to: {}", message.getEmail());
        } catch (Exception e) {
            log.error("Failed to send email to: {}", message.getEmail(), e);
            notification.setStatus(Notification.NotificationStatus.FAILED);
        }

        notificationRepository.save(notification);
    }

    private void sendEmail(NotificationMessage message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(message.getEmail());
        mailMessage.setSubject(message.getSubject());
        mailMessage.setText(message.getContent());
        mailMessage.setFrom("noreply@wkd.pl");

        mailSender.send(mailMessage);
    }

}
