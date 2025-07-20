package io.github.ktrzaskoma.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionDto {
    private String tripId;
    private String routeShortName;
    private String routeLongName;
    private String fromStopName;
    private String toStopName;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private Integer wheelchairAccessible;
    private Integer bikesAllowed;
}
