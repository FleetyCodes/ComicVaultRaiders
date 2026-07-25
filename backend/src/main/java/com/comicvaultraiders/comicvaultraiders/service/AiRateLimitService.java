package com.comicvaultraiders.comicvaultraiders.service;

import com.comicvaultraiders.comicvaultraiders.entity.AiRateLimit;
import com.comicvaultraiders.comicvaultraiders.repository.AiRateLimitRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AiRateLimitService {

    private final AiRateLimitRepo aiRateLimitRepo;

    public AiRateLimitService(AiRateLimitRepo aiRateLimitRepo) {
        this.aiRateLimitRepo = aiRateLimitRepo;
    }

    public List<AiRateLimit> findAll(){
        return aiRateLimitRepo.findAll();
    }

    public AiRateLimit findByUserId(Long userId){
        return aiRateLimitRepo.findByUserId(userId).
                orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));
    }

    @Transactional
    public Optional<AiRateLimit> createAiRateLimit(AiRateLimit aiRateLimit) {
        return Optional.of(aiRateLimitRepo.save(aiRateLimit));
    }


    @Transactional
    public AiRateLimit updateRateLimit(Long userId, Long traffic) {
        return aiRateLimitRepo.findByUserId(userId)
                .map(data -> {
                    data.setTraffic(traffic);
                    return aiRateLimitRepo.save(data);
                })
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));
    }

}
