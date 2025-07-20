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
        SELECT st FROM StopTime st
        JOIN st.trip t
        JOIN t.route r
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
        """)
    List<StopTime> findConnections(@Param("fromStopId") String fromStopId,
                                   @Param("toStopId") String toStopId,
                                   @Param("departureTime") LocalTime departureTime,
                                   @Param("travelDate") LocalDate travelDate);
}
