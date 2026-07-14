package com.bienestar.service;

import com.bienestar.entity.DiaryEntry;
import com.bienestar.repository.DiaryEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiaryRepositoryTest {

    private DiaryEntryRepository diaryEntryRepository;
    private DiaryRepository diaryRepository;

    @BeforeEach
    void setUp() {
        diaryEntryRepository = mock(DiaryEntryRepository.class);
        diaryRepository = new DiaryRepository(diaryEntryRepository);
    }

    @Test
    void saveEntryPersistsEntryViaJpaRepository() {
        diaryRepository.saveEntry("student1", "Hoy me sentí bien", "Feliz");

        ArgumentCaptor<DiaryEntry> captor = ArgumentCaptor.forClass(DiaryEntry.class);
        verify(diaryEntryRepository, times(1)).save(captor.capture());

        DiaryEntry saved = captor.getValue();
        assertEquals("student1", saved.userId);
        assertEquals("Hoy me sentí bien", saved.content);
        assertEquals("Feliz", saved.mood);
        assertNotNull(saved.createdAt);
    }

    @Test
    void saveMultipleEntriesCallsRepositoryEachTime() {
        diaryRepository.saveEntry("student1", "Entrada 1", "Feliz");
        diaryRepository.saveEntry("student2", "Entrada 2", "Triste");

        verify(diaryEntryRepository, times(2)).save(any(DiaryEntry.class));
    }

    @Test
    void diaryEntryConstructorSetsFields() {
        DiaryEntry entry = new DiaryEntry("u1", "contenido", "Estresado");
        assertEquals("u1", entry.userId);
        assertEquals("contenido", entry.content);
        assertEquals("Estresado", entry.mood);
        assertNotNull(entry.createdAt);
    }

    @Test
    void diaryEntryDefaultConstructorWorks() {
        DiaryEntry entry = new DiaryEntry();
        assertNull(entry.userId);
    }
}
