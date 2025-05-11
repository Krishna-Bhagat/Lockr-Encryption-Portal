package com.hades.hades_portal.service;

import java.util.Map;

public interface DecryptionService {
    Map<String, String> decryptFile(
            String encryptedFilename,
            String directory,
            String encryptedKey,
            String privateKey,
            String modulus,
            int rounds
    ) throws Exception;
}
