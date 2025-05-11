package com.hades.hades_portal.service.impl;

import com.hades.hades_portal.crypto.EncDec;
import com.hades.hades_portal.crypto.RsaFunctionClass;
import com.hades.hades_portal.service.DecryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Service
public class DecryptionServiceImpl implements DecryptionService {

    @Autowired
    private RsaFunctionClass rsaFunctionClass;

    @Override
    public Map<String, String> decryptFile(
            String encryptedFilename,
            String directory,
            String encryptedKey,
            String privateKey,
            String modulus, int rounds) throws Exception {

        // Convert string inputs to BigIntegers
        BigInteger encKey = new BigInteger(encryptedKey.trim());
        BigInteger d = new BigInteger(privateKey.trim());
        BigInteger n = new BigInteger(modulus.trim());

        // Decrypt symmetric key using RSA
        BigInteger c=new BigInteger(encKey.toString());
        BigInteger decryptedSymmetricKey = rsaFunctionClass.EncDec(c, d, n);
        String symmetricKey = decryptedSymmetricKey.toString();

        // Determine original extension (.hades -> original extension)
        String originalExt = "txt"; // Since the encrypted file is .hades, we assume original extension is .txt


        // Perform file decryption
        EncDec encDec = new EncDec();
        encDec.decrypt(encryptedFilename, originalExt, directory, symmetricKey, rounds);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Decryption completed successfully");
        response.put("decryptedFile", directory + "/" + encryptedFilename.replace("enc", originalExt));

        return response;
    }
}
