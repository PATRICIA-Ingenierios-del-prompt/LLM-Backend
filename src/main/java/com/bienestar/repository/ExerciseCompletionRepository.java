package com.bienestar.repository;

import com.bienestar.entity.ExerciseCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseCompletionRepository extends JpaRepository<ExerciseCompletion, Long> {

    long countByUserId(String userId);
}
