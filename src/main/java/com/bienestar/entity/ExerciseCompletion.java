package com.bienestar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "exercise_completions")
public class ExerciseCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id", nullable = false)
    public String userId;

    @Column(nullable = false)
    public String tipo;

    @Column(nullable = false)
    public String categoria;

    @Column(name = "completed_at", nullable = false)
    public OffsetDateTime completedAt;

    public ExerciseCompletion() {}

    public ExerciseCompletion(String userId, String tipo, String categoria) {
        this.userId = userId;
        this.tipo = tipo;
        this.categoria = categoria;
        this.completedAt = OffsetDateTime.now();
    }
}
