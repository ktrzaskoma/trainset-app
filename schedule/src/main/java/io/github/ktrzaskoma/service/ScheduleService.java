package io.github.ktrzaskoma.service;


import io.github.ktrzaskoma.dto.ConnectionDto;
import io.github.ktrzaskoma.dto.StopDto;
import io.github.ktrzaskoma.repository.StopRepository;
import io.github.ktrzaskoma.model.StopTime;
import io.github.ktrzaskoma.repository.StopTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final StopRepository stopRepository;
    private final StopTimeRepository stopTimeRepository;

    public List<StopDto> getAllActiveStops() {
        return stopRepository.findByUploadIsActiveTrue().stream()
                .map(stop -> StopDto.builder()
                        .stopId(stop.getStopId())
                        .stopName(stop.getStopName())
                        .stopLat(stop.getStopLat())
                        .stopLon(stop.getStopLon())
                        .wheelchairBoarding(stop.getWheelchairBoarding())
                        .build())
                .collect(Collectors.toList());
    }


    public List<ConnectionDto> findConnections(String fromStopId, String toStopId, LocalDate date, LocalTime time) {
        List<StopTime> departureStopTimes = stopTimeRepository.findConnections(fromStopId, toStopId, time, date);

        List<ConnectionDto> connections = new ArrayList<>();

        for (StopTime departure : departureStopTimes) {
            // Find arrival stop time for the same trip
            StopTime arrival = stopTimeRepository.findAll().stream()
                    .filter(st -> st.getTrip().getTripId().equals(departure.getTrip().getTripId())
                            && st.getStop().getStopId().equals(toStopId)
                            && st.getStopSequence() > departure.getStopSequence())
                    .findFirst()
                    .orElse(null);

            if (arrival != null) {
                connections.add(ConnectionDto.builder()
                        .tripId(departure.getTrip().getTripId())
                        .routeShortName(departure.getTrip().getRoute().getRouteShortName())
                        .routeLongName(departure.getTrip().getRoute().getRouteLongName())
                        .fromStopName(departure.getStop().getStopName())
                        .toStopName(arrival.getStop().getStopName())
                        .departureTime(departure.getDepartureTime())
                        .arrivalTime(arrival.getArrivalTime())
                        .wheelchairAccessible(departure.getTrip().getWheelchairAccessible())
                        .bikesAllowed(departure.getTrip().getBikesAllowed())
                        .build());

                if (connections.size() >= 5) {
                    break;
                }
            }
        }

        return connections;
    }
}
