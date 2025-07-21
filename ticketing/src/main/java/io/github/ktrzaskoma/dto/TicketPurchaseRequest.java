package io.github.ktrzaskoma.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TicketPurchaseRequest {
    @NotBlank
    private String tripId;

    @NotBlank
    private String fromStopId;

    @NotBlank
    private String toStopId;

    @NotNull
    private LocalDate travelDate;

    @NotBlank
    private String paymentMethod;
}
