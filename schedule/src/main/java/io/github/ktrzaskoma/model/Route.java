package io.github.ktrzaskoma.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "routes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route {

    @Id
    @Column(name = "route_id", length = 50)
    private String routeId;

    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;

    @Column(name = "route_short_name", length = 50)
    private String routeShortName;

    @Column(name = "route_long_name")
    private String routeLongName;

    @Column(name = "route_type")
    private Integer routeType;

    @Column(name = "route_color", length = 10)
    private String routeColor;

    @Column(name = "route_text_color", length = 10)
    private String routeTextColor;

    @ManyToOne
    @JoinColumn(name = "upload_id")
    private GtfsUpload upload;
}
