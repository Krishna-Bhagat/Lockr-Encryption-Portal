package com.hades.hades_portal.repository;

import com.hades.hades_portal.model.FileMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileMeta, Long> {
    List<FileMeta> findByOwnerUsername(String ownerUsername);

    Optional<FileMeta> findByFilenameAndOwnerUsername(String name, String username);
}
