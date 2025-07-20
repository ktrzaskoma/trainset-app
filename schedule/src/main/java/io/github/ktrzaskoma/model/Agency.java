package io.github.ktrzaskoma.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agency")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agency {

    @Id
    @Column(name = "agency_id", length = 50)
    private String agencyId;

    @Column(name = "agency_name", nullable = false)
    private String agencyName;

    @Column(name = "agency_url")
    private String agencyUrl;

    @Column(name = "agency_lang", length = 10)
    private String agencyLang;

    @Column(name = "agency_timezone", length = 50)
    private String agencyTimezone;

    @ManyToOne
    @JoinColumn(name = "upload_id")
    private GtfsUpload upload;
}
