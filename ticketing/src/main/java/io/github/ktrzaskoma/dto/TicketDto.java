package io.github.ktrzaskoma.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
    private Long id;
    private String ticketNumber;
    private String tripId;
    private String fromStopId;
    private String toStopId;
    private LocalDate travelDate;
    private BigDecimal price;
    private String status;
    private LocalDateTime createdAt;
}
