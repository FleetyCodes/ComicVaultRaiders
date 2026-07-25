package com.comicvaultraiders.comicvaultraiders.jobs.daily;

import com.comicvaultraiders.comicvaultraiders.entity.AiRateLimit;
import com.comicvaultraiders.comicvaultraiders.entity.RateLimit;
import com.comicvaultraiders.comicvaultraiders.service.AiRateLimitService;
import com.comicvaultraiders.comicvaultraiders.service.RateLimitService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RateLimitResetJob {

    private final RateLimitService rateLimitService;

    private final AiRateLimitService aiRateLimitService;

    public RateLimitResetJob(RateLimitService rateLimitService, AiRateLimitService aiRateLimitService) {
        this.rateLimitService = rateLimitService;
        this.aiRateLimitService = aiRateLimitService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void resetAllRateLimit(){
        List<RateLimit> rateLimits = rateLimitService.findAll();
        rateLimits.forEach(rl -> {
            rl.setTraffic(0L);
            rateLimitService.updateRateLimit(rl);
        });

        List<AiRateLimit> aiRateLimits = aiRateLimitService.findAll();
        aiRateLimits.forEach(aiRl -> {
            aiRateLimitService.updateRateLimit(aiRl.getUserId(), 0L);
        });
    }


}
