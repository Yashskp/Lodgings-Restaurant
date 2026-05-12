package com.example.project.web;

import com.example.project.auth.AppUser;
import com.example.project.auth.AppUserRepository;
import com.example.project.auth.SignupForm;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        if (!model.containsAttribute("signupForm")) {
            model.addAttribute("signupForm", new SignupForm());
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String createAccount(
            @Valid @ModelAttribute SignupForm signupForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        String email = signupForm.getEmail().toLowerCase(Locale.ROOT).trim();
        if (userRepository.existsByEmail(email)) {
            bindingResult.rejectValue("email", "email.exists", "An account with this email already exists");
        }

        if (bindingResult.hasErrors()) {
            return "signup";
        }

        AppUser user = new AppUser();
        user.setFullName(signupForm.getFullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("signupSuccess", "Account created. Please login.");
        return "redirect:/login";
    }
}
