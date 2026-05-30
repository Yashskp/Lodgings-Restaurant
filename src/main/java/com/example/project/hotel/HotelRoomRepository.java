package com.example.project.hotel;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRoomRepository extends JpaRepository<HotelRoom, Long> {

    List<HotelRoom> findAllByOrderByFloorAscNumberAsc();

    List<HotelRoom> findByTypeIgnoreCaseAndAcOrderByFloorAscNumberAsc(String type, boolean ac);

    boolean existsByNumber(int number);
}
