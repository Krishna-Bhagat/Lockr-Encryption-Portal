package com.hades.hades_portal.controller;

import com.hades.hades_portal.model.SharedFile;
import com.hades.hades_portal.repository.SharedFileRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

@Controller
public class ShareController {
    @Autowired
    private SharedFileRepository sharedFileRepository;

    @PostMapping("/share")
    public String handleFileShare(
            @RequestParam("encryptedFile") MultipartFile file,
            @RequestParam("recipientUsername") String recipientUsername,
            HttpSession session,
            Model model
    ) {
        try {
            String senderUsername = (String) session.getAttribute("loggedInUsername");
            if (senderUsername == null) {
                model.addAttribute("message3", "You must be logged in to share files.");
                return "dashboard";
            }

            // Save file to uploads directory
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) uploadFolder.mkdirs();

            Path filePath = Path.of(uploadDir, file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save sharing info to database
            SharedFile sharedFile = new SharedFile();
            sharedFile.setEncryptedFilename(file.getOriginalFilename());
            sharedFile.setSenderUsername(senderUsername);
            sharedFile.setRecipientUsername(recipientUsername);
            sharedFile.setDirectory(uploadDir);
            sharedFile.setSharedAt(LocalDateTime.now());

            sharedFileRepository.save(sharedFile);

            model.addAttribute("message3", "File shared successfully.");
        } catch (Exception e) {
            model.addAttribute("message3", "Error while sharing file: " + e.getMessage());
            e.printStackTrace();
        }

        model.addAttribute("username", session.getAttribute("loggedInUsername"));
        return "dashboard";
    }

}
