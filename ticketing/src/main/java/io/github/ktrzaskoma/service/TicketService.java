package io.github.ktrzaskoma.service;

import io.github.ktrzaskoma.dto.NotificationMessage;
import io.github.ktrzaskoma.dto.TicketDto;
import io.github.ktrzaskoma.dto.TicketPurchaseRequest;
import io.github.ktrzaskoma.exception.TicketNotFoundException;
import io.github.ktrzaskoma.model.Payment;
import io.github.ktrzaskoma.model.Ticket;
import io.github.ktrzaskoma.repository.PaymentRepository;
import io.github.ktrzaskoma.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ticket.price.base}")
    private BigDecimal basePrice;

    @Value("${ticket.price.per-stop}")
    private BigDecimal pricePerStop;

    @Transactional
    public TicketDto purchaseTicket(TicketPurchaseRequest request, Long userId) {
        // Generate unique ticket number
        String ticketNumber = "WKD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Calculate price (simplified - in real app would calculate based on actual stops)
        BigDecimal price = basePrice.add(pricePerStop.multiply(new BigDecimal(5)));

        // Create ticket
        Ticket ticket = Ticket.builder()
                .ticketNumber(ticketNumber)
                .userId(userId)
                .tripId(request.getTripId())
                .fromStopId(request.getFromStopId())
                .toStopId(request.getToStopId())
                .travelDate(request.getTravelDate())
                .price(price)
                .status(Ticket.TicketStatus.PENDING)
                .build();

        ticket = ticketRepository.save(ticket);

        // Process payment
        Payment payment = Payment.builder()
                .ticket(ticket)
                .amount(price)
                .paymentMethod(request.getPaymentMethod())
                .transactionId(UUID.randomUUID().toString())
                .status(Payment.PaymentStatus.COMPLETED)
                .build();

        paymentRepository.save(payment);

        // Update ticket status
        ticket.setStatus(Ticket.TicketStatus.PAID);
        ticket = ticketRepository.save(ticket);

        // Send notification
        sendTicketNotification(ticket, userId);

        return mapToDto(ticket);
    }

    public List<TicketDto> getUserTickets(Long userId) {
        return ticketRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public TicketDto getTicket(String ticketNumber) {
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found: " + ticketNumber));
        return mapToDto(ticket);
    }

    private void sendTicketNotification(Ticket ticket, Long userId) {
        try {
            // Get user email from User Service
            String userEmail = getUserEmail(userId);

            NotificationMessage message = NotificationMessage.builder()
                    .userId(userId)
                    .email(userEmail)
                    .type("TICKET_PURCHASE")
                    .subject("Potwierdzenie zakupu biletu WKD")
                    .content(buildTicketEmailContent(ticket))
                    .build();

            rabbitTemplate.convertAndSend("notifications.exchange", "notification.email", message);
            log.info("Sent notification for ticket: {}", ticket.getTicketNumber());
        } catch (Exception e) {
            log.error("Failed to send notification for ticket: {}", ticket.getTicketNumber(), e);
        }
    }

    private String getUserEmail(Long userId) {
        // In real app, would call User Service
        return "user@example.com";
    }

    private String buildTicketEmailContent(Ticket ticket) {
        return String.format("""
                Szanowny Pasażerze,
                
                Dziękujemy za zakup biletu WKD.
                
                Szczegóły biletu:
                - Numer biletu: %s
                - Trasa: %s - %s
                - Data podróży: %s
                - Cena: %s PLN
                
                Życzymy miłej podróży!
                
                Zespół WKD
                """,
                ticket.getTicketNumber(),
                ticket.getFromStopId(),
                ticket.getToStopId(),
                ticket.getTravelDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                ticket.getPrice()
        );
    }

    private TicketDto mapToDto(Ticket ticket) {
        return TicketDto.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .tripId(ticket.getTripId())
                .fromStopId(ticket.getFromStopId())
                .toStopId(ticket.getToStopId())
                .travelDate(ticket.getTravelDate())
                .price(ticket.getPrice())
                .status(ticket.getStatus().name())
                .createdAt(ticket.getCreatedAt())
                .build();
    }
}
