package com.example.project.ai;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_insights")
public class AiInsight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, length = 120)
    private String model;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected AiInsight() {
    }

    public AiInsight(String title, String content, String model) {
        this.title = title;
        this.content = content;
        this.model = model;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getModel() {
        return model;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
