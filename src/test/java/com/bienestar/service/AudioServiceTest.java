package com.bienestar.service;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AudioServiceTest {
    
    private final AudioService audioService = new AudioService();

    @Test
    void testGetSoundsCatalog() {
        List<String> catalog = audioService.getSoundsCatalog();
        assertNotNull(catalog);
        assertEquals(4, catalog.size());
        assertTrue(catalog.get(0).contains("Ruido Blanco"));
    }

    @Test
    void testGetBreathingExercises() {
        List<String> exercises = audioService.getBreathingExercises();
        assertNotNull(exercises);
        assertEquals(2, exercises.size());
        assertTrue(exercises.get(0).contains("4-7-8"));
    }
}
