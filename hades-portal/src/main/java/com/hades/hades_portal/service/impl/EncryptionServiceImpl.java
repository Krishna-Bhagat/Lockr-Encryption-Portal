package com.hades.hades_portal.service.impl;

import com.hades.hades_portal.crypto.EncDec;
import com.hades.hades_portal.crypto.FunctionSet;
import com.hades.hades_portal.crypto.RsaFunctionClass;
import com.hades.hades_portal.model.FileMeta;
import com.hades.hades_portal.model.User;
import com.hades.hades_portal.repository.FileRepository;
import com.hades.hades_portal.repository.UserRepository;
import com.hades.hades_portal.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class EncryptionServiceImpl implements EncryptionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RsaFunctionClass rsaFunctionClass;

    @Autowired
    private FileRepository fileMetaRepository;


    @Override
    public Map<String, String> encryptFile(
            String originalFilename,
            String directory,
            String rawPrivateKey,
            int rounds, String ownerUsername) throws Exception {
        // 1. Get the full path of the input file
        File originalFile = new File(directory, originalFilename);
        if (!originalFile.exists()) {
            throw new IOException("Original file not found at: " + originalFile.getAbsolutePath());
        }
        rawPrivateKey = FunctionSet.KeyGen(rawPrivateKey);
        // 2. Convert the symmetric key into a BigInteger
        BigInteger m = new BigInteger(rawPrivateKey);
        // 3. Run symmetric encryption using the provided key
        EncDec encDec = new EncDec();
        encDec.encrypt(originalFilename, directory, rawPrivateKey, rounds);
        String encryptedFilename = originalFilename.substring(0, originalFilename.lastIndexOf(".")) + ".enc";




        // 4. Get RSA public key of the recipient (current user)
        Optional<User> userOptional = userRepository.findByUsername(ownerUsername);
        if (!userOptional.isPresent()) {
            throw new Exception("User with provided username not found.");
        }

        User recipient = userOptional.get();
        BigInteger e = new BigInteger(recipient.getPublicKey());
        BigInteger n = new BigInteger(recipient.getNValue());

        // 5. Encrypt the symmetric key using RSA
        BigInteger encryptedKey = rsaFunctionClass.EncDec(m, e, n);

        // 6. Write the encrypted key to a file
        String keyFilename = rsaFunctionClass.WriteEncKey(
                encryptedKey, e, directory, encryptedFilename, rounds
        );

        // 7. Also save metadata to database
        FileMeta meta = new FileMeta();
        meta.setFilename(encryptedFilename);
        meta.setDirectory(directory);
        meta.setEncryptedSymmetricKey(encryptedKey.toString()); // ensure DB column is TEXT
        meta.setOwnerUsername(ownerUsername);
        meta.setEncrypted(true);
        meta.setEncryptionMode(String.valueOf(rounds));

        fileMetaRepository.save(meta);

        // 8. Return result
        Map<String, String> result = new HashMap<>();
        result.put("encryptedFile", encryptedFilename);
        result.put("encryptedKeyFile", keyFilename);

        return result;
    }
}
