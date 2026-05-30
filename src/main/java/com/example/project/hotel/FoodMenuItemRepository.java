package com.example.project.hotel;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodMenuItemRepository extends JpaRepository<FoodMenuItem, Long> {

    List<FoodMenuItem> findAllByOrderByMealAscNameAsc();

    List<FoodMenuItem> findByMealOrderByNameAsc(String meal);
}
