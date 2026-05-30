package com.example.project.hotel;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CleaningStaffRepository extends JpaRepository<CleaningStaff, Long> {

    List<CleaningStaff> findAllByOrderByAreaAscNameAsc();
}
