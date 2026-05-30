package com.example.project.config;

import com.example.project.auth.AppUser;
import com.example.project.auth.AppUserRepository;
import com.example.project.auth.UserRole;
import com.example.project.hotel.CleaningStaff;
import com.example.project.hotel.CleaningStaffRepository;
import com.example.project.hotel.FoodMenuItem;
import com.example.project.hotel.FoodMenuItemRepository;
import com.example.project.hotel.HotelRoom;
import com.example.project.hotel.HotelRoomRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final String ADMIN_EMAIL = "admin@lodgings.com";
    private static final String ADMIN_PASSWORD = "admin12345";

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HotelRoomRepository roomRepository;
    private final CleaningStaffRepository cleaningStaffRepository;
    private final FoodMenuItemRepository foodMenuItemRepository;

    public DataInitializer(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            HotelRoomRepository roomRepository,
            CleaningStaffRepository cleaningStaffRepository,
            FoodMenuItemRepository foodMenuItemRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roomRepository = roomRepository;
        this.cleaningStaffRepository = cleaningStaffRepository;
        this.foodMenuItemRepository = foodMenuItemRepository;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedRooms();
        seedCleaningStaff();
        seedFoodMenu();
    }

    private void seedAdmin() {
        String email = ADMIN_EMAIL.toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            return;
        }

        AppUser admin = new AppUser();
        admin.setFullName("System Administrator");
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);
    }

    private void seedRooms() {
        if (roomRepository.count() > 0) {
            return;
        }

        List<HotelRoom> rooms = List.of(
                room(101, 1, "Normal", false, 1200, true),
                room(102, 1, "Normal", true, 1700, true),
                room(103, 1, "Medium", false, 1900, false),
                room(104, 1, "Medium", true, 2500, true),
                room(105, 1, "Deluxe", false, 3200, true),
                room(106, 1, "Deluxe", true, 3900, false),
                room(107, 1, "Normal", false, 1200, true),
                room(201, 2, "Normal", true, 1800, false),
                room(202, 2, "Medium", false, 2000, true),
                room(203, 2, "Medium", true, 2700, true),
                room(204, 2, "Deluxe", false, 3400, true),
                room(205, 2, "Deluxe", true, 4200, true),
                room(206, 2, "Normal", false, 1300, true),
                room(207, 2, "Medium", true, 2700, false),
                room(301, 3, "Normal", true, 1900, true),
                room(302, 3, "Medium", false, 2100, true),
                room(303, 3, "Medium", true, 2900, false),
                room(304, 3, "Deluxe", false, 3600, true),
                room(305, 3, "Deluxe", true, 4500, true),
                room(306, 3, "Normal", false, 1400, false)
        );
        roomRepository.saveAll(rooms);
    }

    private void seedCleaningStaff() {
        if (cleaningStaffRepository.count() > 0) {
            return;
        }

        cleaningStaffRepository.saveAll(List.of(
                staff("Amit Sharma", "Floor 1", "Morning"),
                staff("Neha Verma", "Floor 1", "Evening"),
                staff("Rahul Singh", "Floor 2", "Morning"),
                staff("Priya Nair", "Floor 2", "Evening"),
                staff("Vikram Rao", "Floor 3", "Full day")
        ));
    }

    private void seedFoodMenu() {
        if (foodMenuItemRepository.count() > 0) {
            return;
        }

        foodMenuItemRepository.saveAll(List.of(
                menu("Breakfast", "Seasonal Fresh Fruit Platter", "Cut seasonal fruits served chilled with honey yogurt", 450, "https://images.unsplash.com/photo-1490474418585-ba9bad8fd0ea?auto=format&fit=crop&w=800&q=80"),
                menu("Breakfast", "House Granola Bowl", "Granola, berries, banana, nuts and natural yogurt", 520, "https://images.unsplash.com/photo-1511690656952-34342bb7c2f2?auto=format&fit=crop&w=800&q=80"),
                menu("Breakfast", "Brioche French Toast", "Golden brioche with maple syrup and caramelised banana", 575, "https://images.unsplash.com/photo-1484723091739-30a097e8f929?auto=format&fit=crop&w=800&q=80"),
                menu("Breakfast", "Vegetable Frittata", "Egg frittata with peppers, herbs and grilled tomato", 550, "https://images.unsplash.com/photo-1525351484163-7529414344d8?auto=format&fit=crop&w=800&q=80"),
                menu("Breakfast", "Taj Indian Breakfast", "Idli, vada, dosa, sambar, chutney and filter coffee", 650, "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?auto=format&fit=crop&w=800&q=80"),
                menu("Breakfast", "Bakery Basket", "Croissant, muffin, danish pastry, butter and preserves", 495, "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=800&q=80"),
                menu("Lunch", "Cream of Asparagus Soup", "Silky asparagus soup finished with parmesan crisp", 395, "https://images.unsplash.com/photo-1547592166-23ac45744acd?auto=format&fit=crop&w=800&q=80"),
                menu("Lunch", "Wild Mushroom Bruschetta", "Toasted sourdough with mushrooms, thyme and garlic", 425, "https://images.unsplash.com/photo-1505253716362-afaea1d3d1af?auto=format&fit=crop&w=800&q=80"),
                menu("Lunch", "Classic Caesar Salad", "Romaine lettuce, parmesan, croutons and caesar dressing", 725, "https://images.unsplash.com/photo-1550304943-4f24f54ddde9?auto=format&fit=crop&w=800&q=80"),
                menu("Lunch", "Healthy Millet Bowl", "Millets, avocado, roasted vegetables and lemon herb dressing", 675, "https://images.unsplash.com/photo-1543339308-43e59d6b73a6?auto=format&fit=crop&w=800&q=80"),
                menu("Lunch", "Arabic Cold Mezze Platter", "Hummus, baba ghanoush, tabbouleh, olives and pita", 725, "https://images.unsplash.com/photo-1541518763669-27fef04b14ea?auto=format&fit=crop&w=800&q=80"),
                menu("Lunch", "Chicken Biryani", "Basmati rice layered with spiced chicken and raita", 850, "https://images.unsplash.com/photo-1563379091339-03246963d51a?auto=format&fit=crop&w=800&q=80"),
                menu("Dinner", "Mini Tortellini Soup", "Pasta soup with roasted tomato and parmesan", 450, "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=800&q=80"),
                menu("Dinner", "Aattu Kaal Saaru", "South Indian mutton bone soup with house spices", 575, "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?auto=format&fit=crop&w=800&q=80"),
                menu("Dinner", "Piatto di Formaggi", "Selection of imported and Indian cheeses with crackers", 875, "https://images.unsplash.com/photo-1452195100486-9cc805987862?auto=format&fit=crop&w=800&q=80"),
                menu("Dinner", "Tandoori Platter", "Assorted kebabs, tikkas, grilled vegetables and chutneys", 950, "https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?auto=format&fit=crop&w=800&q=80"),
                menu("Dinner", "Butter Chicken", "Creamy tomato chicken curry served with naan", 825, "https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?auto=format&fit=crop&w=800&q=80"),
                menu("Dinner", "Gulab Jamun with Ice Cream", "Warm gulab jamun with vanilla ice cream", 375, "https://images.unsplash.com/photo-1605197161470-5d2a9af76c5b?auto=format&fit=crop&w=800&q=80")
        ));
    }

    private static HotelRoom room(int number, int floor, String type, boolean ac, int price, boolean available) {
        HotelRoom room = new HotelRoom();
        room.setNumber(number);
        room.setFloor(floor);
        room.setType(type);
        room.setAc(ac);
        room.setPrice(price);
        room.setAvailable(available);
        return room;
    }

    private static CleaningStaff staff(String name, String area, String shift) {
        CleaningStaff staff = new CleaningStaff();
        staff.setName(name);
        staff.setArea(area);
        staff.setShift(shift);
        return staff;
    }

    private static FoodMenuItem menu(String meal, String name, String description, int price, String photoUrl) {
        FoodMenuItem item = new FoodMenuItem();
        item.setMeal(meal);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setPhotoUrl(photoUrl);
        return item;
    }
}
