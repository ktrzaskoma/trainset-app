package io.github.ktrzaskoma.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "calendar_dates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_id", length = 50)
    private String serviceId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "exception_type")
    private Integer exceptionType;

    @ManyToOne
    @JoinColumn(name = "upload_id")
    private GtfsUpload upload;
}
