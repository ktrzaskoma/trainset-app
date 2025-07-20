package io.github.ktrzaskoma.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import io.github.ktrzaskoma.model.Agency;
import io.github.ktrzaskoma.repository.AgencyRepository;
import io.github.ktrzaskoma.model.CalendarDate;
import io.github.ktrzaskoma.repository.CalendarDateRepository;
import io.github.ktrzaskoma.model.Route;
import io.github.ktrzaskoma.repository.RouteRepository;
import io.github.ktrzaskoma.model.Stop;
import io.github.ktrzaskoma.repository.StopRepository;
import io.github.ktrzaskoma.model.StopTime;
import io.github.ktrzaskoma.repository.StopTimeRepository;
import io.github.ktrzaskoma.model.Trip;
import io.github.ktrzaskoma.repository.TripRepository;
import io.github.ktrzaskoma.model.GtfsUpload;
import io.github.ktrzaskoma.repository.GtfsUploadRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GtfsImportService {

    private final GtfsUploadRepository uploadRepository;
    private final AgencyRepository agencyRepository;
    private final StopRepository stopRepository;
    private final RouteRepository routeRepository;
    private final TripRepository tripRepository;
    private final StopTimeRepository stopTimeRepository;
    private final CalendarDateRepository calendarDateRepository;

    @Transactional
    public void importGtfsData(MultipartFile file, Long userId) throws Exception {
        uploadRepository.findByIsActiveTrue().ifPresent(upload -> {
            upload.setIsActive(false);
            uploadRepository.save(upload);
        });

        // Create new upload
        GtfsUpload upload = GtfsUpload.builder()
                .filename(file.getOriginalFilename())
                .uploadedBy(userId)
                .isActive(true)
                .build();
        upload = uploadRepository.save(upload);

        Map<String, String> fileContents = getStringStringMap(file);

        // Import data in correct order
        importAgency(fileContents.get("agency.txt"), upload);
        importStops(fileContents.get("stops.txt"), upload);
        importRoutes(fileContents.get("routes.txt"), upload);
        importTrips(fileContents.get("trips.txt"), upload);
        importStopTimes(fileContents.get("stop_times.txt"), upload);
        importCalendarDates(fileContents.get("calendar_dates.txt"), upload);
    }

    private static Map<String, String> getStringStringMap(MultipartFile file) throws IOException {
        Map<String, String> fileContents = new HashMap<>();

        // Extract files from ZIP
        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith(".txt")) {
                    byte[] buffer = zis.readAllBytes();
                    String content = new String(buffer, StandardCharsets.UTF_8);
                    fileContents.put(entry.getName(), content);
                }
            }
        }
        return fileContents;
    }

    private void importAgency(String content, GtfsUpload upload) throws Exception {
        try (CSVReader reader = new CSVReaderBuilder(new StringReader(content))
                .withSkipLines(1)
                .build()) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                Agency agency = Agency.builder()
                        .agencyId(line[0])
                        .agencyName(line[1])
                        .agencyUrl(line[2])
                        .agencyLang(line[3])
                        .agencyTimezone(line[4])
                        .upload(upload)
                        .build();
                agencyRepository.save(agency);
            }
        }
    }

    private void importStops(String content, GtfsUpload upload) throws Exception {
        try (CSVReader reader = new CSVReaderBuilder(new StringReader(content))
                .withSkipLines(1)
                .build()) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                Stop stop = Stop.builder()
                        .stopId(line[0])
                        .stopName(line[1])
                        .stopLat(new BigDecimal(line[2]))
                        .stopLon(new BigDecimal(line[3]))
                        .wheelchairBoarding(Integer.parseInt(line[4]))
                        .upload(upload)
                        .build();
                stopRepository.save(stop);
            }
        }
    }

    private void importRoutes(String content, GtfsUpload upload) throws Exception {
        try (CSVReader reader = new CSVReaderBuilder(new StringReader(content))
                .withSkipLines(1)
                .build()) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                Agency agency = agencyRepository.findById(line[0]).orElse(null);
                Route route = Route.builder()
                        .routeId(line[1])
                        .agency(agency)
                        .routeShortName(line[2])
                        .routeLongName(line[3])
                        .routeType(Integer.parseInt(line[4]))
                        .routeColor(line[5])
                        .routeTextColor(line[6])
                        .upload(upload)
                        .build();
                routeRepository.save(route);
            }
        }
    }

    private void importTrips(String content, GtfsUpload upload) throws Exception {
        try (CSVReader reader = new CSVReaderBuilder(new StringReader(content))
                .withSkipLines(1)
                .build()) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                Route route = routeRepository.findById(line[0]).orElse(null);
                Trip trip = tripRepository.findById(line[0]).orElse(null);
                Stop stop = stopRepository.findById(line[2]).orElse(null);

                if (trip != null && stop != null) {
                    StopTime stopTime = StopTime.builder()
                            .trip(trip)
                            .stop(stop)
                            .stopSequence(Integer.parseInt(line[1]))
                            .arrivalTime(LocalTime.parse(line[3]))
                            .departureTime(LocalTime.parse(line[4]))
                            .upload(upload)
                            .build();
                    stopTimeRepository.save(stopTime);
                }
            }
        }
    }

    private void importCalendarDates(String content, GtfsUpload upload) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        try (CSVReader reader = new CSVReaderBuilder(new StringReader(content))
                .withSkipLines(1)
                .build()) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                CalendarDate calendarDate = CalendarDate.builder()
                        .serviceId(line[1])
                        .date(LocalDate.parse(line[0], formatter))
                        .exceptionType(Integer.parseInt(line[2]))
                        .upload(upload)
                        .build();
                calendarDateRepository.save(calendarDate);
            }
        }
    }

    private void importStopTimes(String content, GtfsUpload upload) throws Exception {
        try (CSVReader reader = new CSVReaderBuilder(new StringReader(content))
                .withSkipLines(1)
                .build()) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                Trip trip = tripRepository.findById(line[0]).orElse(null);
                Stop stop = stopRepository.findById(line[2]).orElse(null);

                if (trip != null && stop != null) {
                    StopTime stopTime = StopTime.builder()
                            .trip(trip)
                            .stop(stop)
                            .stopSequence(Integer.parseInt(line[1]))
                            .arrivalTime(LocalTime.parse(line[3]))
                            .departureTime(LocalTime.parse(line[4]))
                            .upload(upload)
                            .build();
                    stopTimeRepository.save(stopTime);
                }
            }
        }
    }
}
