package io.github.ktrzaskoma.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "gtfs_uploads")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GtfsUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filename")
    private String filename;

    @Column(name = "uploaded_by")
    private Long uploadedBy;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @Column(name = "is_active")
    private Boolean isActive;

    @PrePersist
    protected void onCreate() {
        uploadDate = LocalDateTime.now();
        if (isActive == null) {
            isActive = false;
        }
    }
}
