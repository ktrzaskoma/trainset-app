package io.github.ktrzaskoma.controller;


import io.github.ktrzaskoma.dto.TicketDto;
import io.github.ktrzaskoma.dto.TicketPurchaseRequest;
import io.github.ktrzaskoma.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/purchase")
    public ResponseEntity<TicketDto> purchaseTicket(
            @Valid @RequestBody TicketPurchaseRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(ticketService.purchaseTicket(request, userId));
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<List<TicketDto>> getMyTickets(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(ticketService.getUserTickets(userId));
    }

    @GetMapping("/{ticketNumber}")
    public ResponseEntity<TicketDto> getTicket(@PathVariable String ticketNumber) {
        return ResponseEntity.ok(ticketService.getTicket(ticketNumber));
    }
}
