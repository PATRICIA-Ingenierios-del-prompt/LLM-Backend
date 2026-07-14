package com.bienestar.service;

import com.bienestar.entity.DiaryEntry;
import com.bienestar.repository.DiaryEntryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class DiaryRepository {

    private final DiaryEntryRepository diaryEntryRepository;

    public DiaryRepository(DiaryEntryRepository diaryEntryRepository) {
        this.diaryEntryRepository = diaryEntryRepository;
    }

    public void saveEntry(String userId, String content, String mood) {
        diaryEntryRepository.save(new DiaryEntry(userId, content, mood));
    }
}
