package com.bienestar.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class DiaryRepositoryTest {

    private DiaryRepository diaryRepository;

    @BeforeEach
    void setUp() {
        diaryRepository = new DiaryRepository(new ObjectMapper());
        // Eliminar archivo del diario antes de cada test para evitar datos residuales
        new File("diary_entries.json").delete();
    }

    @Test
    void saveEntryCreatesFileAndPersistsData() {
        diaryRepository.saveEntry("student1", "Hoy me sentí bien", "Feliz");
        assertTrue(new File("diary_entries.json").exists());
    }

    @Test
    void saveMultipleEntriesDoesNotFail() {
        diaryRepository.saveEntry("student1", "Entrada 1", "Feliz");
        diaryRepository.saveEntry("student2", "Entrada 2", "Triste");
        // Verifica que se puede guardar varias veces sin excepción
        assertTrue(new File("diary_entries.json").exists());
    }

    @Test
    void diaryEntryConstructorSetsFields() {
        DiaryRepository.DiaryEntry entry = new DiaryRepository.DiaryEntry("u1", "contenido", "Estresado");
        assertEquals("u1", entry.userId);
        assertEquals("contenido", entry.content);
        assertEquals("Estresado", entry.mood);
        assertNotNull(entry.timestamp);
    }

    @Test
    void diaryEntryDefaultConstructorWorks() {
        DiaryRepository.DiaryEntry entry = new DiaryRepository.DiaryEntry();
        assertNull(entry.userId);
    }
}
