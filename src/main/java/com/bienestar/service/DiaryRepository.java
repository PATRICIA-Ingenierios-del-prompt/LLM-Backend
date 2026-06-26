package com.bienestar.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DiaryRepository {

    private static final Logger log = LoggerFactory.getLogger(DiaryRepository.class);
    private static final String FILE_PATH = "diary_entries.json";
    private final ObjectMapper objectMapper;

    public DiaryRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static class DiaryEntry {
        public String userId;
        public String content;
        public String mood;
        public String timestamp;

        public DiaryEntry() {}

        public DiaryEntry(String userId, String content, String mood) {
            this.userId = userId;
            this.content = content;
            this.mood = mood;
            this.timestamp = LocalDateTime.now().toString();
        }
    }

    public void saveEntry(String userId, String content, String mood) {
        List<DiaryEntry> entries = loadEntries();
        entries.add(new DiaryEntry(userId, content, mood));

        try {
            objectMapper.writeValue(new File(FILE_PATH), entries);
        } catch (IOException e) {
            log.error("Error guardando el diario", e);
        }
    }

    private List<DiaryEntry> loadEntries() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            List<DiaryEntry> entries = objectMapper.readValue(file, new TypeReference<List<DiaryEntry>>() {});
            return entries != null ? entries : new ArrayList<>();
        } catch (IOException e) {
            log.error("Error cargando el diario", e);
            return new ArrayList<>();
        }
    }
}
