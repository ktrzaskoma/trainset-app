package io.github.ktrzaskoma.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopDto {
    private String stopId;
    private String stopName;
    private BigDecimal stopLat;
    private BigDecimal stopLon;
    private Integer wheelchairBoarding;
}
