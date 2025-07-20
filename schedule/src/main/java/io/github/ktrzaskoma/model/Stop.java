package io.github.ktrzaskoma.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "stops")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stop {

    @Id
    @Column(name = "stop_id", length = 50)
    private String stopId;

    @Column(name = "stop_name", nullable = false)
    private String stopName;

    @Column(name = "stop_lat", precision = 10, scale = 7)
    private BigDecimal stopLat;

    @Column(name = "stop_lon", precision = 10, scale = 7)
    private BigDecimal stopLon;

    @Column(name = "wheelchair_boarding")
    private Integer wheelchairBoarding;

    @ManyToOne
    @JoinColumn(name = "upload_id")
    private GtfsUpload upload;
}
