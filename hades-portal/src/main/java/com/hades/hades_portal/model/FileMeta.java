package com.hades.hades_portal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "files")
public class FileMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String filename;

    @Column(columnDefinition = "TEXT")
    private String directory;
    @Column(columnDefinition = "TEXT")
    private String encryptedSymmetricKey;

    private String ownerUsername; // Who uploaded

    private boolean encrypted;

    private String encryptionMode; // 4,8,12,16 rounds
    private LocalDateTime createdAt; // Optional
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
