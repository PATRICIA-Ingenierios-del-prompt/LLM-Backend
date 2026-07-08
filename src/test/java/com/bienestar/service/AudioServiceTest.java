package com.bienestar.service;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AudioServiceTest {

    private final AudioService audioService = new AudioService();

    @Test
    void testGetSoundsCatalogNotNull() {
        List<String> catalog = audioService.getSoundsCatalog();
        assertNotNull(catalog);
    }

    @Test
    void testGetSoundsCatalogSize() {
        List<String> catalog = audioService.getSoundsCatalog();
        assertEquals(4, catalog.size());
    }

    @Test
    void testGetSoundsCatalogContainsRuidoBlanco() {
        List<String> catalog = audioService.getSoundsCatalog();
        assertTrue(catalog.get(0).contains("Ruido Blanco"));
    }

    @Test
    void testGetSoundsCatalogContainsAllItems() {
        List<String> catalog = audioService.getSoundsCatalog();
        assertTrue(catalog.get(1).contains("Ruido Marron"));
        assertTrue(catalog.get(2).contains("Lluvia"));
        assertTrue(catalog.get(3).contains("Bosque"));
    }

    @Test
    void testGetBreathingExercisesNotNull() {
        List<String> exercises = audioService.getBreathingExercises();
        assertNotNull(exercises);
    }

    @Test
    void testGetBreathingExercisesSize() {
        List<String> exercises = audioService.getBreathingExercises();
        assertEquals(2, exercises.size());
    }

    @Test
    void testGetBreathingExercisesContains478() {
        List<String> exercises = audioService.getBreathingExercises();
        assertTrue(exercises.get(0).contains("4-7-8"));
    }

    @Test
    void testGetBreathingExercisesContainsBoxBreathing() {
        List<String> exercises = audioService.getBreathingExercises();
        assertTrue(exercises.get(1).contains("Caja"));
    }
}

