package io.github.ktrzaskoma.repository;

import io.github.ktrzaskoma.model.StopTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface StopTimeRepository extends JpaRepository<StopTime, Long> {

    @Query("""
        SELECT DISTINCT st FROM StopTime st
        JOIN FETCH st.trip t
        JOIN FETCH t.route r
        JOIN FETCH st.stop s
        WHERE st.stop.stopId = :fromStopId
        AND st.departureTime >= :departureTime
        AND st.upload.isActive = true
        AND EXISTS (
            SELECT 1 FROM StopTime st2
            WHERE st2.trip = t
            AND st2.stop.stopId = :toStopId
            AND st2.stopSequence > st.stopSequence
        )
        AND EXISTS (
            SELECT 1 FROM CalendarDate cd
            WHERE cd.serviceId = t.serviceId
            AND cd.date = :travelDate
            AND cd.exceptionType = 1
            AND cd.upload.isActive = true
        )
        ORDER BY st.departureTime
        LIMIT 20
        """)
    List<StopTime> findConnections(@Param("fromStopId") String fromStopId,
                                   @Param("toStopId") String toStopId,
                                   @Param("departureTime") LocalTime departureTime,
                                   @Param("travelDate") LocalDate travelDate);

    @Query("""
        SELECT st FROM StopTime st
        JOIN FETCH st.stop
        WHERE st.trip.tripId = :tripId
        AND st.upload.isActive = true
        ORDER BY st.stopSequence
        """)
    List<StopTime> findByTripIdOrderByStopSequence(@Param("tripId") String tripId);

    @Query("""
        SELECT st FROM StopTime st
        WHERE st.stop.stopId = :stopId
        AND st.upload.isActive = true
        ORDER BY st.departureTime
        """)
    List<StopTime> findByStopIdOrderByDepartureTime(@Param("stopId") String stopId);
}
