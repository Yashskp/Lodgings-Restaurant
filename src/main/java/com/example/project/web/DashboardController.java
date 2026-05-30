package com.example.project.web;

import com.example.project.auth.AppUserRepository;
import com.example.project.hotel.CleaningStaff;
import com.example.project.hotel.CleaningStaffRepository;
import com.example.project.hotel.FoodMenuItem;
import com.example.project.hotel.FoodMenuItemRepository;
import com.example.project.hotel.HotelRoom;
import com.example.project.hotel.HotelRoomRepository;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DashboardController {

    private final AppUserRepository userRepository;
    private final HotelRoomRepository roomRepository;
    private final CleaningStaffRepository cleaningStaffRepository;
    private final FoodMenuItemRepository foodMenuItemRepository;

    public DashboardController(
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

    @GetMapping({"/dashboard", "/user"})
    public String dashboard(
            Principal principal,
            @RequestParam(defaultValue = "Normal") String roomType,
            @RequestParam(defaultValue = "AC") String cooling,
            Model model
    ) {
        model.addAttribute("email", principal.getName());
        userRepository.findByEmail(principal.getName())
                .ifPresent(user -> model.addAttribute("role", user.getRole()));

        boolean selectedAc = "AC".equalsIgnoreCase(cooling);
        List<HotelRoom> rooms = roomRepository.findAllByOrderByFloorAscNumberAsc();
        List<HotelRoom> matchingRooms = roomRepository.findByTypeIgnoreCaseAndAcOrderByFloorAscNumberAsc(roomType, selectedAc);
        List<CleaningStaff> cleaningStaff = cleaningStaffRepository.findAllByOrderByAreaAscNameAsc();
        List<FoodMenuItem> foodMenu = foodMenuItemRepository.findAllByOrderByMealAscNameAsc();
        long availableRooms = rooms.stream().filter(HotelRoom::isAvailable).count();

        model.addAttribute("roomTypes", List.of("Normal", "Medium", "Deluxe"));
        model.addAttribute("coolingOptions", List.of("AC", "Non AC"));
        model.addAttribute("mealTypes", List.of("Breakfast", "Lunch", "Dinner"));
        model.addAttribute("selectedRoomType", roomType);
        model.addAttribute("selectedCooling", cooling);
        model.addAttribute("rooms", rooms);
        model.addAttribute("matchingRooms", matchingRooms);
        model.addAttribute("totalRooms", rooms.size());
        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("occupiedRooms", rooms.size() - availableRooms);
        model.addAttribute("cleaningStaff", cleaningStaff);
        model.addAttribute("foodMenu", foodMenu);
        model.addAttribute("breakfastMenu", foodMenuItemRepository.findByMealOrderByNameAsc("Breakfast"));
        model.addAttribute("lunchMenu", foodMenuItemRepository.findByMealOrderByNameAsc("Lunch"));
        model.addAttribute("dinnerMenu", foodMenuItemRepository.findByMealOrderByNameAsc("Dinner"));
        return "dashboard";
    }

    @PostMapping("/user/rooms")
    public String addRoom(
            @RequestParam int number,
            @RequestParam int floor,
            @RequestParam String type,
            @RequestParam String cooling,
            @RequestParam int price,
            @RequestParam(defaultValue = "false") boolean available,
            RedirectAttributes redirectAttributes
    ) {
        if (roomRepository.existsByNumber(number)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Room number already exists.");
            return "redirect:/user";
        }

        HotelRoom room = new HotelRoom();
        room.setNumber(number);
        room.setFloor(floor);
        room.setType(type.trim());
        room.setAc("AC".equalsIgnoreCase(cooling));
        room.setPrice(price);
        room.setAvailable(available);
        roomRepository.save(room);

        redirectAttributes.addFlashAttribute("successMessage", "Room saved to database.");
        redirectAttributes.addAttribute("roomType", type);
        redirectAttributes.addAttribute("cooling", cooling);
        return "redirect:/user";
    }

    @PostMapping("/user/staff")
    public String addStaff(
            @RequestParam String name,
            @RequestParam String area,
            @RequestParam String shift,
            RedirectAttributes redirectAttributes
    ) {
        CleaningStaff staff = new CleaningStaff();
        staff.setName(name.trim());
        staff.setArea(area.trim());
        staff.setShift(shift.trim());
        cleaningStaffRepository.save(staff);

        redirectAttributes.addFlashAttribute("successMessage", "Cleaning staff saved to database.");
        return "redirect:/user";
    }

    @PostMapping("/user/menu")
    public String addMenuItem(
            @RequestParam String meal,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam int price,
            @RequestParam String photoUrl,
            RedirectAttributes redirectAttributes
    ) {
        FoodMenuItem item = new FoodMenuItem();
        item.setMeal(meal.trim());
        item.setName(name.trim());
        item.setDescription(description.trim());
        item.setPrice(price);
        item.setPhotoUrl(photoUrl.trim());
        foodMenuItemRepository.save(item);

        redirectAttributes.addFlashAttribute("successMessage", "Food menu item saved to database.");
        return "redirect:/user";
    }
}
