package com.comicvaultraiders.comicvaultraiders.jobs.daily;

import com.comicvaultraiders.comicvaultraiders.model.RateLimit;
import com.comicvaultraiders.comicvaultraiders.service.RateLimitService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RateLimitResetJob {

    private final RateLimitService rateLimitService;

    public RateLimitResetJob(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void resetAllRateLimit(){

        List<RateLimit> rateLimits = rateLimitService.findAll();
        rateLimits.forEach(rl -> {
            rl.setTraffic(0L);
            rateLimitService.updateRateLimit(rl);
        });
    }


}
