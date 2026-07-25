package com.comicvaultraiders.comicvaultraiders.service;

import com.comicvaultraiders.comicvaultraiders.entity.ComicBulkCreateQueue;
import com.comicvaultraiders.comicvaultraiders.repository.ComicBulkCreateQueueRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ComicBulkCreateQueueService {

    private final ComicBulkCreateQueueRepo comicBulkCreateQueueRepo;

    public ComicBulkCreateQueueService(ComicBulkCreateQueueRepo comicBulkCreateQueueRepo) {
        this.comicBulkCreateQueueRepo = comicBulkCreateQueueRepo;
    }

    public List<ComicBulkCreateQueue> findAll() {
        return comicBulkCreateQueueRepo.findAll(Sort.by("id"));
    }

    public boolean isKeyWordInQueue(String keyword){
        boolean alreadyExists = comicBulkCreateQueueRepo.existsByKeyword(keyword);
        return alreadyExists;
    }

    @Transactional
    public Optional<ComicBulkCreateQueue> putKeyWordInQueue(ComicBulkCreateQueue insertRow) {
        return Optional.of(comicBulkCreateQueueRepo.save(insertRow));
    }

    @Transactional
    public ComicBulkCreateQueue updateRowInQueue(Long queueId, Long newStartIndex) {
        return comicBulkCreateQueueRepo.findById(queueId)
                .map(row -> {
                    row.setStartIndex(newStartIndex);
                    return comicBulkCreateQueueRepo.save(row);
                })
                .orElseThrow(() -> new EntityNotFoundException("Row not found with id " + queueId));
    }

    @Transactional
    public void removeFromQueue(Long queueId){
        comicBulkCreateQueueRepo.deleteAllById(queueId);
    }

}
