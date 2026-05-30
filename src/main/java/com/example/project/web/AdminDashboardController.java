package com.example.project.web;

import com.example.project.auth.AppUserRepository;
import com.example.project.auth.UserRole;
import com.example.project.hotel.CleaningStaffRepository;
import com.example.project.hotel.FoodMenuItemRepository;
import com.example.project.hotel.HotelRoom;
import com.example.project.hotel.HotelRoomRepository;
import java.security.Principal;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

    private final AppUserRepository userRepository;
    private final HotelRoomRepository roomRepository;
    private final CleaningStaffRepository cleaningStaffRepository;
    private final FoodMenuItemRepository foodMenuItemRepository;

    public AdminDashboardController(
            AppUserRepository userRepository,
            HotelRoomRepository roomRepository,
            CleaningStaffRepository cleaningStaffRepository,
            FoodMenuItemRepository foodMenuItemRepository
    ) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.cleaningStaffRepository = cleaningStaffRepository;
        this.foodMenuItemRepository = foodMenuItemRepository;
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model, Principal principal) {
        long totalRooms = roomRepository.count();
        long availableRooms = roomRepository.findAll().stream()
                .filter(HotelRoom::isAvailable)
                .count();

        model.addAttribute("email", principal.getName());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("adminUsers", userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .count());
        model.addAttribute("customerUsers", userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.CUSTOMER)
                .count());
        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("bookedRooms", totalRooms - availableRooms);
        model.addAttribute("cleaningStaff", cleaningStaffRepository.count());
        model.addAttribute("menuItems", foodMenuItemRepository.count());
        model.addAttribute("recentUsers", userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .limit(5)
                .toList());
        model.addAttribute("recentRooms", roomRepository.findAllByOrderByFloorAscNumberAsc().stream()
                .limit(6)
                .toList());
        return "admin-dashboard";
    }
}
