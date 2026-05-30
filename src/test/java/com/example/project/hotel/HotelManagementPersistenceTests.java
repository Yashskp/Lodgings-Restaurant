package com.example.project.hotel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class HotelManagementPersistenceTests {

    @Autowired
    private HotelRoomRepository roomRepository;

    @Autowired
    private CleaningStaffRepository cleaningStaffRepository;

    @Autowired
    private FoodMenuItemRepository foodMenuItemRepository;

    @Test
    void savesUserEnteredHotelDetails() {
        HotelRoom room = new HotelRoom();
        room.setNumber(909);
        room.setFloor(3);
        room.setType("Deluxe");
        room.setAc(true);
        room.setPrice(5200);
        room.setAvailable(true);
        roomRepository.save(room);

        CleaningStaff staff = new CleaningStaff();
        staff.setName("Test Staff");
        staff.setArea("Floor 3");
        staff.setShift("Night");
        cleaningStaffRepository.save(staff);

        FoodMenuItem item = new FoodMenuItem();
        item.setMeal("Dinner");
        item.setName("Test Dinner");
        item.setDescription("Saved from user page form");
        item.setPrice(999);
        item.setPhotoUrl("https://example.com/test-dinner.jpg");
        foodMenuItemRepository.save(item);

        assertThat(roomRepository.existsByNumber(909)).isTrue();
        assertThat(cleaningStaffRepository.findAllByOrderByAreaAscNameAsc())
                .anySatisfy(savedStaff -> assertThat(savedStaff.getName()).isEqualTo("Test Staff"));
        assertThat(foodMenuItemRepository.findByMealOrderByNameAsc("Dinner"))
                .anySatisfy(savedItem -> assertThat(savedItem.getName()).isEqualTo("Test Dinner"));
    }
}
