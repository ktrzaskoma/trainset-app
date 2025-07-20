package io.github.ktrzaskoma.repository;

import io.github.ktrzaskoma.model.GtfsUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GtfsUploadRepository extends JpaRepository<GtfsUpload, Long> {
    Optional<GtfsUpload> findByIsActiveTrue();
}
