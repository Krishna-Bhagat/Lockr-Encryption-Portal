package com.hades.hades_portal.service;

import java.util.Map;

public interface EncryptionService {

    /**
     * Encrypts the uploaded file using a symmetric key, and encrypts that key using RSA.
     *
     * @param originalFilename The original filename uploaded.
     * @param directory        The directory path where the file is stored.
     * @param rawPrivateKey    The user-provided one-time private key (symmetric).
     * @param rounds           The number of encryption rounds to use.
     * @param ownerUsername    Who uploaded the file.
     * @return a result object or map with the encrypted file name, key file name, etc.
     */
    Map<String, String> encryptFile(
            String originalFilename,
            String directory,
            String rawPrivateKey,
            int rounds,
            String ownerUsername
    ) throws Exception;
}

