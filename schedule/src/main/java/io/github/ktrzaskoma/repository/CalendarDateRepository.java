package io.github.ktrzaskoma.repository;

import io.github.ktrzaskoma.model.CalendarDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarDateRepository extends JpaRepository<CalendarDate, Long> {


}

