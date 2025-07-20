package io.github.ktrzaskoma.controller;

import io.github.ktrzaskoma.dto.ConnectionDto;
import io.github.ktrzaskoma.service.ScheduleService;
import io.github.ktrzaskoma.dto.StopDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/stops")
    public ResponseEntity<List<StopDto>> getAllStops() {
        return ResponseEntity.ok(scheduleService.getAllActiveStops());
    }

    @GetMapping("/connections")
    public ResponseEntity<List<ConnectionDto>> findConnections(
            @RequestParam String fromStopId,
            @RequestParam String toStopId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {

        if (date == null) {
            date = LocalDate.now();
        }
        if (time == null) {
            time = LocalTime.now();
        }

        return ResponseEntity.ok(scheduleService.findConnections(fromStopId, toStopId, date, time));
    }
}
