package io.github.ktrzaskoma.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trips")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @Column(name = "trip_id", length = 50)
    private String tripId;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    @Column(name = "service_id", length = 50)
    private String serviceId;

    @Column(name = "trip_headsign")
    private String tripHeadsign;

    @Column(name = "trip_short_name", length = 50)
    private String tripShortName;

    @Column(name = "direction_id")
    private Integer directionId;

    @Column(name = "shape_id", length = 50)
    private String shapeId;

    @Column(name = "wheelchair_accessible")
    private Integer wheelchairAccessible;

    @Column(name = "bikes_allowed")
    private Integer bikesAllowed;

    @ManyToOne
    @JoinColumn(name = "upload_id")
    private GtfsUpload upload;
}
