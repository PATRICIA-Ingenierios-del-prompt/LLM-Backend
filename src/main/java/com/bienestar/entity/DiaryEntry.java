package com.bienestar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "diary_entries")
public class DiaryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id", nullable = false)
    public String userId;

    @Column
    public String mood;

    @Column(nullable = false)
    public String content;

    @Column(name = "created_at", nullable = false)
    public OffsetDateTime createdAt;

    public DiaryEntry() {}

    public DiaryEntry(String userId, String content, String mood) {
        this.userId = userId;
        this.content = content;
        this.mood = mood;
        this.createdAt = OffsetDateTime.now();
    }
}
