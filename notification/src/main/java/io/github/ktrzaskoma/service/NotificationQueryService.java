package io.github.ktrzaskoma.service;

import io.github.ktrzaskoma.dto.NotificationDto;
import io.github.ktrzaskoma.model.Notification;
import io.github.ktrzaskoma.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public List<NotificationDto> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .recipientEmail(notification.getRecipientEmail())
                .subject(notification.getSubject())
                .content(notification.getContent())
                .status(notification.getStatus().name())
                .sentAt(notification.getSentAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
