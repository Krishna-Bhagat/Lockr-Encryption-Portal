package com.hades.hades_portal.controller;

import com.hades.hades_portal.service.EncryptionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Controller
public class EncryptionController {
    @Autowired
    private EncryptionService encryptionService;

    @PostMapping("/encrypt")
    public String encryptFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("symmetricKey") String rawPrivateKey,
            @RequestParam("rounds") int rounds,
            HttpSession session, Model model
    ) {
        try {
            String username = (String) session.getAttribute("loggedInUsername");
            if (username == null) {
                model.addAttribute("message1", "User not logged in");
                return "login";
            }
            // Save uploaded file temporarily
            String uploadDir = System.getProperty("user.dir") + File.separator + "encrypted";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            Path filePath = Path.of(uploadDir, file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Call encryption service
            Map<String, String> result = encryptionService.encryptFile(
                    file.getOriginalFilename(),
                    uploadDir,
                    rawPrivateKey,
                    rounds,
                    username
            );
            model.addAttribute("message1", "File encrypted successfully! -> " + result.get("encryptedFileName"));
            return "dashboard"; // Return the name of the HTML template for the dashboard

        } catch (Exception e) {
            model.addAttribute("message1", "Encryption failed: ->" + e.getMessage());
            return "dashboard"; // Return the name of the HTML template for the dashboard
        }
    }


//    @GetMapping("/dashboard")
//    public String dashboard() {
//        return "dashboard";  // Return the name of the HTML template for the dashboard
//    }


}
