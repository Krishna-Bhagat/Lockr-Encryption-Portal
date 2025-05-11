package com.hades.hades_portal.controller;

import com.hades.hades_portal.model.FileMeta;
import com.hades.hades_portal.model.SharedFile;
import com.hades.hades_portal.model.User;
import com.hades.hades_portal.repository.FileRepository;
import com.hades.hades_portal.repository.SharedFileRepository;
import com.hades.hades_portal.repository.UserRepository;
import com.hades.hades_portal.service.DecryptionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;
import java.util.Optional;

@Controller
public class DecryptionController {

    @Autowired
    private DecryptionService decryptionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private SharedFileRepository sharedFileRepository;

    @PostMapping("/decrypt")
    public String handleDecryption(
            @RequestParam("encryptedFile") MultipartFile encryptedFile,
            HttpSession session,
            Model model) {
        try {
            // Get username from session
            String username = (String) session.getAttribute("loggedInUsername");
            if (username == null) {
                model.addAttribute("message2", "User not logged in.");
                return "login";
            }

//            Path filePath = Path.of(uploadDir, file.getOriginalFilename());
//            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save encrypted / uploaded file temporarily
            String uploadDir = System.getProperty("user.dir") + File.separator +"decrypted";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            File encFile = new File(uploadDir, encryptedFile.getOriginalFilename());
            encryptedFile.transferTo(encFile);
            String encFilename = encFile.getName();

            // Fetch metadata
            Optional<FileMeta> fileMetaOpt =
                    fileRepository.findByFilenameAndOwnerUsername(encFilename, username);
            Optional<SharedFile> sharedOpt =
                    Optional.ofNullable(sharedFileRepository.findByEncryptedFilenameAndRecipientUsername(encFilename, username));
            FileMeta fileMeta = null;

            // Determine if the file is shared or owned
            if (fileMetaOpt.isEmpty()){
                if(sharedOpt.isEmpty()) {
                    model.addAttribute("message2", "File metadata missing.");
                    return "dashboard";
                }
                else{
                    if (!username.equals(sharedOpt.get().getRecipientUsername())) {
                        model.addAttribute("message2", "You are not authorized to decrypt this file.");
                        return "dashboard";
                    }
                    // If shared, get metadata from original owner
                    fileMeta = fileRepository.findByFilenameAndOwnerUsername(
                            sharedOpt.get().getEncryptedFilename(), sharedOpt.get().getSenderUsername()).orElse(null);
                }
            }
            else{
                fileMeta = fileMetaOpt.get();
            }


            if (fileMeta == null) {
                model.addAttribute("message2", "File metadata missing.");
                return "dashboard";
            }

            // Retrieve correct user (sender) whose keys were used for encryption
            Optional<User> userOpt = userRepository.findByUsername(
                    fileMeta.getOwnerUsername());
            if (userOpt.isEmpty()) {
                model.addAttribute("message2", "User not found.");
                return "dashboard";
            }

            // Get the user object
            User user = userOpt.get();

            // Delegate to DecryptionService
            Map<String, String> result = decryptionService.decryptFile(
                    encFile.getName(),
                    uploadDir,
                    fileMeta.getEncryptedSymmetricKey(),
                    user.getPrivateKey(),
                    user.getNValue(),
                    Integer.parseInt(fileMeta.getEncryptionMode())
            );

            // Read the decrypted file content
            String decryptedFilePath = result.get("decryptedFile");
            File decryptedFile = new File(decryptedFilePath);
            if (decryptedFile.exists()) {
                String content = new String(java.nio.file.Files.readAllBytes(decryptedFile.toPath()));
                model.addAttribute("decryptedContent", content);
            } else {
                model.addAttribute("decryptedContent", "Decrypted file not found.");
            }

            model.addAttribute("message2", result.get("message"));
//            model.addAttribute("message2", decryptedFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message2", "Decryption failed: " + e.getMessage());
        }

        model.addAttribute("username", session.getAttribute("loggedInUsername"));
        return "dashboard";
    }

}
