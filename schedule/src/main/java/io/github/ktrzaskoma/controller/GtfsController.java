package io.github.ktrzaskoma.controller;

import io.github.ktrzaskoma.service.GtfsImportService;
import io.github.ktrzaskoma.event.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/gtfs")
@RequiredArgsConstructor
public class GtfsController {

    private final GtfsImportService gtfsImportService;

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadGtfs(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole) {

        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(403).body(new MessageResponse("Access denied"));
        }

        try {
            gtfsImportService.importGtfsData(file, userId);
            return ResponseEntity.ok(new MessageResponse("GTFS data imported successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error importing GTFS data: " + e.getMessage()));
        }
    }
}
