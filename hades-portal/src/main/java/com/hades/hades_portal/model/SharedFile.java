package com.hades.hades_portal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "shared_files")
public class SharedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String encryptedFilename;

    private String senderUsername;

    private String recipientUsername;

    private String directory;

    private LocalDateTime sharedAt = LocalDateTime.now();
}
