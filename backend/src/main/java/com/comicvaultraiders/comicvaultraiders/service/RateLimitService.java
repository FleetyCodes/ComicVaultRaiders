package com.comicvaultraiders.comicvaultraiders.service;

import com.comicvaultraiders.comicvaultraiders.model.ComicBulkCreateQueue;
import com.comicvaultraiders.comicvaultraiders.model.RateLimit;
import com.comicvaultraiders.comicvaultraiders.repository.RateLimitRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RateLimitService {

    private final RateLimitRepo rateLimitRepo;

    public RateLimitService(RateLimitRepo rateLimitRepo) {
        this.rateLimitRepo = rateLimitRepo;
    }

    public RateLimit findByApiName(String apiName){
        return rateLimitRepo.findByApiName(apiName);
    }

    public List<RateLimit> findAll(){
        return rateLimitRepo.findAll();
    }

    @Transactional
    public RateLimit updateRateLimit(RateLimit rateLimit) {
        return rateLimitRepo.findById(rateLimit.getId())
                .map(data -> {
                    data.setTraffic(rateLimit.getTraffic());
                    return rateLimitRepo.save(data);
                })
                .orElseThrow(() -> new EntityNotFoundException("Rate Limit not found with id " + rateLimit.getId()));
    }
}
