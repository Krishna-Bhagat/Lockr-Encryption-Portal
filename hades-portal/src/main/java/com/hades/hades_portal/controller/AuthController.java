package com.hades.hades_portal.controller;

import com.hades.hades_portal.crypto.RsaFunctionClass;
import com.hades.hades_portal.model.User;
import com.hades.hades_portal.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RsaFunctionClass rsaFunction;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().checkPassword(password)) {
            session.setAttribute("loggedInUsername", username);
            model.addAttribute("username", username);
            return "redirect:/dashboard"; // Redirect to the dashboard or home page
        } else {
            model.addAttribute("error", "Invalid credentials / User not found");
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register"; // Return the name of the Thymeleaf template
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                            @RequestParam String email,
                           Model model) {
        if (userRepository.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPublicKey("0");  // Will be set below
        user.setNValue("0");     // Will be set below
        user.setPrivateKey("0"); // Will be set below
        userRepository.save(user);

        rsaFunction.generateKeysIfNotExists(username); // sets public/private keys
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUsername");
        if (username == null) {
            return "redirect:/login"; // or handle unauthorized access
        }

        model.addAttribute("username", username);
        return "dashboard"; // This is your Thymeleaf view that includes the encrypt form
        }
}
