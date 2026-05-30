package com.example.project.ai;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiInsightRepository extends JpaRepository<AiInsight, Long> {

    List<AiInsight> findTop5ByOrderByCreatedAtDesc();
}
