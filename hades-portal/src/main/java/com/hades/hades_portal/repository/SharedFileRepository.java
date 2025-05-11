package com.hades.hades_portal.repository;

import com.hades.hades_portal.model.SharedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharedFileRepository extends JpaRepository<SharedFile, Long> {
    List<SharedFile> findByRecipientUsername(String recipientUsername);

    List<SharedFile> findBySenderUsername(String senderUsername);

    SharedFile findByEncryptedFilenameAndRecipientUsername(
            String encryptedFilename, String recipientUsername);

    SharedFile findByEncryptedFilenameAndSenderUsername(
            String encryptedFilename, String senderUsername);
}
