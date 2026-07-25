package com.comicvaultraiders.comicvaultraiders.repository;

import com.comicvaultraiders.comicvaultraiders.entity.AiRateLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiRateLimitRepo extends JpaRepository<AiRateLimit, Long> {
    Optional<AiRateLimit> findByUserId(Long userId);


}
