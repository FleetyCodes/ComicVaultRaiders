package com.comicvaultraiders.comicvaultraiders.repository;

import com.comicvaultraiders.comicvaultraiders.modell.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}