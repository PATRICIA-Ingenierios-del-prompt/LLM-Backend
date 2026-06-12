package com.bienestar.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DiaryRepository {

    private static final String FILE_PATH = "diary_entries.json";
    private final Gson gson = new Gson();

    public static class DiaryEntry {
        public String userId;
        public String content;
        public String mood;
        public String timestamp;

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

        try (FileWriter writer = new FileWriter(new File(FILE_PATH))) {
            gson.toJson(entries, writer);
        } catch (IOException e) {
            System.err.println("Error guardando el diario: " + e.getMessage());
        }
    }

    private List<DiaryEntry> loadEntries() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<DiaryEntry>>(){}.getType();
            List<DiaryEntry> entries = gson.fromJson(reader, listType);
            return entries != null ? entries : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
