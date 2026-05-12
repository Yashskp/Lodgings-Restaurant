package com.example.project.web;

import com.example.project.auth.AppUser;
import com.example.project.auth.AppUserRepository;
import com.example.project.auth.UserRole;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Locale;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminUserController {

    private final AppUserRepository userRepository;

    public AdminUserController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/admin/users")
    public String users(Model model, Principal principal) {
        model.addAttribute("users", userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("currentEmail", principal.getName());
        return "admin-users";
    }

    @GetMapping("/admin/users/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserEditForm userEditForm = new UserEditForm();
        userEditForm.setFullName(user.getFullName());
        userEditForm.setEmail(user.getEmail());
        userEditForm.setRole(user.getRole());

        model.addAttribute("userId", user.getId());
        model.addAttribute("userEditForm", userEditForm);
        model.addAttribute("roles", UserRole.values());
        return "admin-user-edit";
    }

    @PostMapping("/admin/users/{id}/edit")
    public String updateUser(
            @PathVariable Long id,
            @Valid @ModelAttribute UserEditForm userEditForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String email = userEditForm.getEmail().toLowerCase(Locale.ROOT).trim();

        userRepository.findByEmail(email)
                .filter(existingUser -> !existingUser.getId().equals(id))
                .ifPresent(existingUser -> bindingResult.rejectValue(
                        "email",
                        "email.exists",
                        "Another user already uses this email"
                ));

        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", id);
            model.addAttribute("roles", UserRole.values());
            return "admin-user-edit";
        }

        user.setFullName(userEditForm.getFullName().trim());
        user.setEmail(email);
        user.setRole(userEditForm.getRole());
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("successMessage", "User updated successfully.");
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/role")
    public String updateRole(
            @PathVariable Long id,
            UserRole role,
            RedirectAttributes redirectAttributes
    ) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(role);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Role updated successfully.");
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(
            @PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getEmail().equalsIgnoreCase(principal.getName())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot delete your own admin account.");
            return "redirect:/admin/users";
        }

        userRepository.delete(user);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        return "redirect:/admin/users";
    }
}
