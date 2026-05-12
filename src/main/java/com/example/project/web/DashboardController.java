package com.example.project.web;

import com.example.project.auth.AppUserRepository;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final AppUserRepository userRepository;

    public DashboardController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        model.addAttribute("email", principal.getName());
        userRepository.findByEmail(principal.getName())
                .ifPresent(user -> model.addAttribute("role", user.getRole()));
        return "dashboard";
    }
}
